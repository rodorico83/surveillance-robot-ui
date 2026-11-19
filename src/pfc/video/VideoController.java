package pfc.video;

import java.io.IOException;
import java.util.logging.Logger;

import org.opencv.core.Mat;

/**
 * Video controller implementation.
 */
public class VideoController {

    private Logger log = Logger.getLogger("pcf.video");
    private static VideoTransmit videoTransmit;

    public VideoController() {
        super();
    }

    public VideoController(String device, String rtpport) throws IOException {
        log.entering("VideoController", "VideoController");

        videoTransmit = new VideoTransmit(device, rtpport);
        String result = videoTransmit.start();

        if (result != null) {
            log.severe("Error : " + result);
            throw new IOException(result);
        }
    }

    public static void stopVideoTransmitter() {
        if (videoTransmit != null) {
            videoTransmit.stop();
            videoTransmit = null;
        }
    }

    public static void requestVideoSourceReconnect() {
        if (videoTransmit != null) {
            videoTransmit.requestReconnect();
        }
    }

    public static Mat getCurrentFrameClone() {
        if (videoTransmit == null) {
            return new Mat();
        }
        return videoTransmit.getFrameCopy();
    }
}
