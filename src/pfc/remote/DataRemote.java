package pfc.remote;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;
import pfc.control.Controller;
import pfc.control.RegAlarm;
import pfc.control.ScheduleProgram;
import pfc.control.configuration.Configuration;
import pfc.control.constants.Operations;
import pfc.remote.interfaces.IDataRemote;
import pfc.route.Route;
import pfc.video.VideoController;



/** A DataImpl object is the implementation of the DataRemote
 * interface. This class is an RMI implementation.
 *
 * A DataImpl object contains a reference to the db.
 * This class acts primarily as a wrapper for the database.
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public class DataRemote 
//implements Serializable, IDataRemote {
extends UnicastRemoteObject
implements IDataRemote {

    public static final int REMOTE_OBJECT_PORT = 1100;

	/**
	 * A version number for this class so that serialization can occur
	 * without worrying about the underlying class changing between
	 * serialization and deserialization.
	 */
	private static final long serialVersionUID = 5164L;

	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.remote</code>.
	 */
	private static Logger log = Logger.getLogger("pfc.remote");


	/**
	 * The control handle.
	 */
	private Controller controller = null;


	/**
	 * DataImpl default constructor.
	 * @throws RemoteException Thrown if a <code>DataImpl</code>
	 * instance cannot be created.
	 */
	public DataRemote() throws RemoteException {
		this(REMOTE_OBJECT_PORT);
	}

	public DataRemote(int exportPort) throws RemoteException {
		super(exportPort);
		try {
			controller = new Controller();
		}  catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
	}

	public RegAlarm getMoveAlarm() throws RemoteException {
		log.entering("DataRemote", "readMoveAlarm");
		RegAlarm result = controller.getMoveAlarm();
		log.exiting("DataRemote", "readMoveAlarm");
		return result;
	}

	public RegAlarm getTermalAlarm() throws RemoteException {
		log.entering("DataRemote", "readTermalAlarm");
		RegAlarm result = controller.getTermalAlarm();
		log.exiting("DataRemote", "readTermalAlarm");
		return result;
	}
	
	public RegAlarm getDistanceAlarm() throws RemoteException{
		log.entering("DataRemote","getDistanceAlarm");
		log.exiting("DataRemotee","getDistanceAlarm");
		return controller.getDistanceAlarm();
	}
	
	public RegAlarm getLowVoltageAlarm() throws RemoteException{
		log.entering("DataRemote","getLowVoltageAlarm");
		log.exiting("DataRemote","getLowVoltageAlarm");
		return controller.getLowVoltageAlarm();
	}

	public long takeRobotControl() throws RemoteException, IOException {
		log.entering("DataRemote", "startRobotMovement");
		log.exiting("DataRemote", "startRobotMovement");
		return controller.takeRobotControl();
	}

	public boolean releaseRobotControl(long lockCookie) throws RemoteException, IOException {
		log.entering("DataRemote", "stopRobotMovement");
		log.exiting("DataRemote", "stopRobotMovement");
		return controller.releaseRobotControl(lockCookie);
	}

	public void forwardRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("DataRemote", "forwardRobotMovement");
		controller.forwardRobotMovement(value);
		log.exiting("DataRemote", "forwardRobotMovement");
	}

	public void backwardRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("DataRemote", "backwardRobotMovement");
		controller.backwardRobotMovement(value);
		log.exiting("DataRemote", "backwardRobotMovement");

	}

	public void rightRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("DataRemote", "rightRobotMovement");
		controller.rightRobotMovement(value);
		log.exiting("DataRemote", "rightRobotMovement");

	}

	public void leftRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("DataRemote", "leftRobotMovement");
		controller.leftRobotMovement(value);
		log.exiting("DataRemote", "leftRobotMovement");
	}
	
    public void upCameraMovement(boolean value) throws RemoteException, IOException {
    	log.entering("DataRemote","upCameraMovement");
		controller.upCameraMovement(value);
		log.exiting("DataRemote","upCameraMovement");
    }
    
    public void downCameraMovement(boolean value) throws RemoteException, IOException{
    	log.entering("DataRemote","downCameraMovement");
		controller.downCameraMovement(value);
		log.exiting("DataRemote","downCameraMovement");
    }
    
    public void rightCameraMovement(boolean value) throws RemoteException, IOException{
    	log.entering("DataRemote","rightCameraMovement");
		controller.rightCameraMovement(value);
		log.exiting("DataRemote","rightCameraMovement");
    }
    
    public void leftCameraMovement(boolean value) throws RemoteException, IOException{
    	log.entering("DataRemote","leftCameraMovement");
		controller.leftCameraMovement(value);
		log.exiting("DataRemote","leftCameraMovement");
    }
    
	public void centerCameraMovement() throws RemoteException, IOException {
		log.entering("DataRemote","centerCameraMovement");
		controller.centerCameraMovement();
		log.exiting("DataRemote","centerCameraMovement");
	}

	public Configuration getConf() throws RemoteException {
		log.entering("DataRemote", "getConf");
		log.exiting("DataRemote", "getConf");
		return controller.getConf();
	}
	
	public void setConf(Configuration conf) throws RemoteException, IOException{
		log.entering("DataRemote","setConf");
		controller.setConfiguration(conf);
		log.exiting("DataRemote","setConf");
	}

	public boolean isCamEnabled() throws RemoteException {
		log.entering("DataRemote","isCamEnabled");
		log.exiting("DataRemote","isCamEnabled");
		return controller.isCamEnabled();
	}

	public void setCamEnabled(boolean camEnabled) throws RemoteException {
		log.entering("DataRemote","setCamEnabled");
		controller.setCamEnabled(camEnabled);
		log.exiting("DataRemote","setCamEnabled");
	}

	public void start() throws RemoteException, IOException {
		log.entering("DataRemote","start");
		controller.start();
		log.exiting("DataRemote","start");
	}
	
	public boolean isStarted() throws RemoteException, IOException {
		log.entering("DataRemote","isStarted");
		log.exiting("DataRemote","isStarted");
		return controller.isStarted();
	}

	public void stop() throws RemoteException, IOException {
		log.entering("DataRemote","stop");
		controller.stop();
		log.exiting("DataRemote","stop");
	}

	public ScheduleProgram getSchedule() throws RemoteException {
		log.entering("DataRemote","getSchedule");
		log.exiting("DataRemote","getSchedule");
		return controller.getSchedule();
	}

	public void setSchedule(ScheduleProgram schedule) throws RemoteException {
		log.entering("DataRemote","setSchedule");
		controller.setSchedule(schedule);
		log.exiting("DataRemote","setSchedule");
	}

	public RegAlarm getHeartbeatAlarm() throws RemoteException {
		log.entering("DataRemote","getHeartbeatAlarm");
		log.exiting("DataRemote","getHeartbeatAlarm");
		return controller.getHeartbeatAlarm();
	}
	
	
	public String[] getRouteNames() throws RemoteException {
		log.entering("DataRemote","getRouteNames");
		log.exiting("DataRemote","getRouteNames");
		return controller.getRouteNames();
	}
	
	public void addRoute(Route route) throws RemoteException, IOException {
		log.entering("DataRemote","addRoute");
		controller.saveRoute(route);
		log.exiting("DataRemote","addRoute");
	}
	
	public void enableRobotCam(boolean value) throws RemoteException, IOException{
		log.entering("DataRemote","enableRobotCam");
		Controller.setActiveVideoClient(value);
		if (value) {
			Controller.sendOperation(Operations.ENABLE_CAM);
			Controller.sendOperation(Operations.CENTER_MOVE_CAMERA);
			VideoController.requestVideoSourceReconnect();
		}
		else {
			if (controller.isStarted() && controller.getConf().isEnableSaveVideoAllTime()) return;
			else Controller.sendOperation(Operations.DISABLE_CAM);
			
		}
		log.exiting("DataRemote","enableRobotCam");
	}
	
	
	public void startRouteDefinition() throws RemoteException, IOException {
		log.entering("DataRemote","startRoute");
		controller.startRouteDefinition();
		log.exiting("DataRemote","startRoute");
	}
	
}
