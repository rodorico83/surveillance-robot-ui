package pfc.video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import org.opencv.core.Mat;

/**
 * Utility methods for loading OpenCV and converting frames.
 */
public final class OpenCvUtils {

    private static final Logger LOG = Logger.getLogger("pfc.video");
    private static boolean loaded = false;
    private static boolean ffmpegLoaded = false;
    private static final String FFMPEG_RUNTIME_NAME = "opencv_videoio_ffmpeg4130_64.dll";

    private OpenCvUtils() {
    }

    public static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }

        try {
            File baseDirectory = new File(".").getAbsoluteFile().getParentFile();
            File dllFile = new File(baseDirectory, "lib\\opencv_java4130.dll");
            if (dllFile.exists()) {
                System.load(dllFile.getAbsolutePath());
            } else {
                System.loadLibrary("opencv_java4130");
            }
            loadFfmpegRuntime(baseDirectory);
            loaded = true;
            LOG.info("OpenCV loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            LOG.severe("Unable to load OpenCV native library: " + e.getMessage());
            throw e;
        }
    }

    public static boolean hasFfmpegRuntime() {
        File baseDirectory = new File(".").getAbsoluteFile().getParentFile();
        if (containsFfmpegPlugin(baseDirectory)) {
            return true;
        }

        File libDirectory = new File(baseDirectory, "lib");
        return containsFfmpegPlugin(libDirectory);
    }

    private static boolean containsFfmpegPlugin(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return false;
        }

        File[] matches = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowerName = name.toLowerCase();
                return lowerName.startsWith("opencv_videoio_ffmpeg")
                        && lowerName.endsWith(".dll");
            }
        });

        return matches != null && matches.length > 0;
    }

    private static void loadFfmpegRuntime(File baseDirectory) {
        if (ffmpegLoaded) {
            return;
        }

        File[] candidateFiles = new File[] {
                new File(baseDirectory, "lib\\" + FFMPEG_RUNTIME_NAME),
                new File(baseDirectory, FFMPEG_RUNTIME_NAME),
                new File("D:\\Program Files\\opencv\\build\\x64\\vc16\\bin\\" + FFMPEG_RUNTIME_NAME)
        };

        for (int i = 0; i < candidateFiles.length; i++) {
            File candidateFile = candidateFiles[i];
            if (!candidateFile.exists()) {
                continue;
            }

            try {
                System.load(candidateFile.getAbsolutePath());
                ffmpegLoaded = true;
                LOG.info("OpenCV FFmpeg runtime loaded from " + candidateFile.getAbsolutePath());
                return;
            } catch (UnsatisfiedLinkError e) {
                LOG.warning("Unable to load OpenCV FFmpeg runtime from "
                        + candidateFile.getAbsolutePath() + ": " + e.getMessage());
            }
        }

        LOG.warning("OpenCV FFmpeg runtime not found. RTSP/HTTP streams may not work.");
    }

    public static BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] source = new byte[bufferSize];
        matrix.get(0, 0, source);

        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        byte[] target = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(source, 0, target, 0, source.length);
        return image;
    }
}
