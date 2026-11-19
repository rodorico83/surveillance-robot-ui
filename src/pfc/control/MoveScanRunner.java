package pfc.control;

import java.io.IOException;
import java.util.logging.Logger;
import pfc.control.constants.Operations;

/**
 * Move scan runner implementation
 * @author rodrigo
 *
 */
public class MoveScanRunner implements Runnable{


    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger namespace is <code>suncertify.db</code>.
     */
    private Logger log = Logger.getLogger("pcf.control");

    private Controller controller;

    private Boolean isRunning = false;
    
    private static long moveActivationTime = 3000;


    /**
     * Constructor
     * @param controller pfc.control.Controller
     */
    public MoveScanRunner(Controller controller) {
	this.controller = controller;
    }

    public void run() {
	while(true){
	    if (controller.isStarted() && controller.getConf().isEnableMovementSensor()) {
		try {

		    synchronized(controller.isScanRunning()) {
			isRunning = true;
			if (controller.getTermalScanRunner().isRunning() && controller.isStarted())
			    controller.isScanRunning().wait();

			if (controller.getConf().isEnableMovementSensor() && controller.isStarted()) {
			    log.info("Running Move Scan...");
			    Controller.sendOperation(Operations.RUN_MOVE_SCAN);
			    if (!controller.getConf().isEnableStaticMovement()) 
				Thread.sleep(moveActivationTime);
			    Controller.sendOperation(Operations.CENTER_MOVE_CAMERA);
			    Thread.sleep(controller.getConf().getPeriodOrientationMovementSensor());
			    Controller.sendOperation(Operations.LEFT_MOVE_CAMERA);
			    Thread.sleep(controller.getConf().getPeriodOrientationMovementSensor());
			    Controller.sendOperation(Operations.RIGHT_MOVE_CAMERA);
			    Thread.sleep(controller.getConf().getPeriodOrientationMovementSensor());
			    Controller.sendOperation(Operations.CENTER_MOVE_CAMERA);
			    Thread.sleep(2000);
			    Controller.sendOperation(Operations.STOP_MOVE_SCAN);
			    log.info("Finalized Move Scan...");
			}

			isRunning = false;
			controller.isScanRunning().notifyAll();

		    }
		    synchronized (this) {
			this.notifyAll();
		    }
		    Thread.sleep(controller.getConf().getPeriodMovementSensor());

		} catch (IOException e) {
		    e.printStackTrace();
		} catch (InterruptedException e) {
		    isRunning = false;
		    log.info("Move Scan Runner Interrupted...");
		}
	    }
	    else {
		synchronized(this){
		    isRunning = false;
		    try {
			log.info("Move Scan Runner waiting to start or to enable the scan");
			isRunning = false;
			this.wait();
			log.info("Move Scan Runner started");
		    } catch (InterruptedException e) {
			log.info("Termal Scan Runner Interrupted");
		    }

		}
	    }
	}
    }

    /**
     * To know if the move scan is running
     * @return boolean
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
