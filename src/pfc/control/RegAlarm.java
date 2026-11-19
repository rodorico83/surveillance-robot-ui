package pfc.control;

import java.io.Serializable;
import java.util.Date;

/**
 * Record for alarms
 * @author rodrigo
 *
 */
public class RegAlarm implements Serializable {


	/**
	 * Serial version ID for serialization
	 */
	private static final long serialVersionUID = -5365584411345870918L;

	/**
	 * Heartbeat alarm record identification
	 */
	public static final String HEARTBEAT_TYPE = "heartbeat";
	/**
	 * Move alarm record identification
	 */
	public static final String MOVE_TYPE = "movement";
	/**
	 * Termal alarm record identification
	 */
	public static final String TERMAL_TYPE = "termal";
	/**
	 * Heartbeat alarm record identification
	 */
	public static final String DISTANCE_TYPE = "proximity";
	/**
	 * low voltage alarm record identification
	 */
	public static final String LOW_VOLTAGE_TYPE = "lowVoltage";


	private String type;

	private Date timeOcurrence;
	
	private String termalCaptures = null;
	
	private String distance = null;
	
	private String voltage = null;

	/**
	 * Default Constructor 
	 * @param type alarm kind
	 * @param date date for alarm
	 */
	public RegAlarm(String type, Date date) {
		this.type = type;
		this.timeOcurrence = date;
	}
	
	/**
	 * Constructor for termal alarm
	 * @param type alarm kind
	 * @param date date occurence
	 * @param termalCaptures String, value separated by commas
	 */
	public RegAlarm(String type, Date date, String termalCaptures) {
		this.type = type;
		this.timeOcurrence = date;
		this.termalCaptures = termalCaptures;
	}
	
	
	
	/**
	 * Get the time occurence of the alarm
	 * @return Date
	 */
	public Date getTimeOcurrence() {
		return timeOcurrence;
	}
	
	/**
	 * Set the time occurence of the alarm
	 * @param timeOcurrence Date 
	 */
	public void setTimeOcurrence(Date timeOcurrence) {
		this.timeOcurrence = timeOcurrence;
	}
	
	/**
	 * Get the alarm kind
	 * @return String
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Set the alarm kind
	 * @param type String
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the termal captures
	 * @return String, values separated by commas
	 */
	public String getTermalCaptures() {
		return termalCaptures;
	}

	/**
	 * Set termal captures alarm
	 * @param termalCaptures String, values separated by commas
	 */
	public void setTermalCaptures(String termalCaptures) {
		this.termalCaptures = termalCaptures;
	}

	/**
	 * Get the distance proximity 
	 * @return String value in centimeters
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * Set the distance proximity detected
	 * @param distance String value in centimeters
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * Get the voltage value
	 * @return String Value in Volts
	 */
	public String getVoltage() {
		return voltage;
	}

	/**
	 * Set the voltage value detected
	 * @param voltage String, value in centimeters
	 */
	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}

}
