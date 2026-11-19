package pfc.gui.client;

import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import pfc.gui.ApplicationMode;
import pfc.gui.ApplicationRunner;
import pfc.control.configuration.SavedConfiguration;
import pfc.gui.comun.ConfigOptions;
import pfc.gui.comun.OptionUpdate;
import pfc.language.*;


/**
 * Dialog box to get configuration options for client application. This class
 * provides a standard dialog box which allows the user to select the location
 * of the server (which may be a physical file in local mode, or the address
 * (and, optionally, the port) of the server. The user can, of course, cancel,
 * in which case the application should not start (this is at the applications
 * discretion though - the business logic could be changed later in the calling
 * class to decide to start the application anyway if there configuration info
 * can be loaded from file).<br/>
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public class HostLocationDialog extends WindowAdapter
implements ActionListener, Observer {

	/*
	 * Some values for possible port ranges so we can determine what sort of
	 * port the user has specified.
	 */
	private static final int LOWEST_PORT = 0;
	private static final int HIGHEST_PORT = 65535;
	private static final int SYSTEM_PORT_BOUNDARY = 1024;
	private static final int IANA_PORT_BOUNDARY = 49151;

	/*
	 * The bits and pieces that comprise our dialog box. They are all global so
	 * we can disable them or enable them as the user enters valid information.
	 */
	private JOptionPane options = null;
	private JDialog dialog = null;
	private JButton connectButton = null;
	private JButton exitButton = null;
	private Language lang = null;
	

	/*
	 * The common panel that is used by both the client and the server for
	 * specifying where the database is.
	 */
	private ConfigOptions configOptions = null;

	/*
	 * Flags to show whether enough information has been provided for us to
	 * start the application.
	 */
	private boolean validHost = false;
	private boolean validPort = false;
	private boolean validPortComm = false;
	private boolean validPortRTP = false;
	private boolean validVideoDevice = false;
	private boolean validCnx = false;

	/*
	 * Details specified in the configOptions pane detailing where the server
	 * is.
	 */
	private String languages = null;
	private String language = null;
	private String location = null;
	private String port = null;
	private String commport = null;
	private String rtpport = null;
	private String videoSourceType = "local";
	private String videoDevice = null;
	private String videoStreamUrl = "";
	private ConnectionType networkType = null;
	private String camEnable = null;
	private String saveVideoPath = null;

	/*
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>sampleproject.gui</code>.
	 */
	private Logger log = Logger.getLogger("pfc.gui.client");

	/**
	 * Creates a dialog where the user can specify the location of the
	 * database,including the type of network connection (if this is a
	 * networked client)and IP address and port number; or search and select
	 * the database on a local drive if this is a standalone client.
	 *
	 * @param parent Defines the Component that is to be the parent of this
	 * dialog box. For information on how this is used, see
	 * <code>JOptionPane</code>
	 * @param connectionMode Specifies the type of connection (standalone or
	 * networked)
	 * @see JOptionPane
	 */
	public HostLocationDialog(Frame parent,
			ApplicationMode connectionMode) {


		// load saved configuration
		SavedConfiguration config = SavedConfiguration.getSavedConfiguration();
		language = config.getParameter(SavedConfiguration.LANGUAGE);
		
		try {
			lang = (Language) Class.forName("pfc.language.Language" + language).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		configOptions = (new ConfigOptions(connectionMode, lang));
		configOptions.getObservable().addObserver(this);


		languages = config.getParameter(SavedConfiguration.LANGUAGES);
		configOptions.setLanguages(languages);

		configOptions.setLanguage(language);
		
		camEnable = config.getParameter(SavedConfiguration.ENABLE_CAM);
		configOptions.setCamEnable(camEnable);
		
		
		if (connectionMode  == ApplicationMode.STANDALONE_CLIENT) {
		    saveVideoPath = config.getParameter(SavedConfiguration.SAVE_VIDEO_LOCATION);
		    configOptions.setSaveVideoPathText(saveVideoPath);
		    videoSourceType = config.getParameter(SavedConfiguration.VIDEO_SOURCE_TYPE);
		    configOptions.setVideoSourceType(videoSourceType);
		    videoStreamUrl = config.getParameter(SavedConfiguration.VIDEO_STREAM_URL);
		    configOptions.setStreamUrlText(videoStreamUrl);
		}

		// the port and connection type are irrelevant in standalone mode
		if (connectionMode == ApplicationMode.STANDALONE_CLIENT) {
			validHost = true;
			validPort = true;
			validPortRTP = true;

			//there is always at least a default port number, so we don't have
			// to validate this.
			rtpport = config.getParameter(SavedConfiguration.SERVER_RTP_PORT);
			configOptions.setPortRTPNumberText(rtpport);
			validPortRTP = true;

			//When we are in standalone mode the rtp clients only be localhost and the location of the server is the same
			try {
				InetAddress local = InetAddress.getByName(InetAddress.getLocalHost().getHostName());
				location = local.getHostAddress();
			} catch (UnknownHostException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}

			//there is always at least a default port number, so we don't have
			// to validate this.
			commport = config.getParameter(SavedConfiguration.SERVER_COMM_PORT);
			configOptions.setPortCommNumberText(commport);
			validPortComm = true;

			// there is always at least a default port number, so we don't have
			// to validate this.
			videoDevice = config.getParameter(SavedConfiguration.VIDEO_DEVICE);
			configOptions.setCameraDeviceSelection(videoDevice);
			validVideoDevice = true;

			validCnx = true;
			networkType = ConnectionType.DIRECT;
			//location = config.getParameter(SavedConfiguration.SERVER_LOCATION);
		} else {
			// there may not be a network connectivity type defined and, if
			// not, we do not set a default - force the user to make a choice
			// the at least for the first time they run this.
			String tmp = config.getParameter(SavedConfiguration.NETWORK_TYPE);
			if (tmp != null) {
				try {
					networkType = ConnectionType.valueOf(tmp);
					configOptions.setNetworkConnection(networkType);
					validCnx = true;
				} catch (IllegalArgumentException e) {
					log.warning("Unknown connection type: " + networkType);
				}
			}

			// there is always at least a default port number, so we don't have
			// to validate this.
			port = config.getParameter(SavedConfiguration.SERVER_PORT);
			configOptions.setPortNumberText(port);
			validPort = true;

			//there is always at least a default port number, so we don't have
			// to validate this.
			rtpport = config.getParameter(SavedConfiguration.SERVER_RTP_PORT);
			configOptions.setPortRTPNumberText(rtpport);
			validPortRTP = true;

			validVideoDevice = true;

			location = config.getParameter(SavedConfiguration.SERVER_ADDRESS);
			//	    configOptions.s(port);
		}

		// there may not be a default server location, so we had better
		// validate before using the returned value.
		if (location != null) {
			configOptions.setLocationFieldText(location);
			validHost = true;
		}

		options = new JOptionPane(configOptions,
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		
		

		connectButton = new JButton(lang.CONNECT);
		connectButton.setActionCommand("Connect");
		connectButton.addActionListener(this);

		boolean allValid = validHost && validPort && validPortRTP && validCnx;
		connectButton.setEnabled(allValid);

		exitButton = new JButton(lang.QUIT);
		exitButton.setActionCommand(lang.QUIT);
		exitButton.addActionListener(this);

		options.setOptions(new Object[] {connectButton, exitButton});

		dialog = options.createDialog(parent, lang.HOSTLOCATIONDIALOG_WINDOW_NAME);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(this);
		dialog.setVisible(true);
	}

	// Note: we can get away with not specifying the parameters in these
	// callback methods, as they are specified in the interfaces we are
	// implementing.

	/**
	 * Callback event handler to process situations where the user has closed
	 * the window rather than clicking one of the buttons.
	 */
	public void windowClosing(WindowEvent we) {
		processCommand(lang.QUIT);
	}

	/**
	 * Callback event handler to process clicks on any of the buttons.
	 */
	public void actionPerformed(ActionEvent ae) {
		processCommand(ae.getActionCommand());
	}

	/**
	 * Common event handling code - can handle desirable actions (such as
	 * buttons being clicked) and undesirable actions (the window being
	 * closed) all in a common location.
	 *
	 * @param command a String representing the action that occurred.
	 */
	private void processCommand(String command) {
		dialog.setVisible(false);
		if (command.equals("Connect")) {
			options.setValue(JOptionPane.OK_OPTION);
		} else {
			options.setValue(JOptionPane.CANCEL_OPTION);
		}
	}

	/**
	 * Callback method to process modifications in the common ConfigOptions
	 * panel. ConfigOptions was developed to be common to many applications
	 * (even though we only have three modes), so it does not have any
	 * knowledge of how we are using it within this dialog box. So
	 * ConfigOptions just sends updates to registered Observers whenever
	 * anything changes. We can receive those notifications here, and
	 * decide whether we have enough information to enable the "Connect"
	 * button of the dialog box.
	 */
	public void update(Observable o, Object arg) {
		// we are going to ignore the Observable object, since we are only
		// observing one object. All we are interested in is the argument.

		if (!(arg instanceof OptionUpdate)) {
			log.log(Level.WARNING,
					"Server received update type: " + arg,
					new IllegalArgumentException());
			return;
		}

		OptionUpdate optionUpdate = (OptionUpdate) arg;

		// load saved configuration
		SavedConfiguration config = SavedConfiguration.getSavedConfiguration();

		switch (optionUpdate.getUpdateType()) {
		case HOST_LOCATION_CHANGED:
			location = (String) optionUpdate.getPayload();
			if (configOptions.getApplicationMode()
					== ApplicationMode.NETWORK_CLIENT) {

				try {
					if (location.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
						// location given matches 4 '.' separated numbers
						// regex could be improved by limiting each quad to
						// no more than 3 digits.
						String[] quads = location.split("\\.");
						byte[] address = new byte[quads.length];
						for (int i = 0; i < quads.length; i++) {
							address[i] = new Integer(quads[i]).byteValue();
						}
						InetAddress.getByAddress(address);
					} else {
						InetAddress.getAllByName(location);
					}
					log.info("Server specified " + location);
					validHost = true;
					config.setParameter(SavedConfiguration.SERVER_ADDRESS,
							location);
				} catch (UnknownHostException uhe) {
					log.warning("Unknown host: " + location);
					validHost = false;
				}
			}
			break;
			
		case SAVE_VIDEO_PATH_CHANGED:
			String saveVideoPath = (String) optionUpdate.getPayload();
			config.setParameter(SavedConfiguration.SAVE_VIDEO_LOCATION, saveVideoPath);
			break;	

		case PORT_CHANGED:
			port = (String) optionUpdate.getPayload();
			int p = Integer.parseInt(port);

			if (p >= LOWEST_PORT && p < HIGHEST_PORT) {
				if (p < SYSTEM_PORT_BOUNDARY) {
					log.info("User chose System port " + port);
				} else if (p < IANA_PORT_BOUNDARY) {
					log.info("User chose IANA port " + port);
				} else {
					log.info("User chose dynamic port " + port);
				}
				validPort = true;
				config.setParameter(SavedConfiguration.SERVER_PORT, port);
			} else {
				validPort = false;
			}
			break;

		case LANGUAGE_CHANGED:
			language = (String) optionUpdate.getPayload();
			try {
				lang = (Language) Class.forName("pfc.language.Language" + language).newInstance();
				config.setParameter(SavedConfiguration.LANGUAGE, language);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				log.severe(e.getMessage());
				configOptions.setLanguage(config.getParameter(SavedConfiguration.LANGUAGE));
				ApplicationRunner.handleAlert("Language "+ language + " Not found in the Application.");
			}
			break;

		case COMM_PORT_CHANGED:    
			commport = (String) optionUpdate.getPayload();
			validPortComm = true;
			config.setParameter(SavedConfiguration.SERVER_COMM_PORT, commport);
			break; 

		case RTP_PORT_CHANGED:
			rtpport = (String) optionUpdate.getPayload();
			validPortRTP = true;
			config.setParameter(SavedConfiguration.SERVER_RTP_PORT, rtpport);
			break;    

		case CAM_ENABLE_CHANGED:
			camEnable = (String) optionUpdate.getPayload();
			config.setParameter(SavedConfiguration.ENABLE_CAM, camEnable);
			break;    

		case VIDEO_DEVICE_CHANGED:
			videoDevice = (String) optionUpdate.getPayload();
			validVideoDevice = true;
			config.setParameter(SavedConfiguration.VIDEO_DEVICE, videoDevice);
			break;    

		case VIDEO_SOURCE_TYPE_CHANGED:
			videoSourceType = (String) optionUpdate.getPayload();
			config.setParameter(SavedConfiguration.VIDEO_SOURCE_TYPE, videoSourceType);
			break;

		case VIDEO_STREAM_URL_CHANGED:
			videoStreamUrl = (String) optionUpdate.getPayload();
			config.setParameter(SavedConfiguration.VIDEO_STREAM_URL, videoStreamUrl);
			break;

		case NETWORK_CHOICE_MADE:
			networkType = (ConnectionType) optionUpdate.getPayload();
			switch (networkType) {
			case RMI:
				log.info("Server connection via RMI");
				break;
			default:
				log.info("Unknown connection type: " + networkType);
				break;
			}
			config.setParameter(SavedConfiguration.NETWORK_TYPE,
					"" + networkType);
			validCnx = true;
			break;
		default:
			log.warning("Unknown update: " + optionUpdate);
			break;
		}

		boolean allValid = validHost && validPort && validPortRTP && validCnx;
		if (connectButton != null)
			connectButton.setEnabled(allValid);
	}


	/**
	 * Returns the location of the database, which may be either the path to
	 * the local database, or the address of the network server hosting the
	 * database.
	 *
	 * @return the location of the database.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns the port number the network server should be listening on for
	 * client connections.
	 *
	 * @return the port number for connecting to the network server.
	 */
	public String getPort() {
		return port;
	}


	/**
	 * Returns the commport number the controller communicate with the robot
	 *
	 * @return the port number for connecting to the network server.
	 */
	public String getCommport() {
		return commport;
	}

	/**
	 * Returns the rtpcport number for the RTP communication
	 *
	 * @return the rtpport number for connecting to the network server.
	 */
	public String getRtpport() {
		return rtpport;
	}

	/**
	 * Returns the video device
	 *
	 * @return the port number for connecting to the network server.
	 */
	public String getVideoDevice() {
		return videoDevice;
	}

	public String getSelectedVideoSource() {
		return configOptions.getSelectedVideoSource();
	}

	/**
	 * Returns the type of network connection (Sockets, RMI ...) that should
	 * be used to connect to the server.
	 *
	 * @return the network protocol used to connect to the server.
	 */
	public ConnectionType getNetworkType() {
		return networkType;
	}

	/**
	 * Let the caller of this dialog know whether the user connected or
	 * cancelled.
	 *
	 * @return true if the user cancelled or closed the window.
	 */
	public boolean userCanceled() {
		if (options.getValue() instanceof Integer) {
			int status = ((Integer) options.getValue()).intValue();
			return status != JOptionPane.OK_OPTION;
		} else {
			return false;
		}
	}

	/**
	 * Is video enabled
	 * @return boolean
	 */
	public boolean isCamEnable() {
		if (this.camEnable.equals("true")) return true;
		else return false;
	}

	/**
	 * Set if is video enabled in a string format
	 * @param useCam String
	 */
	public void setCamEnable(String useCam) {
		this.camEnable = useCam;
	}

	/**
	 * Get the language selected
	 * @return String
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * set the language to use
	 * @param language String
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Get language instance used
	 * @return Language
	 */
	public Language getLang() {
		return lang;
	}

	/**
	 * Set language instance to use
	 * @param lang Language
	 */
	public void setLang(Language lang) {
		this.lang = lang;
	}


}

