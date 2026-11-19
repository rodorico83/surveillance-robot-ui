package pfc.video;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import pfc.gui.client.PlayerWindow;

/**
 * Direct local video receiver for standalone mode.
 */
public class LocalVideoReceive implements VideoSession {

    private static final Logger LOG = Logger.getLogger("pfc.video");
    private static final int STREAM_OPEN_TIMEOUT_MS = 4000;
    private static final int STREAM_READ_TIMEOUT_MS = 4000;
    private static final int STREAM_MAX_READ_FAILURES = 3;
    private static final long STREAM_STALE_FRAME_REOPEN_MS = 6000L;

    private final PlayerWindow playerWindow;
    private final String source;
    private final boolean directStreamSource;
    private Thread receiverThread;
    private volatile boolean running;
    private final Object captureLock = new Object();
    private VideoCapture capture;

    public LocalVideoReceive(PlayerWindow playerWindow) {
        this(playerWindow, null);
    }

    public LocalVideoReceive(PlayerWindow playerWindow, String source) {
        this.playerWindow = playerWindow;
        this.source = source == null ? "" : source.trim();
        this.directStreamSource = isStreamSource(this.source);
    }

    public boolean initialize() {
        if (directStreamSource) {
            OpenCvUtils.ensureLoaded();
        }
        running = true;
        receiverThread = new Thread(new Runnable() {
            public void run() {
                receiveFrames();
            }
        }, "pfc-local-video-receive");
        receiverThread.start();
        return true;
    }

    public void close() {
        running = false;
        if (receiverThread != null) {
            receiverThread.interrupt();
        }

        if (directStreamSource && receiverThread != null) {
            final Thread threadToJoin = receiverThread;
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
            }, "pfc-local-video-cleanup");
            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }
        else {
            releaseCapture();
        }
    }

    private void receiveFrames() {
        if (directStreamSource) {
            receiveFramesFromStream();
            return;
        }

        while (running) {
            Mat frame = VideoController.getCurrentFrameClone();
            try {
                if (frame.empty()) {
                    Thread.sleep(30);
                    continue;
                }

                BufferedImage image = OpenCvUtils.matToBufferedImage(frame);
                if (image != null && playerWindow != null) {
                    playerWindow.updateFrame(image);
                }

                Thread.sleep(33);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (RuntimeException e) {
                LOG.warning("Local video receive error: " + e.getMessage());
                if (playerWindow != null) {
                    playerWindow.closeRTP();
                }
                break;
            } finally {
                frame.release();
            }
        }
    }

    private void receiveFramesFromStream() {
        Mat frame = new Mat();
        int consecutiveReadFailures = 0;
        long lastFrameReadAt = 0L;

        while (running) {
            VideoCapture activeCapture = getOrOpenCapture();
            try {
                if (activeCapture == null) {
                    Thread.sleep(500);
                    continue;
                }

                boolean readOk = activeCapture.read(frame);
                if (!running) {
                    break;
                }
                if (!readOk || frame.empty()) {
                    consecutiveReadFailures++;
                    if (shouldReopenStream(consecutiveReadFailures, lastFrameReadAt)) {
                        resetCapture("No frames received from direct standalone stream.");
                        consecutiveReadFailures = 0;
                    }
                    Thread.sleep(150);
                    continue;
                }

                consecutiveReadFailures = 0;
                lastFrameReadAt = System.currentTimeMillis();

                BufferedImage image = OpenCvUtils.matToBufferedImage(frame);
                if (image != null && playerWindow != null) {
                    playerWindow.updateFrame(image);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable e) {
                if (!running) {
                    break;
                }
                LOG.warning("Direct standalone stream error: " + e.getMessage());
                resetCapture("Runtime exception on direct standalone stream.");
            } finally {
                frame.release();
                frame = new Mat();
            }
        }

        releaseCapture();
        frame.release();
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

            VideoCapture openedCapture = new VideoCapture();
            if (!openedCapture.open(source, Videoio.CAP_FFMPEG)) {
                openedCapture.release();
                LOG.warning("Couldn't open direct standalone stream source " + source);
                return null;
            }

            openedCapture.set(Videoio.CAP_PROP_BUFFERSIZE, 1);
            openedCapture.set(Videoio.CAP_PROP_OPEN_TIMEOUT_MSEC, STREAM_OPEN_TIMEOUT_MS);
            openedCapture.set(Videoio.CAP_PROP_READ_TIMEOUT_MSEC, STREAM_READ_TIMEOUT_MS);
            openedCapture.set(Videoio.CAP_PROP_N_THREADS, 1);

            capture = openedCapture;
            LOG.info("Direct standalone stream ready: " + source);
            return capture;
        }
    }

    private void resetCapture(String reason) {
        synchronized (captureLock) {
            if (capture != null) {
                LOG.warning(reason + " Reopening direct standalone stream " + source);
                capture.release();
                capture = null;
            }
        }
    }

    private boolean shouldReopenStream(int consecutiveReadFailures, long lastFrameReadAt) {
        if (consecutiveReadFailures >= STREAM_MAX_READ_FAILURES) {
            return true;
        }

        return lastFrameReadAt > 0L
                && System.currentTimeMillis() - lastFrameReadAt >= STREAM_STALE_FRAME_REOPEN_MS;
    }

    private boolean isStreamSource(String value) {
        return value != null && value.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*");
    }

    private void releaseCapture() {
        synchronized (captureLock) {
            if (capture != null) {
                capture.release();
                capture = null;
            }
        }
    }
}
