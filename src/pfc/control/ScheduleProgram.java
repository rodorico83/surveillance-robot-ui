package pfc.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Schedule Configuration program implementation
 * @author Rodrigo
 *
 */
public class ScheduleProgram implements Serializable, Cloneable{

	/**
	 * Serial Id for serialization 
	 */
	private static final long serialVersionUID = -6448050488878706981L;



	private boolean isEnabled = false;
	private boolean once = false;
	private boolean diary = false;
	private boolean weekly = false;
	private boolean monthly = false;
	private boolean anual = false;
	private Calendar start = null;
	private Calendar stop = null;


	/**
	 * Get A Schedule Configuration Object Clon
	 * @return Object
	 */
	public Object getClon(){  
		ObjectOutputStream oos = null;  
		ObjectInputStream ois = null;  
		try {  
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
			oos = new ObjectOutputStream(bos);  
			// serialize and pass the object  
			oos.writeObject(this);  
			oos.flush();  
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());  
			ois = new ObjectInputStream(bin);  
			// return the new object  
			return ois.readObject();  
		} catch (Exception ex) {  
			throw new RuntimeException("Error while making a deep copy of object: {0}", ex);  
		}  
	}  

	
	/**
	 * Validate a Field date (hour, minute, moth day)
	 * @param type int date field
	 * @param value  date field value
	 * @return boolena
	 * 
	 */
	public static boolean validateField(int type, int value){
		if (type == Calendar.HOUR_OF_DAY && 0 <= value && value <= 24) return true;
		else if (type == Calendar.MINUTE && 0 <= value && value <= 59) return true;
		else if (type == Calendar.DAY_OF_MONTH && 0 < value && value <= 31) return true;
		return false;
	}
	
	
	/**
	 * @return boolean
	 */
	public boolean isAnual() {
		return anual;
	}

	/**
	 * @param anuary boolean
	 */
	public void setAnual(boolean anuary) {
		this.anual = anuary;
	}

	/**
	 * @return boolean
	 */
	public boolean isDiary() {
		return diary;
	}

	/**
	 * @param diary boolean
	 */
	public void setDiary(boolean diary) {
		this.diary = diary;
	}

	/**
	 * @return boolean
	 */
	public boolean isMonthly() {
		return monthly;
	}

	/**
	 * @param monthly boolean
	 */
	public void setMonthly(boolean monthly) {
		this.monthly = monthly;
	}

	/**
	 * @return boolean
	 */
	public boolean isWeekly() {
		return weekly;
	}

	/**
	 * @param weekly boolean
	 */
	public void setWeekly(boolean weekly) {
		this.weekly = weekly;
	}

	/**
	 * @return boolean
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled boolean
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return boolean
	 */
	public boolean isOnce() {
		return once;
	}

	/**
	 * @param once boolean
	 */
	public void setOnce(boolean once) {
		this.once = once;
	}

	/**
	 * @param starts calendar to get start or stop calendar
	 * @return Calendar 
	 */
	public Calendar getCalendar(boolean starts) {
		if (starts)
			return start;
		else 
			return stop;
	}

	/**
	 * @param calendar calendar to set for start or stop schedule vigilance
	 * @param starts boolean 
	 */
	public void setCalendar(Calendar calendar, boolean starts) {
		if (starts)
			this.start = calendar;
		else
			this.stop = calendar;
	}   
}
