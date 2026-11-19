package pfc.control;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Logger;


/**
 * Schedule Runner implementation
 * @author rodrigo
 *
 */
public class ScheduleProgramRunner implements Serializable, Runnable{

	/**
	 * Serial Id for serialization
	 */
	private static final long serialVersionUID = 3600006569899290098L;


	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.db</code>.
	 */
	private Logger log = Logger.getLogger("pcf.control");


	private Controller controller = null;
	private ScheduleProgram scheduleProgram = null;

	/**
	 * Default constructor
	 * @param controller Controller
	 */
	public ScheduleProgramRunner(Controller controller) {
		if (scheduleProgram == null) {
			this.controller = controller;
			scheduleProgram = new ScheduleProgram();
		}
		else
			log.info("Trying to create another ScheduleProgramRunner");
	}



	/**
	 * @return ScheduleProgram
	 */
	public ScheduleProgram getScheduleProgram() {
		return scheduleProgram;
	}


	/**
	 * @param scheduleProgram ScheduleProgram
	 */
	public void setScheduleProgram(ScheduleProgram scheduleProgram) {
		this.scheduleProgram = scheduleProgram;
	}




	public void run() {
		Calendar now;
		boolean starts = false;

		while (true) {


			try {

				now = Calendar.getInstance();
				Calendar start = scheduleProgram.getCalendar(true);
				Calendar stop = scheduleProgram.getCalendar(false);
				if (!scheduleProgram.isEnabled()) {
					log.info("Schedule Thread Waiting for a new Configuration...");
					synchronized(this) {
						this.wait();
					}
				}
				else if (!starts) {

					if (scheduleProgram.isOnce() && 
							now.get(Calendar.HOUR) == start.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == start.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_MONTH) == start.get(Calendar.DAY_OF_MONTH) &&
							now.get(Calendar.MONTH) == start.get(Calendar.MONTH) &&
							now.get(Calendar.YEAR) == start.get(Calendar.YEAR)) starts = true;
					else if (scheduleProgram.isDiary() && 
							now.get(Calendar.HOUR) == start.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == start.get(Calendar.MINUTE) ) starts = true;
					else if (scheduleProgram.isWeekly() &&
							now.get(Calendar.HOUR) == start.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == start.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_WEEK) == start.get(Calendar.DAY_OF_WEEK)) starts = true;
					else if (scheduleProgram.isMonthly() && 
							now.get(Calendar.HOUR) == start.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == start.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_MONTH) == start.get(Calendar.DAY_OF_MONTH)) starts = true;
					else if (scheduleProgram.isAnual() &&
							now.get(Calendar.HOUR) == start.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == start.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_MONTH) == start.get(Calendar.DAY_OF_MONTH) &&
							now.get(Calendar.MONTH) == start.get(Calendar.MONTH)) starts = true;

					if (!controller.isStarted() && starts) {
						log.info("Scheduling Starts Vigilance at " + now.getTime().toString());
						this.controller.start();
					}
				}

				else if (starts) {

					if (scheduleProgram.isOnce() && 
							now.get(Calendar.HOUR) == stop.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == stop.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_MONTH) == stop.get(Calendar.DAY_OF_MONTH) &&
							now.get(Calendar.MONTH) == stop.get(Calendar.MONTH) &&
							now.get(Calendar.YEAR) == stop.get(Calendar.YEAR)) starts = false;
					else if (scheduleProgram.isDiary() && 
							now.get(Calendar.HOUR) == stop.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == stop.get(Calendar.MINUTE) ) starts = false;
					else if (scheduleProgram.isWeekly() &&
							now.get(Calendar.HOUR) == stop.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == stop.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_WEEK) == stop.get(Calendar.DAY_OF_WEEK)) starts = false;
					else if (scheduleProgram.isMonthly() && 
							now.get(Calendar.HOUR) == stop.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == stop.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_MONTH) == stop.get(Calendar.DAY_OF_MONTH)) starts = false;
					else if (scheduleProgram.isAnual() &&
							now.get(Calendar.HOUR) == stop.get(Calendar.HOUR) &&
							now.get(Calendar.MINUTE) == stop.get(Calendar.MINUTE) &&
							now.get(Calendar.DAY_OF_MONTH) == stop.get(Calendar.DAY_OF_MONTH) &&
							now.get(Calendar.MONTH) == stop.get(Calendar.MONTH)) starts = false;

					if (controller.isStarted() && !starts) {
						log.info("Scheduling Stops Vigilance at " + now.getTime().toString());
						this.controller.stop();
					}


				}
				Thread.sleep(60000);
			}
			catch (InterruptedException e) {
				log.info("Interrupted Schedule Thread...");
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}
}
