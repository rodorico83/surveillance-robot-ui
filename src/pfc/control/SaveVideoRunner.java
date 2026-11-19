package pfc.control;

import java.io.IOException;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoWriter;

import pfc.control.constants.Operations;
import pfc.gui.ApplicationRunner;

/**
 * Save Video Runner Implementation.
 */
public class SaveVideoRunner implements Runnable {

    private Logger log = Logger.getLogger("pcf.control");

    private boolean isRecording;
    private String saveVideoPath;
    private Controller controller;

    public SaveVideoRunner(Controller controller) {
        super();
        this.isRecording = false;
        this.saveVideoPath = null;
        this.controller = controller;
    }

    public void run() {
        if (!isRecording && saveVideoPath != null) {
            isRecording = true;

            try {
                Controller.sendOperation(Operations.ENABLE_CAM);
            } catch (IOException e) {
                log.severe(e.getMessage());
                e.printStackTrace();
            }

            VideoWriter writer = null;
            try {
                Mat firstFrame = waitForFrame();
                if (firstFrame.empty()) {
                    throw new IOException("Unable to capture frames from the camera.");
                }

                writer = new VideoWriter(
                        saveVideoPath,
                        VideoWriter.fourcc('M', 'J', 'P', 'G'),
                        15.0,
                        firstFrame.size());

                if (!writer.isOpened()) {
                    throw new IOException("Unable to open video writer for " + saveVideoPath);
                }

                long startTime = System.currentTimeMillis();
                log.info("Starting recording ...");

                while (shouldKeepRecording(startTime)) {
                    Mat frame = pfc.video.VideoTransmit.getDataOutputClon();
                    if (!frame.empty()) {
                        writer.write(frame);
                    }
                    frame.release();
                    Thread.sleep(66);
                }

                firstFrame.release();
                log.info("Recording done saved to " + saveVideoPath);
            } catch (Exception e) {
                log.severe(e.getMessage());
                ApplicationRunner.handleAlert(e.getMessage());
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.release();
                }
                try {
                    if (!Controller.isActiveVideoClient()) {
                        Controller.sendOperation(Operations.DISABLE_CAM);
                    }
                } catch (IOException e) {
                    log.severe(e.getMessage());
                    e.printStackTrace();
                }
                isRecording = false;
            }
        }
    }

    private Mat waitForFrame() throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            Mat frame = pfc.video.VideoTransmit.getDataOutputClon();
            if (!frame.empty()) {
                return frame;
            }
            frame.release();
            Thread.sleep(100);
        }
        return new Mat();
    }

    private boolean shouldKeepRecording(long startTime) throws InterruptedException {
        if (controller.getConf().isEnableSaveVideoAllTime()) {
            synchronized (this) {
                this.wait();
            }
            return false;
        }
        return System.currentTimeMillis() - startTime < controller.getConf().getTimeSaveVideo();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getSaveVideoPath() {
        return saveVideoPath;
    }

    public void setSaveVideoPath(String saveVideoPath) {
        this.saveVideoPath = saveVideoPath;
    }
}
