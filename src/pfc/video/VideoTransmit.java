package pfc.video;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * OpenCV-backed video transmitter.
 */
public class VideoTransmit implements FrameGrabber {

    private static final Logger LOG = Logger.getLogger("pcf.video");
    private static final int JPEG_QUALITY = 45;
    private static final double MAX_STREAM_WIDTH = 480.0;
    private static final double MAX_STREAM_HEIGHT = 360.0;
    private static final int SEND_FRAME_INTERVAL_MS = 40;
    private static final int STREAM_OPEN_TIMEOUT_MS = 4000;
    private static final int STREAM_READ_TIMEOUT_MS = 4000;
    private static final int STREAM_MAX_READ_FAILURES = 3;
    private static final long STREAM_STALE_FRAME_REOPEN_MS = 6000L;

    private final int cameraIndex;
    private final String source;
    private final boolean streamSource;
    private final int port;
    private final List<ClientConnection> clients = new ArrayList<ClientConnection>();

    private VideoCapture capture;
    private ServerSocket serverSocket;
    private Thread captureThread;
    private Thread acceptThread;
    private Thread sendThread;
    private volatile boolean running;
    private final Object frameLock = new Object();
    private final Object captureLock = new Object();
    private final Object encodedFrameLock = new Object();
    private Mat latestFrame = new Mat();
    private byte[] latestEncodedFrame;
    private long latestEncodedVersion;
    private volatile boolean reconnectRequested;

    public VideoTransmit(String device, String port) {
        this.source = device == null ? "" : device.trim();
        this.streamSource = isStreamSource(this.source);
        this.cameraIndex = parseCameraIndex(this.source);
        this.port = Integer.parseInt(port);
    }

    public synchronized String start() throws IOException {
        OpenCvUtils.ensureLoaded();

        if (source.length() == 0) {
            return "No video source configured.";
        }

        if (!streamSource) {
            capture = openCapture();
            if (!capture.isOpened()) {
                return "Couldn't open video source " + describeSource();
            }
            configureLocalCapture(capture);
        }

        serverSocket = new ServerSocket(port);
        running = true;

        startAcceptLoop();
        startCaptureLoop();
        startSendLoop();
        return null;
    }

    public synchronized void stop() {
        running = false;

        if (acceptThread != null) {
            acceptThread.interrupt();
        }
        if (captureThread != null) {
            captureThread.interrupt();
        }
        if (sendThread != null) {
            sendThread.interrupt();
        }

        closeServerSocket();
        closeClients();
        scheduleCaptureCleanup();

        synchronized (frameLock) {
            if (latestFrame != null) {
                latestFrame.release();
                latestFrame = new Mat();
            }
        }
        clearLatestEncodedFrame();
    }

    public Mat getFrameCopy() {
        synchronized (frameLock) {
            if (latestFrame == null || latestFrame.empty()) {
                return new Mat();
            }
            return latestFrame.clone();
        }
    }

    public static Mat getDataOutputClon() {
        return VideoController.getCurrentFrameClone();
    }

