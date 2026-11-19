package pfc.audio;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Rodrigo Jimenez
 */
public class ReproduceAudio implements Runnable {


    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger namespace is <code>suncertify.db</code>.
     */
    private Logger log = Logger.getLogger("pcf.audio");
    
    /**
     * Audio File Path for alarm 
     */
    private String file;

    /**
     * @param file file path of the audio 
     */
    public ReproduceAudio(String file) {
	this.file = file;
    }

    public void run() {
	/* Obtain a video clip */
	Clip sound = null;
	try {
	    sound = AudioSystem.getClip();
	} catch (LineUnavailableException e) {
	    log.severe(e.getMessage());
	    e.printStackTrace();
	    
	}
	/* Charge the clip with a video file */
	try {
	    
	    sound.open(AudioSystem.getAudioInputStream(new File(file)));
	    log.info("Playing " + file);
	} catch (LineUnavailableException e) {
	    log.severe(e.getMessage());
	    //e.printStackTrace();
	} catch (IOException e) {
	    log.severe(e.getMessage());
	    //e.printStackTrace();
	} catch (UnsupportedAudioFileException e) {
	    log.severe(e.getMessage());
	    //e.printStackTrace();
	}
	/* start reproduction */
	sound.start();

	try {
	    Thread.sleep(5000);
	} catch (InterruptedException e) {
	    log.severe(e.getMessage());
	    e.printStackTrace();
	}
	
	/* Close clip. */
	log.info("Closing player " + file);
	sound.close();
    }

}

