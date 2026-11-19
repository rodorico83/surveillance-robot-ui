package pfc.control;

import java.io.IOException;
import java.util.logging.Logger;

import pfc.control.constants.Operations;

/**
 * Termal Scan runner implementation
 * @author rodrigo
 *
 */
public class TermalScanRunner implements Runnable{


    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger namespace is <code>suncertify.db</code>.
     */
    private Logger log = Logger.getLogger("pcf.control");

    private Controller controller;

    private Boolean isRunning = false;


    /**
     * Constructor
     * @param controller  pfc.control.Controller
     */
    public TermalScanRunner(Controller controller) {
	this.controller = controller;
    }


    public void run() {
	while(true){
	    if (controller.isStarted() && controller.getConf().isEnableTermalSensor() && controller.getConf().getPeriodTermalSensor() != 0) {
		try {
		    synchronized(controller.isScanRunning()) {
			isRunning = true;
			if (controller.getMoveScanRunner().isRunning() && controller.isStarted())
			    controller.isScanRunning().wait();
			if (controller.getConf().isEnableTermalSensor() && controller.isStarted() && controller.getConf().getPeriodTermalSensor() != 0) {
			    log.info("Running Termal Scan...");
			    Controller.sendOperation(Operations.RUN_TERMAL_SCAN);
			    Controller.sendOperation(Operations.CENTER_MOVE_CAMERA);
			    Thread.sleep(controller.getConf().getPeriodOrientationTermalSensor());
			    Controller.sendOperation(Operations.LEFT_MOVE_CAMERA);
			    Thread.sleep(controller.getConf().getPeriodOrientationTermalSensor());
			    Controller.sendOperation(Operations.RIGHT_MOVE_CAMERA);
			    Thread.sleep(controller.getConf().getPeriodOrientationTermalSensor());
			    Controller.sendOperation(Operations.CENTER_MOVE_CAMERA);
			    Thread.sleep(2000);
			    Controller.sendOperation(Operations.STOP_TERMAL_SCAN);
			    log.info("Finalized Termal Scan...");

			}
			isRunning = false;
			controller.isScanRunning().notifyAll();
		    }

		    Thread.sleep(controller.getConf().getPeriodTermalSensor());

		} 
		catch (InterruptedException e) {
		    isRunning = false;
		    log.info("Termal Scan Runner Interrupted");
		}catch (IOException e) {
		    e.printStackTrace();
		} 


	    }
	    else {
		synchronized(this){
		    isRunning = false;
		    try {
			log.info("Termal Scan Runner waiting to start or to enable the scan");
			isRunning = false;
			this.wait();
			log.info("Termal Scan Runner started");
		    } catch (InterruptedException e) {
			log.info("Termal Scan Runner Interrupted");
		    }

		}
	    }
	}
    }


    /**
     * To know if the termal scan is running
     * @return Boolean
     */
    public Boolean isRunning() {
	return isRunning;
    }


    /**
     * @param isRunning Boolean
     */
    public void setIsRunning(Boolean isRunning) {
        this.isRunning = isRunning;
    }


}
