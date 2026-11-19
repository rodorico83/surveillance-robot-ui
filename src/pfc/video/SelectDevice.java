package pfc.video;

import java.util.Vector;
import java.util.logging.Logger;

import org.opencv.videoio.VideoCapture;

/**
 * Select Video Device Implementation.
 */
public class SelectDevice {

    private static Logger log = Logger.getLogger("pcf.video");

    public static Vector<String> lookUpDevices() {
        OpenCvUtils.ensureLoaded();

        Vector<String> result = new Vector<String>();
        for (int index = 0; index < 10; index++) {
            VideoCapture capture = new VideoCapture(index);
            if (capture.isOpened()) {
                result.add("Camera " + index);
                log.info("capture video device found: Camera " + index);
                capture.release();
            }
        }
        return result;
    }
}
