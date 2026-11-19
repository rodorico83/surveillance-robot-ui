package pfc.control.client;


import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import pfc.audio.ReproduceAudio;
import pfc.control.RegAlarm;
import pfc.language.Language;
import pfc.control.configuration.SavedConfiguration;
import pfc.service.interfaces.IService;

/**
 * Client controller implementation which check for alarms at he server and the state running or not
 * @author Rodrigo
 *
 */
public class Controller implements Runnable{

	private IService connection;
	private  RegAlarm moveAlarm = null;
	private  RegAlarm termalAlarm = null;
	private  RegAlarm heartbeatAlarm = null;
	private  RegAlarm distanceAlarm = null;
	private  RegAlarm lowVoltageAlarm = null;
	private long updateAlarmTime;
	private SavedConfiguration config;
	private JLabel statusLabel  = null;
	private Language lang = null;
	private JTextArea logger = null;

	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.db</code>.
	 */
	private Logger log = Logger.getLogger("pcf.control.client");

	/**
	 * Default constructor
	 * @param connection Service connectio to the server
	 * @param lang language to use
	 * @param statusLabel status label indicating if the vigilance is running or not
	 * @param logger logger
	 */
	public Controller(IService connection, Language lang, JLabel statusLabel, JTextArea logger){

		/* load saved configuration */
		config = SavedConfiguration.getSavedConfiguration();
		this.statusLabel = statusLabel;
		this.lang = lang;
		this.connection = connection;
		this.logger = logger;
		updateAlarmTime = Long.parseLong(config.getParameter(SavedConfiguration.CLIENT_ALARM_CHECK_PERIOD));
	}

