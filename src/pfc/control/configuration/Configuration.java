package pfc.control.configuration;

import java.io.Serializable;
import pfc.control.Controller;
import pfc.control.configuration.SavedConfiguration;
import pfc.gui.ApplicationRunner;


/**
 * Implements The varibles configuration for the robot
 * @author Rodrigo Jimenez Rodriguez
 * @version 1.0	
 */
public class Configuration implements Serializable, Cloneable {


	/**
	 * serial version for the Object
	 */
	private static final long serialVersionUID = 6958553822058813974L;
	
	
	private String commport; 

	/**
	 * enable the termal sensor scan
	 */
	private boolean enableTermalSensor;

	/**
	 * Enable/Disable the movement sensor scan
	 */
	private boolean enableMovementSensor;

	/**
	 * Period that the termal sensor scan 
	 */
	private long periodTermalSensor;

	/**
	 * Period that the sensor movement scan
	 */
	private long periodMovementSensor;
	
	/**
	 * Period that the termal sensor scan 
	 */
	private long periodOrientationTermalSensor;

	/**
	 * Period that the sensor movement scan
	 */
	private long periodOrientationMovementSensor;

	/**
	 * Enable the auto movement/manual movement for the robot 
	 */
	private boolean enableSaveVideoAlarm;
	
	/**
	 * Enable the auto movement/manual movement for the robot 
	 */
	private boolean enableSaveVideoAllTime;
	

	/**
	 * Time for the video alarm recording 
	 */
	private long timeSaveVideo;

	/**
	 * Period that the heartbeat is sended to the robot
	 */
	private long periodHeartbeat;

	/**
	 * Enable the followlines movement for the robot 
	 */
	private boolean enableFollowLinesMovement;

	/**
	 * Enable the route movement 
	 */
	private boolean enableRouteMovement;

	/**
	 * Enable the route movement 
	 */
	private boolean enableStaticMovement;
		
	/**
	 * active route for the route programmed function
	 */
	private String activeRoute;
	
	/**
	 * active route for the route programmed function
	 */
	private String emailReceivers[];
	
	/**
	 * active route for the route programmed function
	 */
	private boolean enableEmail;
	
	/**
	 * active route for the route programmed function
	 */
	private float proximityDistanceAlarm;
	
	/**
	 * active route for the route programmed function
	 */
	private float lowVoltageValueAlarm;
	
	/**
	 * active route for the route programmed function
	 */
	private float chargeVoltageValue;
	
	/**
	 *  kill active scan by a stop operation
	 */
	private boolean killsScansByStop;
	
	/**
	 * Follow Lines move identifier
	 */
	public static final String FOLLOW_LINES = "FollowLines";
	
	/**
	 * Static move identifier
	 */
	public static final String STATIC = "Static";
	
