package pfc.control.configuration;

import java.io.*;
import java.util.*;

import pfc.gui.ApplicationRunner;

/**
 * Provides read/write access to the user's saved configuration parameters on
 * disk, so that next time they connect, we can offer the same configuration
 * parameters as a default.
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public class SavedConfiguration implements Serializable{


	/**
	 * Serial ID for serialization
	 */
	private static final long serialVersionUID = -5283899890889105860L;


	/**
	 * Server adress.
	 */
	public static final String SERVER_ADDRESS = "ServerAddress";


	/**
	 * Server RMI port
	 */
	public static final String SERVER_PORT = "ServerPort";

	/**
	 * Server serial port name
	 */
	public static final String SERVER_COMM_PORT = "ServerCommPort";

	/**
	 * Server RTP port listen
	 */
	public static final String SERVER_RTP_PORT = "ServerRTPPort";

	/**
	 * All The languages implemented
	 */
	public static final String LANGUAGES = "Languages";

	/**
	 * last Language selected
	 */
	public static final String LANGUAGE = "LanguageSelected";
	
	/**
	 * Alarm file location
	 */
	public static final String ALARM_FILE_LOCATION = "AlarmFilePath";

	/**
	 * Video camera device name
	 */
	public static final String VIDEO_DEVICE = "VideoDevice";

	/**
	 * Video source type (local or stream)
	 */
	public static final String VIDEO_SOURCE_TYPE = "VideoSourceType";

	/**
	 * Video stream URL used when the source type is stream
	 */
	public static final String VIDEO_STREAM_URL = "VideoStreamUrl";

	/**
	 * Cam enabled or not
	 */
	public static final String ENABLE_CAM = "EnableCam";
	
	/**
	 * Save video when alarm or not
	 */
	public static final String SAVE_VIDEO_ALARM = "SaveVideoAlarm";
	
	/**
	 * Save video all the vigilance time
	 */
	public static final String SAVE_VIDEO_ALL_TIME = "SaveVideoAllTime";
	
	
	/**
	 * Save video alarms location
	 */
	public static final String SAVE_VIDEO_LOCATION = "SaveVideoLocation";

	/**
	 * Save video duration for alarm
	 */
	public static final String SAVE_VIDEO_TIME = "SaveVideoMsTime";

	/**
	 * Move Scan period duration
	 */
	public static final String MOVE_SCAN_PERIOD = "MoveScanMsPeriod";
	
	/**
	 * Move scan orientation duration
	 */
	public static final String MOVE_ORIENTATION_PERIOD = "MoveOrientationMsPeriod";

	/**
	 * Termal scan period duration
	 */
	public static final String TERMAL_SCAN_PERIOD = "TermalScanMsPeriod";
	
	/**
	 * Termal scan orientation duration
	 */
	public static final String TERMAL_ORIENTATION_PERIOD = "TermalOrientationMsPeriod";

	/**
	 * Heartbeat period duration
	 */
	public static final String HEARTBEAT_PERIOD = "HeartbeatMsPeriod";
	
	/**
	 * Key for up camera
	 */
	public static final String KEY_CAMERA_UP = "KeyCameraUp";
	
	/**
	 * Key for down camera
	 */
	public static final String KEY_CAMERA_DOWN = "KeyCameraDown";
	
	/**
	 * Key for left camera
	 */
	public static final String KEY_CAMERA_LEFT = "KeyCameraLeft";
	
	/**
	 * Key for right camera
	 */
	public static final String KEY_CAMERA_RIGHT = "KeyCameraRight";
	
	/**
	 * Key for center camera
	 */
	public static final String KEY_CAMERA_CENTER = "KeyCameraCenter";
	
	
	/**
	 * Period for alarm chekin a the server by clients
	 */
	public static final String CLIENT_ALARM_CHECK_PERIOD = "ClientAlarmCheckPeriod";
	
	/**
	 * Enable termal sensor or not 
	 */
	public static final String ENABLE_TERMAL_SCAN = "EnableTermalScan";
	
	/**
	 * Enabled move sensor or not
	 */
	public static final String ENABLE_MOVE_SCAN = "EnableMoveScan";
	
	/**
	 * What type of network connectivity will be used between the client and server.
	 */
	public static final String NETWORK_TYPE = "NetworkType";
	
	
	/**
	 * The Mail Transport protocol.
	 */
	public static final String MAIL_TRANSPORT_PROTOCOL = "MailTransportProtocol"; 
	
	/**
	 * Mail Server Host 
	 */
	public static final String MAIL_HOST = "MailHost";
	
	/**
	 * Mail Server User
	 */
	public static final String MAIL_USER = "MailUser";
	
	/**
	 * Mail Server Password
	 */
	public static final String MAIL_PASSWORD = "MailPassword";
	
	/**
	 * Mail Receivers
	 */
	public static final String MAIL_RECEIVER = "MailReceivers";
	
	/**
	 * Enabled mail or not
	 */
	public static final String ENABLE_MAIL = "EnableEmail";

	/**
	 * Path where the routes will be saved and loaded
	 */
	public static final String ROUTE_PATH = "RoutePath";
	
	/**
	 * The kind of vigilance selected (follow lines, route programed or static)
	 */
	public static final String KIND_VIGILANCE = "KindVigilance";
	
	/**
	 *  The route programed selected
	 */
	public static final String ACTIVE_ROUTE = "ActiveRoute";
	
	/**
	 *  Distance for proximity alarm
	 */
	public static final String PROXIMITY_DISTANCE_ALARM = "ProximityDistance";
	
	/**
	 *  Voltage for low voltage alarm
	 */
	public static final String LOW_VOLTAGE_VALUE = "LowVoltageValue";
	
	/**
	 *  Voltage for charge batery
	 */
	public static final String CHARGE_VOLTAGE_VALUE = "ChargeVoltageValue";
	
	/**
	 *  kill active scan by a stop operation
	 */
	public static final String KILL_SCANS_BY_STOP = "KillScansByStop";
	
	
	/**
	 * The Properties for this application.
	 */
	private Properties parameters = null;

	/**
	 * The location where our configuration file will be saved.
	 */
	private static final String BASE_DIRECTORY = System.getProperty("user.dir");

	/**
	 * the name of our properties file.
	 */
	private static final String OPTIONS_FILENAME = "pfc.properties";
	
	
	

	/**
	 * The file containing our saved configuration.
	 */
	private static File savedOptionsFile
	= new File(BASE_DIRECTORY, OPTIONS_FILENAME);

	/**
	 * Placeholder for our singleton version of OptionsModel. Since we know
	 * that we will want at least one of these, we are creating our instance as
	 * soon as we possibly can. If we were unsure whether we would ever need
	 * this or not we would probably perform a lazy instantiation.
	 */
	private static SavedConfiguration savedConfiguration
	= new SavedConfiguration();


	/**
	 * Creates a new instance of SavedConfiguration. There should only ever be
	 * one instance of this class (a Singleton), so we have made it private.
	 */
	private SavedConfiguration() {
		parameters = loadParametersFromFile();

		if (parameters == null) {
			parameters = new Properties();

			parameters.setProperty(LANGUAGES, "English,Spanish");
			parameters.setProperty(LANGUAGE, "English");
			parameters.setProperty(SERVER_ADDRESS, "localhost");
			parameters.setProperty(SERVER_PORT, String.valueOf(java.rmi.registry.Registry.REGISTRY_PORT));
			parameters.setProperty(SERVER_COMM_PORT,"COM1");
			parameters.setProperty(SERVER_RTP_PORT, "1234" );
			parameters.setProperty(VIDEO_DEVICE, "Camera 0" );
			parameters.setProperty(VIDEO_SOURCE_TYPE, "local");
			parameters.setProperty(VIDEO_STREAM_URL, "");
			parameters.setProperty(ENABLE_CAM, "true");
			parameters.setProperty(SAVE_VIDEO_ALARM, "false");
			parameters.setProperty(SAVE_VIDEO_ALL_TIME, "false");
			parameters.setProperty(SAVE_VIDEO_LOCATION, "video");
			parameters.setProperty(SAVE_VIDEO_TIME, "30000");  // 30 Seconds
			parameters.setProperty(MOVE_SCAN_PERIOD, "120000"); // 2 Minutes
			parameters.setProperty(TERMAL_SCAN_PERIOD, "10000"); // 10 Seconds
			parameters.setProperty(HEARTBEAT_PERIOD, "10000"); // 10 Seconds
			parameters.setProperty(KEY_CAMERA_UP, "w"); 
			parameters.setProperty(KEY_CAMERA_DOWN, "s"); 
			parameters.setProperty(KEY_CAMERA_LEFT, "a"); 
			parameters.setProperty(KEY_CAMERA_RIGHT, "d");
			parameters.setProperty(KEY_CAMERA_CENTER,"x");
			parameters.setProperty(ALARM_FILE_LOCATION, "audio");
			parameters.setProperty(MOVE_ORIENTATION_PERIOD, "10000");
			parameters.setProperty(TERMAL_ORIENTATION_PERIOD, "5000");
			parameters.setProperty(CLIENT_ALARM_CHECK_PERIOD,"60000");
			parameters.setProperty(ENABLE_TERMAL_SCAN,"false");
			parameters.setProperty(ENABLE_MOVE_SCAN,"false");
			parameters.setProperty(MAIL_TRANSPORT_PROTOCOL,"smtp");
			parameters.setProperty(MAIL_HOST,"smtp.gmail.com");
			parameters.setProperty(MAIL_USER, "rodrigojimen");
			parameters.setProperty(MAIL_PASSWORD, "12173759");
			parameters.setProperty(MAIL_RECEIVER,"rodrigoj@telefonica.net");
			parameters.setProperty(ENABLE_MAIL,"true");
			parameters.setProperty(ROUTE_PATH,"routes");
			parameters.setProperty(KIND_VIGILANCE,"route");
			parameters.setProperty(ACTIVE_ROUTE,"");
			parameters.setProperty(LOW_VOLTAGE_VALUE,"0.0");
			parameters.setProperty(CHARGE_VOLTAGE_VALUE,"10.0");
			parameters.setProperty(KILL_SCANS_BY_STOP,"true");
		}

		ensureDefault(VIDEO_SOURCE_TYPE, "local");
		ensureDefault(VIDEO_STREAM_URL, "");
		if (parameters.remove("ServerRTPClientsMask") != null) {
			saveParametersToFile();
		}
	}

	private void ensureDefault(String key, String value) {
		if (parameters.getProperty(key) == null) {
			parameters.setProperty(key, value);
		}
	}

	/**
	 * return the single instance of the SavedConfiguration.
	 *
	 * @return the single instance of the SavedConfiguration.
	 */
	public static SavedConfiguration getSavedConfiguration() {
		return savedConfiguration;
	}

	/**
	 * returns the value of the named parameter.<p>
	 *
	 * @param parameterName the name of the parameter for which the user
	 * is requesting the value.
	 * @return the value of the named parameter.
	 */
	public String getParameter(String parameterName) {
		return parameters.getProperty(parameterName);
	}

	/**
	 * Updates the saved parameters with the new values. Always saves the new
	 * values immediately - this means we will be saving the properties file
	 * far more often than we need when we enter our first set of values,
	 * however once the initial set of values have been entered, they should
	 * rarely (if ever) be changed. So this will not be  a problem most of the
	 * time. Doing it this way means that the user of this class need not
	 * explictly save the parameters.
	 *
	 * @param parameterName the name of the parameter.
	 * @param parameterValue the value to be stored for the parameter
	 */
	public void setParameter(String parameterName, String parameterValue) {
		parameters.setProperty(parameterName, parameterValue);
		saveParametersToFile();
	}

	/**
	 * saves the parameters to a file so that they can be used again next time
	 * the application starts.
	 */
	private void saveParametersToFile() {
		try {
			synchronized (savedOptionsFile) {
				if (savedOptionsFile.exists()) {
					savedOptionsFile.delete();
				}
				savedOptionsFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(savedOptionsFile);
				parameters.store(fos, "PFC Vigilance robot contorl server configuration");
				fos.close();
			}
		} catch (IOException e) {
			ApplicationRunner.handleAlert(
					"Unable to save user parameters to file. "
					+ "They wont be remembered next time you start.");
		}
	}

	/**
	 * attempts to load the saved parameters from the file so that the user does
	 * not have to reenter all the information.
	 *
	 * @return Properties loaded from file or null.
	 */
	private Properties loadParametersFromFile() {
		Properties loadedProperties = null;

		if (savedOptionsFile.exists() && savedOptionsFile.canRead()) {
			synchronized (savedOptionsFile) {
				try {
					FileInputStream fis = new FileInputStream(savedOptionsFile);
					loadedProperties = new Properties();
					loadedProperties.load(fis);
					fis.close();
				} catch (FileNotFoundException e) {
					assert false : "File not found after existance verified";
				ApplicationRunner.handleAlert("Unable to load user "
						+ "parameters. Default values will be used.\n"
						+ e);
				} catch (IOException e) {
					assert false : "Bad data in parameters file";
				ApplicationRunner.handleAlert("Unable to load user "
						+ "parameters. Default values will be used.\n"
						+ e);
				}
			}
		}
		return loadedProperties;
	}
}