	public void run() {

		RegAlarm newMoveAlarm, newTermalAlarm, newHeartbeatAlarm, newDistanceAlarm, newLowVoltageAlarm;
		boolean isStarted = false;
		boolean alarm = false;


		while (true) {
			try {
				//log.info("Client Checking Server Status...");
				newMoveAlarm = connection.getMoveAlarm();
				newTermalAlarm = connection.getTermalAlarm();
				newHeartbeatAlarm = connection.getHeartbeatAlarm();
				newDistanceAlarm = connection.getDistanceAlarm();
				newLowVoltageAlarm = connection.getLowVoltageAlarm();

				if (newAlarmDetected(moveAlarm,newMoveAlarm)){
					log.info("Movemenet Alarm Detected " + newMoveAlarm.getTimeOcurrence().toString());
					logger.append("INFO: " + lang.MOVE_ALARM_MESSAGE + newMoveAlarm.getTimeOcurrence().toString() + "\n");
					alarm = true;
				}
				if (newAlarmDetected(termalAlarm,newTermalAlarm)) {
					log.info("Termal Alarm Detected " + newTermalAlarm.getTimeOcurrence().toString() + " Captures: " + newTermalAlarm.getTermalCaptures());
					logger.append("INFO: " + lang.TERMAL_ALARM_MESSAGE + newTermalAlarm.getTimeOcurrence().toString() + " Captures: " + newTermalAlarm.getTermalCaptures() + "\n");
					alarm = true;
				}
				if (newAlarmDetected(heartbeatAlarm,newHeartbeatAlarm)) {
					log.info("Heartbeat Alarm Detected " + newHeartbeatAlarm.getTimeOcurrence().toString());
					logger.append("INFO: " + lang.HEARTBEAT_ALARM_MESSAGE + newHeartbeatAlarm.getTimeOcurrence().toString() + "\n");
					alarm = true;
				}

				if (newAlarmDetected(distanceAlarm,newDistanceAlarm)) {
					log.info("Distance Proximity Alarm Detected " + newDistanceAlarm.getDistance() + " centimeters " + newDistanceAlarm.getTimeOcurrence().toString());
					logger.append("INFO: " + lang.DISTANCE_ALARM_MESSAGE + newDistanceAlarm.getTimeOcurrence().toString() + " Distance: " + newDistanceAlarm.getDistance() + " cm\n");
					alarm = true;
				}
				
				if (newAlarmDetected(lowVoltageAlarm,newLowVoltageAlarm)) {
					log.info("Low Voltage Alarm Detected " + newLowVoltageAlarm.getVoltage() + " V "+  newLowVoltageAlarm.getTimeOcurrence().toString());
					logger.append("INFO: " + lang.LOW_VOLTAGE_ALARM_MESSAGE + newLowVoltageAlarm.getTimeOcurrence().toString() + " Voltage: " + newLowVoltageAlarm.getVoltage() + "\n");
					alarm = true;
				}

				if (alarm){
					alarm = false;
					Thread thread = new Thread(new ReproduceAudio(config.getParameter(SavedConfiguration.ALARM_FILE_LOCATION)));
					thread.start();
				}

				if (connection.isStarted() && !isStarted) {
					statusLabel.setText(lang.VIGILANCE_STARTED_LABEL);
					logger.append("INFO: " + lang.START_VIGILANCE_MESSAGE + ((Date)new Date()).toString() + "\n");
					isStarted = true;
				}
				else if (!connection.isStarted() && isStarted){
					statusLabel.setText(lang.VIGILANCE_STOPPED_LABEL);
					logger.append("INFO: " + lang.STOP_VIGILANCE_MESSAGE + ((Date)new Date()).toString() + "\n");
					isStarted = false;
				}

			} catch (RemoteException e) {
				log.severe(e.getMessage());
				log.severe(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				log.severe(e.getMessage());
				log.severe(e.toString());
				e.printStackTrace();
			}

			try {
				Thread.sleep(updateAlarmTime);
			} catch (InterruptedException e) {
				log.severe(e.getMessage());
				log.severe(e.toString());
				e.printStackTrace();
			}
		}
	}


	private boolean newAlarmDetected(RegAlarm lastAlarm, RegAlarm newAlarm){
		boolean setNewAlarm = false;
		if (newAlarm != null) {
			if ((lastAlarm == null) || (lastAlarm != null && lastAlarm.getTimeOcurrence().toString().compareTo(newAlarm.getTimeOcurrence().toString()) != 0)) { 
				setNewAlarm = true;
				
				if (newAlarm.getType().equals(RegAlarm.HEARTBEAT_TYPE)) 
					heartbeatAlarm = new RegAlarm (newAlarm.getType(), newAlarm.getTimeOcurrence());
				
				else if (newAlarm.getType().equals(RegAlarm.MOVE_TYPE)) 
					moveAlarm = new RegAlarm(newAlarm.getType(), newAlarm.getTimeOcurrence());

				else if (newAlarm.getType().equals(RegAlarm.TERMAL_TYPE))  
					termalAlarm = new RegAlarm(newAlarm.getType(), newAlarm.getTimeOcurrence());
				
				else if (newAlarm.getType().equals(RegAlarm.DISTANCE_TYPE))  
					distanceAlarm = new RegAlarm(newAlarm.getType(), newAlarm.getTimeOcurrence());
				
				else if (newAlarm.getType().equals(RegAlarm.LOW_VOLTAGE_TYPE))  
					lowVoltageAlarm = new RegAlarm(newAlarm.getType(), newAlarm.getTimeOcurrence());
			}

		}
		return setNewAlarm;
	}


	/**
	 * Get the period of time for server alarm check
	 * @return long 
	 */
	public long getUpdateAlarmTime() {
		return updateAlarmTime;
	}

	/**
	 * Set the period of time for server alarm check
	 * @param updateAlarmTime long
	 */
	public void setUpdateAlarmTime(long updateAlarmTime) {
		this.updateAlarmTime = updateAlarmTime;
		config.setParameter(SavedConfiguration.CLIENT_ALARM_CHECK_PERIOD, String.valueOf(updateAlarmTime));
	}


}
