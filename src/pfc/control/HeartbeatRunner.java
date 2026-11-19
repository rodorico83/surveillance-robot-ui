package pfc.control;

import java.io.IOException;
import java.util.logging.Logger;

import pfc.control.constants.Operations;
/**
 * Heartbear Runner Representation
 * @author rodrigo
 *
 */
public class HeartbeatRunner implements Runnable{

	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.db</code>.
	 */
	private Logger log = Logger.getLogger("pcf.control");

	private Controller controller;
	private boolean lastHeartbeat;
	
	
	/**
	 * Constructor
	 * @param controller server controller
	 */
	public HeartbeatRunner(Controller controller) {
		lastHeartbeat = true;
		this.controller = controller;
	}
	
	public void run() {
		boolean started = false;
		while(true){
			if (controller.isStarted() && !controller.getMoveScanRunner().isRunning()) {
			//if (controller.isStarted()) {
				if (started && !lastHeartbeat) 
					controller.setHeartbeatAlarmDetected();
				started = true;
				try {
					lastHeartbeat = false;
					Controller.sendOperation(Operations.HEARTBEAT);
					Thread.sleep(controller.getConf().getPeriodHeartbeat());
				} catch (InterruptedException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
				}
			}
			else if (!controller.isStarted()) {
				synchronized(this){
					try {
						log.info("Heartbeat Runner waiting to start");
						started = false;
						this.wait();
						log.info("Heartbeat Runner started");
					} catch (InterruptedException e) {
						log.info("Heartbeat Runner started");
						e.printStackTrace();
					}
					
				}
			}
			else if (controller.getMoveScanRunner().isRunning()) {
			    synchronized(controller.getMoveScanRunner()) {
				try {
				    log.info("Wating to finalize Move Scan");
				    controller.getMoveScanRunner().wait();
				} catch (InterruptedException e) {
				    log.severe(e.getMessage());
				    e.printStackTrace();
				}
			    }
			}
		}
	}

	/**
	 * to kwon if the las request heartbeat was sucesfully
	 * @return boolean
	 */
	public boolean isLastHeartbeat() {
		return lastHeartbeat;
	}
	
	/**
	 * set the result of the last hearbeat request
	 * @param lastHeartbeat boolean
	 */
	public void setLastHeartbeat(boolean lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}

}