	/**
	 * Programmed route move identifier
	 */
	public static final String PROGRAM_ROUTE = "ProgramRoute";
	
	
	/**
	 * Saved configuration
	 */
	SavedConfiguration config = null;

	
	/**
	 * Default Constructor
	 */
	public Configuration(){
		super();

		//load saved configuration
		config = SavedConfiguration.getSavedConfiguration();

		try {
			this.enableTermalSensor = Boolean.parseBoolean(config.getParameter(SavedConfiguration.ENABLE_TERMAL_SCAN));
			this.enableMovementSensor = Boolean.parseBoolean(config.getParameter(SavedConfiguration.ENABLE_MOVE_SCAN));
			this.timeSaveVideo = Long.parseLong(config.getParameter(SavedConfiguration.SAVE_VIDEO_TIME));
			this.periodMovementSensor = Long.parseLong(config.getParameter(SavedConfiguration.MOVE_SCAN_PERIOD));
			this.periodTermalSensor = Long.parseLong(config.getParameter(SavedConfiguration.TERMAL_SCAN_PERIOD)); 
			this.periodOrientationMovementSensor = Long.parseLong(config.getParameter(SavedConfiguration.MOVE_ORIENTATION_PERIOD));
			this.periodOrientationTermalSensor = Long.parseLong(config.getParameter(SavedConfiguration.TERMAL_ORIENTATION_PERIOD)); 
			this.periodHeartbeat = Long.parseLong(config.getParameter(SavedConfiguration.HEARTBEAT_PERIOD)); 
			this.enableSaveVideoAlarm = Boolean.parseBoolean(config.getParameter(SavedConfiguration.SAVE_VIDEO_ALARM));
			this.enableSaveVideoAllTime = Boolean.parseBoolean(config.getParameter(SavedConfiguration.SAVE_VIDEO_ALL_TIME));
			this.emailReceivers = (config.getParameter(SavedConfiguration.MAIL_RECEIVER)).split(";");
			this.enableEmail = Boolean.parseBoolean(config.getParameter(SavedConfiguration.ENABLE_MAIL));
			this.proximityDistanceAlarm = Float.parseFloat(config.getParameter(SavedConfiguration.PROXIMITY_DISTANCE_ALARM));
			this.lowVoltageValueAlarm = Float.parseFloat(config.getParameter(SavedConfiguration.LOW_VOLTAGE_VALUE));
			this.chargeVoltageValue = Float.parseFloat(config.getParameter(SavedConfiguration.CHARGE_VOLTAGE_VALUE));
			this.killsScansByStop = Boolean.parseBoolean(config.getParameter(SavedConfiguration.KILL_SCANS_BY_STOP));
			this.activeRoute = null;
			
			if (config.getParameter(SavedConfiguration.KIND_VIGILANCE).compareTo(FOLLOW_LINES) == 0){
				this.enableFollowLinesMovement = true;
				this.enableRouteMovement = false;
				this.enableStaticMovement = false;
			}
			else if (config.getParameter(SavedConfiguration.KIND_VIGILANCE).compareTo(STATIC) == 0){
				this.enableFollowLinesMovement = false;
				this.enableRouteMovement = false;
				this.enableStaticMovement = true;
			}
			else if (config.getParameter(SavedConfiguration.KIND_VIGILANCE).compareTo(PROGRAM_ROUTE) == 0){
				this.enableFollowLinesMovement = false;
				this.enableRouteMovement = true;
				this.enableStaticMovement = false;
				this.activeRoute = config.getParameter(SavedConfiguration.ACTIVE_ROUTE);
			}
			
		}catch (NumberFormatException e){
			ApplicationRunner.handleAlert("Error at SavedConfiguration a defined Value can not parsed to a long\boolean value." );
			Controller.exit(-1);
		}
	}
	
	public Object clone(){
        Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return obj;
    }


	
	/**
	 * @return String
	 */
	public String getActiveRoute() {
		return this.activeRoute;
	}

	/**
	 * @param activeRoute String
	 */
	public void setActiveRoute(String activeRoute) {
		this.activeRoute = activeRoute;
		
	}

	/**
	 * @return long
	 */
	public long getTimeSaveVideo() {
		return timeSaveVideo;
	}

	/**
	 * @param periodSaveVideo long
	 */
	public void setTimeSaveVideo(long periodSaveVideo) {
		this.timeSaveVideo = periodSaveVideo;
	}

	/**
	 * @return long
	 */
	public long getPeriodHeartbeat() {
		return periodHeartbeat;
	}

	/**
	 * @param periodHeartbeat long
	 */
	public void setPeriodHeartbeat(long periodHeartbeat) {
		this.periodHeartbeat = periodHeartbeat;
	}

	/**
	 * @return boolean
	 */
	public boolean isEnableSaveVideoAlarm() {
		return enableSaveVideoAlarm;
	}

	/**
	 * @param enableSaveVideoAlarm boolean
	 */
	public void setEnableSaveVideoAlarm(boolean enableSaveVideoAlarm) {
		this.enableSaveVideoAlarm = enableSaveVideoAlarm;		
	}

	/**
	 * @return boolean
	 */
	public boolean isEnableRouteMovement() {
		return enableRouteMovement;
	}

	/**
	 * @param enableRouteMovement boolean
	 */
	public void setEnableRouteMovement(boolean enableRouteMovement) {
		this.enableRouteMovement = enableRouteMovement;
	}

	/**
	 * @return boolean
	 */
	public boolean isEnableStaticMovement() {
		return enableStaticMovement;
	}

	/**
	 * @param enableStaticMovement boolean
	 */
	public void setEnableStaticMovement(boolean enableStaticMovement) {
		this.enableStaticMovement = enableStaticMovement;
	}

	/**
	 * @return boolean
	 */
	public boolean isEnableMovementSensor() {
		return enableMovementSensor;
	}

	/**
	 * @param enableMovementSensor boolean
	 */
	public void setEnableMovementSensor(boolean enableMovementSensor) {
		this.enableMovementSensor = enableMovementSensor;
	}

	/**
	 * @return boolean
	 */ 
	public boolean isEnableTermalSensor() {
		return enableTermalSensor;
	}