    private void startAcceptLoop() {
        acceptThread = new Thread(new Runnable() {
            public void run() {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        socket.setTcpNoDelay(true);
                        socket.setSendBufferSize(64 * 1024);
                        synchronized (clients) {
                            clients.add(new ClientConnection(socket));
                        }
                        LOG.info("Video client connected from " + socket.getRemoteSocketAddress());
                    } catch (IOException e) {
                        if (running) {
                            LOG.warning("Error accepting video client: " + e.getMessage());
                        }
                    }
                }
            }
        }, "pfc-video-accept");
        acceptThread.start();
    }

    private void startCaptureLoop() {
        captureThread = new Thread(new Runnable() {
            public void run() {
                Mat frame = new Mat();
                Mat encodedFrameSource = new Mat();
                MatOfByte encodedFrame = new MatOfByte();
                MatOfInt jpegParams = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, JPEG_QUALITY);
                int consecutiveReadFailures = 0;
                long lastFrameReadAt = 0L;

                while (running) {
                    try {
                        if (streamSource && reconnectRequested) {
                            resetCapture("Reconnect requested for late-start video stream.");
                            reconnectRequested = false;
                        }

                        VideoCapture activeCapture = getOrOpenCapture();
                        if (activeCapture == null) {
                            try {
                                Thread.sleep(streamSource ? 1000 : 100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                            continue;
                        }

                        boolean readOk = activeCapture.read(frame);
                        if (!running) {
                            break;
                        }
                        if (!readOk || frame.empty()) {
                            consecutiveReadFailures++;
                            if (streamSource && shouldReopenStream(consecutiveReadFailures, lastFrameReadAt)) {
                                resetCapture("No frames received from stream.");
                                consecutiveReadFailures = 0;
                                clearLatestFrame();
                            }
                            try {
                                Thread.sleep(streamSource ? 300 : 100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                            continue;
                        }

                        consecutiveReadFailures = 0;
                        lastFrameReadAt = System.currentTimeMillis();

                        synchronized (frameLock) {
                            if (latestFrame != null) {
                                latestFrame.release();
                            }
                            latestFrame = frame.clone();
                        }

                        prepareFrameForStreaming(frame, encodedFrameSource);
                        Imgcodecs.imencode(".jpg", encodedFrameSource, encodedFrame, jpegParams);
                        storeLatestEncodedFrame(encodedFrame.toArray());
                    } catch (RuntimeException e) {
                        if (!running) {
                            break;
                        }
                        LOG.warning("Video capture error: " + e.getMessage());
                        resetCapture("Runtime exception on video source.");
                    }
                }

                releaseCapture();
                frame.release();
                encodedFrameSource.release();
                encodedFrame.release();
                jpegParams.release();
            }
        }, "pfc-video-capture");
        captureThread.start();
    }

    private void startSendLoop() {
        sendThread = new Thread(new Runnable() {
            public void run() {
                long lastSentVersion = -1L;

                while (running) {
                    EncodedFrameSnapshot snapshot = getLatestEncodedFrameSnapshot();
                    if (snapshot == null || snapshot.version == lastSentVersion) {
                        sleepQuietly(10);
                        continue;
                    }

                    broadcast(snapshot.data);
                    lastSentVersion = snapshot.version;
                    sleepQuietly(SEND_FRAME_INTERVAL_MS);
                }
            }
        }, "pfc-video-send");
        sendThread.start();
    }

    private void broadcast(byte[] jpegBytes) {
        synchronized (clients) {
            Iterator<ClientConnection> iterator = clients.iterator();
            while (iterator.hasNext()) {
                ClientConnection client = iterator.next();
                try {
                    client.output.writeInt(jpegBytes.length);
                    client.output.write(jpegBytes);
                    client.output.flush();
                } catch (IOException e) {
                    closeQuietly(client);
                    iterator.remove();
                }
            }
        }
    }

    private void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOG.warning("Error closing video server socket: " + e.getMessage());
            }
            serverSocket = null;
        }
    }

    private void closeClients() {
        synchronized (clients) {
            Iterator<ClientConnection> iterator = clients.iterator();
            while (iterator.hasNext()) {
                closeQuietly(iterator.next());
                iterator.remove();
            }
        }
    }

    private void closeQuietly(ClientConnection client) {
        try {
            client.socket.close();
        } catch (IOException e) {
            LOG.fine("Ignoring socket close error: " + e.getMessage());
        }
    }

    private VideoCapture openCapture() {
        if (streamSource) {
            VideoCapture streamCapture = new VideoCapture();
            if (!OpenCvUtils.hasFfmpegRuntime()) {
                LOG.warning("FFmpeg runtime DLL not found. RTSP/HTTP streams usually require opencv_videoio_ffmpeg*.dll deployed with the application.");
            }

            if (!streamCapture.open(source, Videoio.CAP_FFMPEG)) {
                streamCapture.release();
                return streamCapture;
            }
            configureStreamCapture(streamCapture);
            return streamCapture;
        }
        return new VideoCapture(cameraIndex);
    }

    private void configureLocalCapture(VideoCapture localCapture) {
        localCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        localCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        localCapture.set(Videoio.CAP_PROP_FPS, 15);
    }

    private void configureStreamCapture(VideoCapture streamCapture) {
        streamCapture.set(Videoio.CAP_PROP_BUFFERSIZE, 1);
        streamCapture.set(Videoio.CAP_PROP_OPEN_TIMEOUT_MSEC, STREAM_OPEN_TIMEOUT_MS);
        streamCapture.set(Videoio.CAP_PROP_READ_TIMEOUT_MSEC, STREAM_READ_TIMEOUT_MS);
        streamCapture.set(Videoio.CAP_PROP_N_THREADS, 1);
    }

    private VideoCapture getOrOpenCapture() {
        synchronized (captureLock) {
            if (!running) {
                return null;
            }

            if (capture != null && capture.isOpened()) {
                return capture;
            }

            if (capture != null) {
                capture.release();
                capture = null;
            }

            VideoCapture openedCapture = openCapture();
            if (!openedCapture.isOpened()) {
                openedCapture.release();
                LOG.warning("Couldn't open video source " + describeSource());
                return null;
            }

            if (!streamSource) {
                configureLocalCapture(openedCapture);
            }

            capture = openedCapture;
            LOG.info("Video source ready: " + describeSource());
            return capture;
        }
    }

    private int parseCameraIndex(String device) {
        if (device == null) {
            return 0;
        }

        String trimmed = device.trim();
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            int lastSpace = trimmed.lastIndexOf(' ');
            if (lastSpace >= 0 && lastSpace + 1 < trimmed.length()) {
                try {
                    return Integer.parseInt(trimmed.substring(lastSpace + 1));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0;
    }

    private boolean isStreamSource(String value) {
        return value != null && value.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*");
    }

    private String describeSource() {
        if (streamSource) {
            return source;
        }
        return "camera " + cameraIndex;
    }

    private boolean shouldReopenStream(int consecutiveReadFailures, long lastFrameReadAt) {
        if (!streamSource) {
            return false;
        }

        if (consecutiveReadFailures >= STREAM_MAX_READ_FAILURES) {
            return true;
        }

        return lastFrameReadAt > 0L
                && System.currentTimeMillis() - lastFrameReadAt >= STREAM_STALE_FRAME_REOPEN_MS;
    }

    private void resetCapture(String reason) {
        synchronized (captureLock) {
            if (capture != null) {
                LOG.warning(reason + " Reopening " + describeSource());
                capture.release();
                capture = null;
            }
        }
    }

    public void requestReconnect() {
        if (!streamSource) {
            return;
        }

        reconnectRequested = true;
        clearLatestFrame();
        clearLatestEncodedFrame();
        LOG.info("Video stream reconnect requested for " + describeSource());
    }

    private void clearLatestFrame() {
        synchronized (frameLock) {
            if (latestFrame != null) {
                latestFrame.release();
            }
            latestFrame = new Mat();
        }
        clearLatestEncodedFrame();
    }

    private void prepareFrameForStreaming(Mat sourceFrame, Mat targetFrame) {
        if (sourceFrame.cols() <= MAX_STREAM_WIDTH && sourceFrame.rows() <= MAX_STREAM_HEIGHT) {
            sourceFrame.copyTo(targetFrame);
            return;
        }

        double scale = Math.min(MAX_STREAM_WIDTH / sourceFrame.cols(), MAX_STREAM_HEIGHT / sourceFrame.rows());
        Size targetSize = new Size(
                Math.max(1, Math.round(sourceFrame.cols() * scale)),
                Math.max(1, Math.round(sourceFrame.rows() * scale)));
        Imgproc.resize(sourceFrame, targetFrame, targetSize, 0.0, 0.0, Imgproc.INTER_AREA);
    }

    private void storeLatestEncodedFrame(byte[] jpegBytes) {
        synchronized (encodedFrameLock) {
            latestEncodedFrame = jpegBytes;
            latestEncodedVersion++;
        }
    }

    private EncodedFrameSnapshot getLatestEncodedFrameSnapshot() {
        synchronized (encodedFrameLock) {
            if (latestEncodedFrame == null || latestEncodedFrame.length == 0) {
                return null;
            }
            return new EncodedFrameSnapshot(latestEncodedFrame, latestEncodedVersion);
        }
    }

    private void clearLatestEncodedFrame() {
        synchronized (encodedFrameLock) {
            latestEncodedFrame = null;
            latestEncodedVersion = 0L;
        }
    }

    private void scheduleCaptureCleanup() {
        if (captureThread != null) {
            final Thread threadToJoin = captureThread;
            Thread cleanupThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        threadToJoin.join(STREAM_READ_TIMEOUT_MS + 500L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        releaseCapture();
                    }
                }
            }, "pfc-video-capture-cleanup");
            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }
        else {
            releaseCapture();
        }
    }

    private void releaseCapture() {
        synchronized (captureLock) {
            if (capture != null) {
                capture.release();
                capture = null;
            }
        }
    }

    private void sleepQuietly(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static final class EncodedFrameSnapshot {
        private final byte[] data;
        private final long version;

        private EncodedFrameSnapshot(byte[] data, long version) {
            this.data = data;
            this.version = version;
        }
    }

    private static final class ClientConnection {
        private final Socket socket;
        private final DataOutputStream output;

        private ClientConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }
    }
}
