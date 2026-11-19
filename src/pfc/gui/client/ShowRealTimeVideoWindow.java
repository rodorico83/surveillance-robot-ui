package pfc.gui.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Logger;
import pfc.gui.ApplicationRunner;
import pfc.language.Language;


/**
 * Show real time window
 * @author rodrigo
 *
 */
public class ShowRealTimeVideoWindow extends PlayerWindow{
	
	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.gui</code>.
	 */
	private Logger log = Logger.getLogger("pfc.gui.client");
	
	private GuiController controller;
	
	
	/**
	 * Serial ID for serialization 
	 */
	private static final long serialVersionUID = -51151889565495979L;

	private Language lang;

	/**
	 * Constructor
	 * @param lang language
	 * @param controller controller
	 */
	public ShowRealTimeVideoWindow(Language lang, GuiController controller) {
		super(lang);
		this.lang = lang;
		this.controller = controller;
		this.setName(this.lang.OPTIONS_SHOW_REAL_TIME_VIDEO_NAME);
		this.setTitle(this.lang.OPTIONS_SHOW_REAL_TIME_VIDEO_NAME);
		this.addWindowListener(new ShowRealTimeWindowAdapter());
	}
	
	public void initializeComponents(){};
	
	
	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	class ShowRealTimeWindowAdapter extends WindowAdapter {

		public void windowClosing(WindowEvent we) {
			try {
				controller.getConnection().enableRobotCam(false);
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
			closeRTP();
			setVisible(false);
		}
	}

}

