package pfc.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Logger;
import gnu.io.NoSuchPortException;
import pfc.control.Controller;
import pfc.control.RegAlarm;
import pfc.control.ScheduleProgram;
import pfc.control.configuration.Configuration;
import pfc.control.constants.Operations;
import pfc.route.Route;
import pfc.service.interfaces.IService;
import pfc.video.VideoController;


/**
 * Implements the interface Service for service the server 
 * for RMI client.
 * @author Rodrigo Jimenez
 * @version 1.0
 *
 */
public class Service implements IService {


	Controller controller = null;
	VideoController video = null;


	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>sampleproject.gui</code>.
	 */
	private Logger log = Logger.getLogger("pfc.service");

	/**
	 * DataImpl default constructor.
	 * @param commport serial port name
	 * @throws NoSuchPortException the port is in use or is not found
	 * @throws Exception other exception
	 */
	public Service(String commport) throws NoSuchPortException, Exception {
		controller = new Controller(commport,false);
		log.info("Created new local Controller");
	}

	/**
	 * DataImpl default constructor.
	 * @param commport serial port name
	 * @param device camera device name
	 * @param rtpport rtp port 
	 * @throws NoSuchPortException NoSuchPortException 
	 * @throws IOException IOException   
	 * @throws Exception Exception 
	 * @throws RemoteException Thrown if a <code>DataImpl</code>
	 * instance cannot be created.
	 */
	public Service(String commport, String device, String rtpport) throws NoSuchPortException, IOException, Exception {
		video = new VideoController(device,rtpport);
		controller = new Controller(commport,true);
		log.info("Created new local Controller");
	}

	public long takeRobotControl() throws RemoteException, IOException {
		log.entering("Service","startRobotMovement");
		log.exiting("Service","startRobotMovement");
		return controller.takeRobotControl();
	}

	public boolean releaseRobotControl(long lockCookie) throws RemoteException, IOException {
		log.entering("Service","stopRobotMovement");
		log.exiting("Service","stopRobotMovement");
		return controller.releaseRobotControl(lockCookie);
	}

	public void forwardRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("Service","forwardRobotMovement");
		controller.forwardRobotMovement(value);
		log.exiting("Service","forwardRobotMovement");
	}

	public void backwardRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("Service","backwardRobotMovement");
		controller.backwardRobotMovement(value);
		log.exiting("Service","backwardRobotMovement");
	}

	public void rightRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("Service","rightRobotMovement");
		controller.rightRobotMovement(value);
		log.exiting("Service","rightRobotMovement");

	}

	public void leftRobotMovement(boolean value) throws RemoteException, IOException {
		log.entering("Service","leftRobotMovement");
		controller.leftRobotMovement(value);
		log.exiting("Service","leftRobotMovement");
	}


	public void upCameraMovement(boolean value) throws RemoteException, IOException {
		log.entering("Service","upCameraMovement");
		controller.upCameraMovement(value);
		log.exiting("Service","upCameraMovement");
	}

	public void downCameraMovement(boolean value) throws RemoteException, IOException{
		log.entering("Service","downCameraMovement");
		controller.downCameraMovement(value);
		log.exiting("Service","downCameraMovement");
	}

	public void rightCameraMovement(boolean value) throws RemoteException, IOException{
		log.entering("Service","rightCameraMovement");
		controller.rightCameraMovement(value);
		log.exiting("Service","rightCameraMovement");
	}

	public void leftCameraMovement(boolean value) throws RemoteException, IOException{
		log.entering("Service","leftCameraMovement");
		controller.leftCameraMovement(value);
		log.exiting("Service","leftCameraMovement");
	}

	
	public void centerCameraMovement() throws RemoteException, IOException {
		log.entering("Service","centerCameraMovement");
		controller.centerCameraMovement();
		log.exiting("Service","centerCameraMovement");
	}

	public RegAlarm getMoveAlarm() throws RemoteException{
		log.entering("Service","getMoveAlarm");
		log.exiting("Service","getMoveAlarm");
		return  controller.getMoveAlarm();
	}

	public RegAlarm getTermalAlarm() throws RemoteException{
		log.entering("Service","getTermalAlarm");
		log.exiting("Service","getTermalAlarm");
		return controller.getTermalAlarm();
	}

	
	public RegAlarm getDistanceAlarm() throws RemoteException{
		log.entering("Service","getDistanceAlarm");
		log.exiting("Service","getDistanceAlarm");
		return controller.getDistanceAlarm();
	}
	
	public RegAlarm getLowVoltageAlarm() throws RemoteException{
		log.entering("Service","getLowVoltageAlarm");
		log.exiting("Service","getLowVoltageAlarm");
		return controller.getLowVoltageAlarm();
	}

	public Configuration getConf() throws RemoteException {
		log.entering("Service","getConf");
		log.exiting("Service","getConf");
		return controller.getConf();
	}

	public void setConf(Configuration conf) throws RemoteException, IOException {
		log.entering("Service","setConf");
		controller.setConfiguration(conf);
		log.exiting("Service","setConf");
	}

	public boolean isCamEnabled() throws RemoteException {
		log.entering("Service","isCamEnabled");
		log.exiting("Service","isCamEnabled");
		return controller.isCamEnabled();
	}

	public void setCamEnabled(boolean camEnabled) throws RemoteException {
		log.entering("Service","setCamEnabled");
		controller.setCamEnabled(camEnabled);
		log.exiting("Service","setCamEnabled");
	}

	public void start() throws RemoteException, IOException {
		log.entering("Service","start");
		controller.start();
		log.exiting("Service","start");
	}
	
	public boolean isStarted() throws RemoteException, IOException {
		log.entering("Service","isStarted");
		log.exiting("Service","isStarted");
		return controller.isStarted();
	}

	public void stop() throws RemoteException, IOException {
		log.entering("Service","stop");
		controller.stop();
		log.exiting("Service","stop");
	}


	public ScheduleProgram getSchedule() throws RemoteException {
		log.entering("Service","getSchedule");
		log.exiting("Service","getSchedule");
		return controller.getSchedule();
	}

	public void setSchedule(ScheduleProgram schedule) throws RemoteException {
		log.entering("Service","setSchedule");
		controller.setSchedule(schedule);
		log.exiting("Service","setSchedule");
	}


	public RegAlarm getHeartbeatAlarm() throws RemoteException {
		log.entering("Service","getHeartbeatAlarm");
		log.exiting("Service","getHeartbeatAlarm");
		return controller.getHeartbeatAlarm();
	}


	public String[] getRouteNames() throws RemoteException {
		log.entering("Service","getRouteNames");
		log.exiting("Service","getRouteNames");
		return controller.getRouteNames();
	}
		
	public void addRoute(Route route) throws RemoteException, IOException {
		log.entering("Service","addRoute");
		controller.saveRoute(route);
		log.exiting("Service","addRoute");
	}
	
	public void enableRobotCam(boolean value) throws RemoteException, IOException{
		log.entering("Service","enableRobotCam");
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
		log.exiting("Service","enableRobotCam");
	}
	
	
	public void startRouteDefinition() throws RemoteException, IOException {
		log.entering("Service","startRouteDefinition");
		controller.startRouteDefinition();
		log.exiting("Service","startRouteDefinition");
	}
	
}