	/**
	 * @param enableTermalSensor boolean
	 */
	public void setEnableTermalSensor(boolean enableTermalSensor) {
		this.enableTermalSensor = enableTermalSensor;
	}

	/**
	 * @return boolean
	 */
	public boolean isEnableFollowLinesMovement() {
		return enableFollowLinesMovement;
	}

	/**
	 * @param enableFollowLinesMovement boolean
	 */
	public void setEnableFollowLinesMovement(boolean enableFollowLinesMovement) {
		this.enableFollowLinesMovement = enableFollowLinesMovement;
	}

	/**
	 * @return long
	 */
	public long getPeriodMovementSensor() {
		return periodMovementSensor;
	}

	/**
	 * @param periodMovementSensor long
	 */
	public void setPeriodMovementSensor(long periodMovementSensor) {
		this.periodMovementSensor = periodMovementSensor;
	}

	/**
	 * @return llong
	 */
	public long getPeriodTermalSensor() {
		return periodTermalSensor;
	}

	/**
	 * @param periodTermalSensor long
	 */
	public void setPeriodTermalSensor(long periodTermalSensor) {
		this.periodTermalSensor = periodTermalSensor;
	}

	/**
	 * @return String
	 */
	public String getCommport() {
		return commport;
	}

	/**
	 * @param commport Strning
	 */
	public void setCommport(String commport) {
		this.commport = commport;
	}

	/**
	 * @return long
	 */
	public long getPeriodOrientationTermalSensor() {
		return periodOrientationTermalSensor;
	}

	/**
	 * @param periodOrientationTermalSensor long
	 */
	public void setPeriodOrientationTermalSensor(long periodOrientationTermalSensor) {
		this.periodOrientationTermalSensor = periodOrientationTermalSensor;
	}

	/**
	 * @return long
	 */
	public long getPeriodOrientationMovementSensor() {
		return periodOrientationMovementSensor;
	}

	/**
	 * @param periodOrientationMovementSensor lomng
	 */
	public void setPeriodOrientationMovementSensor(long periodOrientationMovementSensor) {
		this.periodOrientationMovementSensor = periodOrientationMovementSensor;
		
	}

	/**
	 * @return boolean
	 */
	public boolean isEnableSaveVideoAllTime() {
		return enableSaveVideoAllTime;
	}

	/**
	 * @param enableSaveVideoAllTime boolean
	 */
	public void setEnableSaveVideoAllTime(boolean enableSaveVideoAllTime) {
		this.enableSaveVideoAllTime = enableSaveVideoAllTime;
	}

	/**
	 * @return String[]
	 */
	public String[] getEmailReceivers() {
		return emailReceivers;
	}

	/**
	 * @param emailReceivers String
	 */
	public void setEmailReceivers(String emailReceivers[]) {
		this.emailReceivers = emailReceivers;
	}

	/**
	 * @return boolean
	 */
	public boolean isEnableEmail() {
		return enableEmail;
	}

	/**
	 * @param enableEmail boolean
	 */
	public void setEnableEmail(boolean enableEmail) {
		this.enableEmail = enableEmail;		
	}

	/**
	 * @return boolean
	 */
	public float getProximityDistanceAlarm() {
		return proximityDistanceAlarm;
	}

	/**
	 * @param proximityDistanceAlarm float
	 */
	public void setProximityDistanceAlarm(float proximityDistanceAlarm) {
		this.proximityDistanceAlarm = proximityDistanceAlarm;
	}

	/**
	 * @return float
	 */
	public float getLowVoltageValueAlarm() {
		return lowVoltageValueAlarm;
	}

	/**
	 * @param lowVoltageValueAlarm float
	 */
	public void setLowVoltageValueAlarm(float lowVoltageValueAlarm) {
		this.lowVoltageValueAlarm = lowVoltageValueAlarm;
	}

	/**
	 * @return float
	 */
	public float getChargeVoltageValue() {
		return chargeVoltageValue;
	}

	/**
	 * @param chargeVoltageValue float
	 */
	public void setChargeVoltageValue(float chargeVoltageValue) {
		this.chargeVoltageValue = chargeVoltageValue;
	}

	/**
	 * @return boolean
	 */
	public boolean getKillsScansByStop() {
	    return killsScansByStop;
	}

	/**
	 * @param killsScansByStop boolean
	 */
	public void setKillsScansByStop(boolean killsScansByStop) {
	    this.killsScansByStop = killsScansByStop;
	}

}
