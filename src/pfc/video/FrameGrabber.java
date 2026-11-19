package pfc.video;

import org.opencv.core.Mat;

/**
 * Provides access to a live frame source.
 */
public interface FrameGrabber {

    Mat getFrameCopy();
}
