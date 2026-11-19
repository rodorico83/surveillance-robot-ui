package pfc.video;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import pfc.gui.client.PlayerWindow;

/**
 * Simple TCP video receiver for OpenCV frames.
 */
public class VideoReceive implements VideoSession {

    private final String host;
    private final int port;
    private final PlayerWindow playerWindow;

    private Socket socket;
    private Thread receiverThread;
    private volatile boolean running;

    private Logger log = Logger.getLogger("pfc.video");

    public VideoReceive(String host, String port, PlayerWindow playerWindow) {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.playerWindow = playerWindow;
    }

    public VideoReceive(String host, String port) {
        this(host, port, null);
    }

    public boolean initialize() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 3000);
        socket.setTcpNoDelay(true);
        socket.setReceiveBufferSize(64 * 1024);
        running = true;

        receiverThread = new Thread(new Runnable() {
            public void run() {
                receiveFrames();
            }
        }, "pfc-video-receive");
        receiverThread.start();
        return true;
    }

    public boolean isDone() {
        return playerWindow == null;
    }

    public void close() {
        running = false;
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.fine("Ignoring socket close error: " + e.getMessage());
            }
            socket = null;
        }
    }

    private void receiveFrames() {
        DataInputStream input = null;

        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            while (running) {
                byte[] payload = readLatestFramePayload(input);
                if (payload == null) {
                    continue;
                }

                BufferedImage frame = ImageIO.read(new java.io.ByteArrayInputStream(payload));
                if (frame == null || playerWindow == null) {
                    continue;
                }

                playerWindow.updateFrame(frame);
            }
        } catch (IOException e) {
            if (running) {
                log.warning("Video receive error: " + e.getMessage());
                if (playerWindow != null) {
                    playerWindow.closeRTP();
                }
            }
        } finally {
            running = false;
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.fine("Ignoring stream close error: " + e.getMessage());
                }
            }
        }
    }

    private byte[] readLatestFramePayload(DataInputStream input) throws IOException {
        int length = input.readInt();
        if (length <= 0) {
            return null;
        }

        byte[] payload = new byte[length];
        input.readFully(payload);

        while (running && input.available() >= 4) {
            input.mark(input.available() + 4);
            int nextLength = input.readInt();
            if (nextLength <= 0) {
                continue;
            }

            if (input.available() < nextLength) {
                input.reset();
                break;
            }

            payload = new byte[nextLength];
            input.readFully(payload);
        }

        return payload;
    }
}
