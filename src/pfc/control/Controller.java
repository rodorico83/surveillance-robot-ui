package pfc.control;

import gnu.io.NoSuchPortException;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import pfc.control.configuration.Configuration;
import pfc.control.constants.Constants;
import pfc.route.Route;
import pfc.rs232.SerialCommunication;
import pfc.rs232.SerialReader;
import pfc.util.Utils;
import pfc.video.VideoController;
import pfc.video.VideoTransmit;
import pfc.control.constants.Operations;
import pfc.control.configuration.SavedConfiguration;



/**
 * Implements the robot control interface
 * @author Rodrigo Jimenez Rodriguez
 * @version 1.0
 */
public class Controller {


	/**
	 * The class that handles all our physical access to the robot configuration
	 */
	private static Configuration conf = null;

	/**
	 * Register for move alarm
	 */
	private static RegAlarm moveAlarm;

	/**
	 * Register for termal alarm
	 */
	private static RegAlarm termalAlarm;

	/**
	 * RS232 Thread reader
	 */
	private static Thread threadReader;

	/**
	 * Serial Communication access
	 */
	private static SerialCommunication serial;

	/**
	 * Runner Instance of a Serial Reader
	 */
	private static SerialReader serialReader;

	/**
	 * Runner of heartbeat
	 */
	private static HeartbeatRunner heartbeatRunner;

	/**
	 * Thread for run a pfc.control.Controller.heartbeatRunner
	 *
	 */
	private static Thread threadHeartbeat;

	/**
	 * Register for heartbeat alarm
	 */
	private static RegAlarm heartbeatAlarm;

	/**
	 * Register for distance proximity alarm
	 */
	private static RegAlarm distanceAlarm;


	/**
	 * Register for low voltage alarm
	 */
	private static RegAlarm lowVoltageAlarm;

	/**
	 * Runner of move scan controller
	 */
	private static MoveScanRunner moveScanRunner;

	/**
	 * Thread for run a pfc.control.Controller.moveScanRunner
	 */
	private static Thread threadMoveScan;

	/**
	 * Runner for termal scan controller
	 */
	private static TermalScanRunner termalScanRunner;

	/**
	 * Thread for run a pfc.control.Controller.termalScanRunner
	 */
	private static Thread threadTermalScan;


	/**
	 * Thread for run a pfc.control.Controller.scheduleRunner
	 */
	private static Thread threadSchedule;

	/**
	 * Runner for Schedule configuration
	 */
	private static ScheduleProgramRunner scheduleRunner;

	/**
	 * Indicates if the control of the robot is taken by a client
	 */
	private static boolean isLocked;


	/**
	 * Indicates if is going to use a cam or not
	 */
	private static boolean camEnabled;

	/**
	 * Indicates if the vigilance is started
	 */
	private static boolean started;


	/**
	 * Lock Key when the robot control is taken by a client
	 */
	private static long lockId = 0;

	/**
	 * This variable saves an instance of the video Controller
	 */
	private static VideoController videoController;

	/**
	 * Runner for a save video alarm
	 */
	private static SaveVideoRunner saveVideoRunner;

	/**
	 * Thread for run a pfc.control.Controller.saveVideoRunner
	 */
	private static Thread threadSaveVideo;

	/**
	 * Record for all the routes saved
	 */
	private static HashMap<String,Route> routes;

	/**
	 * For recording a actual client saving route
	 */
	private static Route routeDefinition;


	/**
	 * For syncrhonize the termal and scan runner
	 */
	private static Boolean scanRunning;

	/**
	 * indicate if is actually programming a new route
	 */
	private static boolean programmingRoute;


	/**
	 * indicate if is actually there a client at the RTP Sesion
	 */
	private static boolean activeVideoClient;

	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.db</code>.
	 */
	private Logger log = Logger.getLogger("pcf.control");


	/**
	 * load saved configuration
	 */
	private static SavedConfiguration properties = null;


	/**
	 * Default Constructor for the clients
	 */
	public Controller() {
		log.info("Client Controller Instance");
	}

	/**
	 * Constructor which get the serial port comm
	 * @param commport comport name
	 * @param camEnabled boolean
	 * @throws NoSuchPortException If the port is not found or is in use
	 * @throws Exception other exception
	 */
	public Controller(String commport, boolean camEnabled) throws NoSuchPortException, Exception {
		log.info("Controller Constructor: " + commport );

		Controller.routes = new HashMap<String,Route>();
		Controller.isLocked = false;
		Controller.started = false;
		Controller.moveAlarm = null;
		Controller.termalAlarm = null;

		Controller.conf = new Configuration();

		Controller.serial = new SerialCommunication(commport);
		Controller.serialReader = new SerialReader(this, serial);
		Controller.threadReader = new Thread(serialReader);
		Controller.threadReader.start();

		Controller.heartbeatRunner = new HeartbeatRunner(this);
		Controller.threadHeartbeat = new Thread(heartbeatRunner);
		Controller.threadHeartbeat.start();

		Controller.moveScanRunner = new MoveScanRunner(this);
		Controller.threadMoveScan = new Thread(moveScanRunner);
		Controller.threadMoveScan.start();

		Controller.termalScanRunner = new TermalScanRunner(this);
		Controller.threadTermalScan = new Thread(termalScanRunner);
		Controller.threadTermalScan.start();

		Controller.camEnabled = camEnabled;
		Controller.scheduleRunner = new ScheduleProgramRunner(this);
		Controller.threadSchedule = new Thread(scheduleRunner);
		Controller.threadSchedule.start();


		Controller.programmingRoute= false;
		Controller.scanRunning = false;

		if (Controller.camEnabled) {
			Controller.videoController = new VideoController();
			saveVideoRunner = new SaveVideoRunner(this);
		}
		else {
			conf.setEnableSaveVideoAlarm(false);
			conf.setEnableSaveVideoAllTime(false);
		}

		properties = SavedConfiguration.getSavedConfiguration();

		Controller.routeDefinition = null;
		loadRoutes();
	}


	/**
	 * Load Routes from the xml definitions
	 */
	private void loadRoutes() {
		log.entering("Controller", "loadRoutes");
		String routePath = properties.getParameter(SavedConfiguration.ROUTE_PATH);

		File namesFile = new File(routePath + "\\Routes.xml");
		String[] routeNames = Route.getRouteNames(namesFile);
		if (routeNames != null){
			for (int i = 0; i < routeNames.length; i++){
				if (routeNames[i] != null){
					File routeFile = new File(routePath + "\\" + routeNames[i] + ".xml");
					Route route;
					try {
						route = Route.getRoute(routeFile);
						routes.put(routeNames[i], route);
					} catch (Exception e) {
						log.severe(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		log.exiting("Controller", "loadRoutes");
	}

	/**
	 * Get all the routes names defined at the server
	 * @return Route names
	 */
	public String[] getRouteNames() {
		log.entering("Controller", "getRouteNames");
		Set<String> set = Controller.routes.keySet();
		set.iterator();
		Iterator<String> iterator = set.iterator();

		String[] routeNames = new String[set.size()];
		int i = 0;
		while (iterator.hasNext()){
			String route = (String) iterator.next();
			if (route != null) {
				routeNames[i] = route;
				i++;
			}
		}
		log.exiting("Controller", "getRouteNames");
		return routeNames;
	}

	/**
	 * save a new route at the server
	 * @param route Route to save
	 * @throws IOException serial communication error
	 */
	public synchronized void saveRoute(Route route) throws IOException{
		boolean overwrite = false;
		log.entering("Controller", "addRoute");
		sendOperation(Operations.ROUTE_END);
		if (routes.containsKey(route.getName())) {
			log.info("The route " + route.getName() + " will be overwrited");
			overwrite = true;
		}
		routeDefinition.setName(route.getName());
		Controller.routes.put(route.getName(), routeDefinition);
		saveRouteToDisk(routeDefinition, overwrite);
		log.entering("Controller", "addRoute");
	}


	/**
	 * Save a route to an xml
	 * @param route Route to sve
	 * @param overwrite overwrite an exiting route
	 */
	public synchronized void saveRouteToDisk(Route route, boolean overwrite){
		log.entering("Controller", "saveRouteToDisk");
		String routePath = properties.getParameter(SavedConfiguration.ROUTE_PATH);
		File file = new File(routePath + "\\" + route.getName() + ".xml");
		log.info("Saving Route " + route.getName() + " to disk...");
		Route.saveRoute(route, file);
		if (!overwrite) {
			File namesFile = new File(routePath + "\\" + "Routes.xml");
			Route.saveRouteName(route.getName(), namesFile);
		}
		log.entering("Controller", "saveRouteToDisk");
	}

	/**
	 * Remove a route from the server
	 * @param route Route to remove
	 */
	public synchronized void removeRoute(Route route){
		log.entering("Controller", "addRoute");
		routes.remove(route.getName());
		log.entering("Controller", "addRoute");
	}


	/**
	 * Set the new move alarm detection.
	 */
	public void setMoveAlarmDetected() {
		log.entering("Controller", "setMoveAlarmDetected");
		moveAlarm = new RegAlarm(RegAlarm.MOVE_TYPE, new Date());
		log.info("Move Alarm detected " + moveAlarm.getTimeOcurrence().toString());

		if (camEnabled && conf.isEnableSaveVideoAlarm() && !saveVideoRunner.isRecording()){
			log.info("Saving Move Alarm Video");
			String saveVideoLocation = properties.getParameter(SavedConfiguration.SAVE_VIDEO_LOCATION) + "//moveAlarm-" + Utils.getStringDate(moveAlarm.getTimeOcurrence()) + ".avi";
			saveVideoRunner.setSaveVideoPath(saveVideoLocation);
			Controller.threadSaveVideo = new Thread(saveVideoRunner);
			threadSaveVideo.start();

		}
		try {
			if (conf.isEnableEmail())
				Utils.sendMail(properties, "Move Alarm Detected", conf.getEmailReceivers(), "Move Alarm Detected At " + moveAlarm.getTimeOcurrence().toString());
		} catch (Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
		log.exiting("Controller", "setMoveAlarmDetected");
	}



	/**
	 * Get the last move alarm record
	 * @return Move alarm record
	 */
	public RegAlarm getMoveAlarm() {
		log.entering("Controller", "getMoveAlarm");
		log.exiting("Controller", "getMoveAlarm");
		return moveAlarm;
	}


	/**
	 * Set the distance proximity detection at this moment
	 * @param distance proximity distance detected in centimeters
	 */
	public void setDistanceAlarmDetected(String distance) {

		log.entering("Controller", "setDistanceAlarmDetected");

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(false);
		distance = nf.format((new Float((( new Float(Long.valueOf(distance))) / new Float(29)))));

		Controller.started = false;
		log.info("Stopping Vigilance");

		if (moveScanRunner.isRunning())
			threadMoveScan.interrupt();
		if (termalScanRunner.isRunning())
			threadTermalScan.interrupt();

	/*if (conf.isEnableSaveVideoAllTime()) {
	    synchronized(saveVideoRunner){
		saveVideoRunner.notify();
	    }
	}*/

		log.info("Robot Controller Stop the Robot Vigilance");

		distanceAlarm = new RegAlarm(RegAlarm.DISTANCE_TYPE, new Date());
		distanceAlarm.setDistance(distance);
		log.info("Distance Alarm detected: " + distance + " cm " + distanceAlarm.getTimeOcurrence().toString());

		if (camEnabled && conf.isEnableSaveVideoAlarm() && !saveVideoRunner.isRecording()){
			log.info("Saving Distance Alarm Video");
			String saveVideoLocation = properties.getParameter(SavedConfiguration.SAVE_VIDEO_LOCATION) + "//distanceAlarm-" + Utils.getStringDate(distanceAlarm.getTimeOcurrence()) + ".avi";
			saveVideoRunner.setSaveVideoPath(saveVideoLocation);
			Controller.threadSaveVideo = new Thread(saveVideoRunner);
			threadSaveVideo.start();

		}
		try {
			if (conf.isEnableEmail())
				Utils.sendMail(properties, "Distance Alarm Detected", conf.getEmailReceivers(), "Distance Alarm Detected: " + distance + " cm At " + distanceAlarm.getTimeOcurrence().toString());
		} catch (Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}

		log.exiting("Controller", "setDistanceAlarmDetected");
	}



	/**
	 * Set the low voltage alarm
	 * @param voltage Voltage Value detected in Volt
	 */
	public void setLowVoltageAlarmDetected(String voltage) {
		log.entering("Controller", "setLowVoltageAlarmDetected");

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(false);
		voltage= nf.format( new Float( ( (new Float( Long.valueOf(voltage) * Constants.VREF * Constants.RESISTANCE_DIVISOR)) / (Constants.SCALE_ADC ))));
		//Float ccc = new Float( ( new Float( Long.valueOf(voltage) * Constants.VREF * Constants.RESISTANCE_DIVISOR) ));

		//oltage = String.valueOf(( (Double) Math.floor(((conf.getChargeVoltageValue() * Constants.SCALE_ADC)) / (Constants.VREF * Constants.RESISTANCE_DIVISOR))).longValue());

		lowVoltageAlarm = new RegAlarm(RegAlarm.LOW_VOLTAGE_TYPE, new Date());
		lowVoltageAlarm.setVoltage(voltage);
		log.info("Low Voltage Alarm detected: "  + voltage + " V " + lowVoltageAlarm.getTimeOcurrence().toString());

		try {
			if (conf.isEnableEmail())
				Utils.sendMail(properties, "Low Voltage Alarm Detected", conf.getEmailReceivers(), "Low Voltage Alarm Detected: " + voltage + " V At " + distanceAlarm.getTimeOcurrence().toString());
		} catch (Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}

		log.exiting("Controller", "setLowVoltageAlarmDetected");
	}

	/**
	 * Set the termal alarm detection at this moment
	 * @param captures Temperatures captured separated by commas
	 */
	public void setTermalAlarmDetected(String captures) {
		log.entering("Controller", "setTermalAlarmDetected");
		termalAlarm = new RegAlarm(RegAlarm.TERMAL_TYPE, new Date(), captures);
		log.info("Termal Alarm detected "  + termalAlarm.getTimeOcurrence().toString() + " Captures: " + captures);

		if (camEnabled && conf.isEnableSaveVideoAlarm() && !saveVideoRunner.isRecording()) {
			log.info("Saving Termal Alarm Video");
			String saveVideoLocation = properties.getParameter(SavedConfiguration.SAVE_VIDEO_LOCATION) + "//termalAlarm-" + Utils.getStringDate(termalAlarm.getTimeOcurrence()) + ".avi";
			saveVideoRunner.setSaveVideoPath(saveVideoLocation);
			Controller.threadSaveVideo = new Thread(saveVideoRunner);
			threadSaveVideo.start();
		}

		try {
			if (conf.isEnableEmail())
				Utils.sendMail(properties, "Termal Alarm Detected", conf.getEmailReceivers(), "Termal Alarm Detected At " + termalAlarm.getTimeOcurrence().toString());
		} catch (Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}

		log.exiting("Controller", "setTermalAlarmDetected");
	}

	/**
	 * Get the last termal alarm record
	 * @return Record for last termal alarm
	 */
	public RegAlarm getTermalAlarm() {
		log.entering("Controller", "getTermalAlarm");
		log.exiting("Controller", "getTermalAlarm");
		return termalAlarm;
	}


	/**
	 * Set the heartbeat alarm detection
	 */
	public void setHeartbeatAlarmDetected() {
		log.entering("Controller", "setHeartbeatAlarmDetected");
		heartbeatAlarm = new RegAlarm(RegAlarm.HEARTBEAT_TYPE, new Date());
		log.info("Heartbeat Alarm detected " + heartbeatAlarm.getTimeOcurrence().toString());

		if (camEnabled && conf.isEnableSaveVideoAlarm()&& !saveVideoRunner.isRecording()) {
			log.info("Saving Heartbeat Alarm Video");
			String saveVideoLocation = properties.getParameter(SavedConfiguration.SAVE_VIDEO_LOCATION) + "//heartbeatAlarm-" + Utils.getStringDate(heartbeatAlarm.getTimeOcurrence()) + ".avi";
			saveVideoRunner.setSaveVideoPath(saveVideoLocation);
			Controller.threadSaveVideo = new Thread(saveVideoRunner);
			threadSaveVideo.start();
		}
		try {
			if (conf.isEnableEmail())
				Utils.sendMail(properties, "Heartbeat Alarm Detected", conf.getEmailReceivers(), "Heartbeat Alarm Detected At " + heartbeatAlarm.getTimeOcurrence().toString());
		} catch (Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
		log.exiting("Controller", "setHeartbeatAlarmDetected");
	}



	/**
	 * Get the las heartbeat alarm record
	 * @return Record for the last heartbeat alarm
	 */
	public RegAlarm getHeartbeatAlarm() {
		log.entering("Controller", "getHeartbeatAlarm");
		log.exiting("Controller", "getHeartbeatAlarm");
		return heartbeatAlarm;
	}


	/**
	 * Take the robot control for manual vigilance by a client
	 * @return key needed for release the control
	 * @throws IOException serial communication error
	 */
	public synchronized long takeRobotControl() throws IOException {
		long result = 0;
		log.entering("Controller", "takeRobotControl");

		synchronized(this) {
			if (!isLocked) {
				result = (long) (Math.random() * 2645);
				log.info("Generate a new lockCookie for the client");
				isLocked = true;
				lockId = result;

			}
			else{
				log.info("Another Client has the Robot control ");

			}
		}

		if (result != 0) {
			while (termalScanRunner.isRunning() || moveScanRunner.isRunning()) {
				log.info("Wating to finalize a scan...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.severe(e.getMessage());
				}
			}


			/* Send low voltage value*/
			String voltage = String.valueOf(( (Double) Math.floor((conf.getLowVoltageValueAlarm() * Constants.SCALE_ADC) / Constants.VREF * Constants.RESISTANCE_DIVISOR)).longValue());
			char params[] = new char[5];
			CharSequence seq = new String (voltage);
			for (int j = 0; j < seq.length(); j++){
				params[j] = seq.charAt(j);
			}
			sendOperation(Operations.SET_LOW_VOLTAGE_VALUE, params);

			/* Send charge voltage */
			voltage = String.valueOf(( (Double) Math.floor((conf.getChargeVoltageValue() * Constants.SCALE_ADC) / Constants.VREF * Constants.RESISTANCE_DIVISOR)).longValue());
			params = new char[5];
			seq = new String (voltage);
			for (int j = 0; j < seq.length(); j++){
				params[j] = seq.charAt(j);
			}
			sendOperation(Operations.SET_CHARGE_VOLTAGE_VALUE, params);


			sendOperation(Operations.ENABLE_MANUAL_MOVE);
			sendOperation(Operations.START);
			sendOperation(Operations.CENTER_MOVE_CAMERA);
			sendOperation(Operations.ENABLE_CAM);

		}

		log.exiting("Controller", "takeRobotControl");
		return result;
	}

	/**
	 * Release the control for a client
	 * @param lockCookie key for release
	 * @return , boolean indicating the result of the operation
	 * @throws IOException exception
	 */
	public synchronized boolean releaseRobotControl(long lockCookie) throws IOException {
		boolean result = false;
		log.entering("Controller", "releaseRobotControl");
		if (programmingRoute) {
			programmingRoute = false;
			sendOperation(Operations.ROUTE_END);

		}
		if (isLocked && lockId == lockCookie) {
			log.info("Robot Control lock release succesfully.");
			result = true;
			isLocked = false;
			sendOperation(Operations.DISABLE_CAM);
			sendOperation(Operations.DISABLE_MANUAL_MOVE);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendOperation(Operations.STOP);

		}
		else log.info("Robot Control lock not release the Cookie are not the same.");
		log.exiting("Controller", "releaseRobotControl");
		return result;
	}

	/**
	 * Make a Forward movement
	 * @param value for move forward or stop movement
	 * @throws IOException serial communication error
	 */
	public void forwardRobotMovement(boolean value) throws IOException {
		log.entering("Controller", "forwardRobotMovement");
		if (value) sendOperation(Operations.FORWARD_MOVE);
		else sendOperation(Operations.FORWARD_MOVE_STOP);
		log.exiting("Controller", "forwardRobotMovement");
	}

	/**
	 * Make a backward movement
	 * @param value for move backward or stop movement
	 * @throws IOException serial communication error
	 */
	public void backwardRobotMovement(boolean value) throws IOException {
		log.entering("Controller", "backwardRobotMovement");
		if (value) sendOperation(Operations.BACKWARD_MOVE);
		else sendOperation(Operations.BACKWARD_MOVE_STOP);
		log.exiting("Controller", "backwardRobotMovement");
	}

	/**
	 * Make a right movement
	 * @param value for move right or stop movement
	 * @throws IOException serial communication error
	 */
	public void rightRobotMovement(boolean value) throws IOException {
		log.entering("Controller", "rightRobotMovement");
		if (value) sendOperation(Operations.RIGHT_MOVE);
		else sendOperation(Operations.RIGHT_MOVE_STOP);
		log.exiting("Controller", "rightRobotMovement");
	}

	/**
	 * Make a left movement
	 * @param value for move left or stop movement
	 * @throws IOException serial communication error
	 */
	public void leftRobotMovement(boolean value) throws IOException {
		log.entering("Controller", "leftRobotMovement");
		if (value) sendOperation(Operations.LEFT_MOVE);
		else sendOperation(Operations.LEFT_MOVE_STOP);
		log.exiting("Controller", "leftRobotMovement");
	}

	/**
	 * Make a up camera movement
	 * @param value for move up camera or stop movement
	 * @throws IOException serial communication error
	 */
	public void upCameraMovement(boolean value) throws IOException {
		log.entering("Controller", "upCameraMovement");
		if (value) sendOperation(Operations.UP_MOVE_CAMERA);
		else sendOperation(Operations.UP_MOVE_CAMERA_STOP);
		log.exiting("Controller", "upCameraMovement");
	}

	/**
	 * Make a down camera movement
	 * @param value for move down camera or stop movement
	 * @throws IOException serial communication error
	 */
	public void downCameraMovement(boolean value) throws IOException {
		log.entering("Controller", "downCameraMovement");
		if (value) sendOperation(Operations.DOWN_MOVE_CAMERA);
		else sendOperation(Operations.DOWN_MOVE_CAMERA_STOP);
		log.exiting("Controller", "downCameraMovement");
	}

	/**
	 * Make a right camera movement
	 * @param value for move right camera or stop movement
	 * @throws IOException serial communication error
	 */
	public void rightCameraMovement(boolean value) throws IOException {
		log.entering("Controller", "rightCameraMovement");
		if (value) sendOperation(Operations.RIGHT_MOVE_CAMERA);
		else sendOperation(Operations.RIGHT_MOVE_STOP_CAMERA_STOP);
		log.exiting("Controller", "rightCameraMovement");
	}

	/**
	 * Make a left camera movement
	 * @param value for move left camera or stop movement
	 * @throws IOException serial communication error
	 */
	public void leftCameraMovement(boolean value) throws IOException {
		log.entering("Controller", "leftCameraMovement");
		if (value) sendOperation(Operations.LEFT_MOVE_CAMERA);
		else  sendOperation(Operations.LEFT_MOVE_CAMERA_STOP);
		log.exiting("Controller", "leftCameraMovement");
	}

	/**
	 * Make a center camera movement
	 * @throws IOException serial communication error
	 */
	public void centerCameraMovement() throws IOException {
		log.entering("Controller", "centerCameraMovement");
		sendOperation(Operations.CENTER_MOVE_CAMERA);
		log.exiting("Controller", "centerCameraMovement");
	}

	/**
	 * Close the serial port
	 * @throws IOException serial communication error
	 */
	public static void closeSerial() throws IOException {
		threadReader.interrupt();
		serial.close();
	}

	/**
	 * Get the actual configuration server
	 * @return Acrtual configuration
	 */
	public Configuration getConf() {
		log.entering("Controller", "getConf");
		log.exiting("Controller", "getConf");
		return Controller.conf;
	}

	/**
	 * Set The configuration sended by a client
	 * @param conf Configuration to save
	 * @throws IOException serial communication error
	 */
	public void setConfiguration(Configuration conf) throws IOException {
		log.entering("Controller", "setConf");

		savePropertiesConfiguration(conf);

		/* Set the kind of movement for the robot */
		if (conf.isEnableStaticMovement() && !Controller.conf.isEnableStaticMovement()) sendOperation(Operations.ENABLE_STATIC_MOVE);
		else if (conf.isEnableRouteMovement() && !Controller.conf.isEnableRouteMovement()) sendOperation(Operations.ENABLE_ROUTE_MOVE);
		else if (conf.isEnableFollowLinesMovement() && !Controller.conf.isEnableFollowLinesMovement()) sendOperation(Operations.ENABLE_FOLLOW_LINES_MOVE);

		/*Set the sensor configuration */
		if (conf.isEnableMovementSensor() && !Controller.conf.isEnableMovementSensor()) {
			Controller.conf.setEnableMovementSensor(true);
			sendOperation(Operations.ENABLE_MOVE_SENSOR);
			if (started) {
				synchronized(moveScanRunner) {
					moveScanRunner.notify();
				}
			}
		}
		else if (!conf.isEnableMovementSensor() && Controller.conf.isEnableMovementSensor()) {
			sendOperation(Operations.DISABLE_MOVE_SENSOR);
			Controller.conf.setEnableMovementSensor(false);
		}

		Controller.conf.setKillsScansByStop(conf.getKillsScansByStop());

		if (conf.isEnableTermalSensor() && (!Controller.conf.isEnableTermalSensor() || (Controller.conf.getPeriodTermalSensor() == 0 && conf.getPeriodTermalSensor() != 0) ) ) {
			if (!Controller.conf.isEnableTermalSensor())
				sendOperation(Operations.ENABLE_TERMAL_SENSOR);
			if (Controller.conf.getPeriodTermalSensor() == 0 && conf.getPeriodTermalSensor() != 0){
				Controller.conf.setPeriodTermalSensor(conf.getPeriodTermalSensor());
			}
			Controller.conf.setEnableTermalSensor(true);
			if (started) {
				synchronized(termalScanRunner) {
					termalScanRunner.notify();
				}
			}
		}
		else if (!conf.isEnableTermalSensor() && Controller.conf.isEnableTermalSensor()) {
			Controller.conf.setEnableTermalSensor(false);
			sendOperation(Operations.DISABLE_TERMAL_SENSOR);
		}

		if (conf.isEnableSaveVideoAllTime() && !Controller.conf.isEnableSaveVideoAllTime()) {
			Controller.conf.setEnableSaveVideoAllTime(true);
			sendOperation(Operations.ENABLE_CAM);
		}

		if (conf.getProximityDistanceAlarm() !=  Controller.conf.getProximityDistanceAlarm()) {
			String distance = String.valueOf(((Double)Math.floor(conf.getProximityDistanceAlarm() * 29)).longValue());
			char params[] = new char[5];
			CharSequence seq = new String (distance);
			int j = 0;
			for (j = 0; j < seq.length(); j++){
				params[j] = seq.charAt(j);
			}
			sendOperation(Operations.SET_DISTANCE_FOR_ALARM, params);
		}


		if (conf.getLowVoltageValueAlarm() !=  Controller.conf.getLowVoltageValueAlarm()) {
			String value = String.valueOf(((Double)Math.floor((conf.getLowVoltageValueAlarm() * Constants.SCALE_ADC) / (Constants.VREF * Constants.RESISTANCE_DIVISOR))).longValue());
			char params[] = new char[5];
			CharSequence seq = new String (value);
			int j = 0;
			for (j = 0; j < seq.length(); j++){
				params[j] = seq.charAt(j);
			}
			sendOperation(Operations.SET_LOW_VOLTAGE_VALUE, params);
		}


		if (conf.getChargeVoltageValue()!=  Controller.conf.getChargeVoltageValue()) {
			String value = String.valueOf(((Double) Math.floor((conf.getChargeVoltageValue() * Constants.SCALE_ADC) / (Constants.VREF * Constants.RESISTANCE_DIVISOR))).longValue());
			char params[] = new char[5];
			CharSequence seq = new String (value);
			int j = 0;
			for (j = 0; j < seq.length(); j++){
				params[j] = seq.charAt(j);
			}
			sendOperation(Operations.SET_CHARGE_VOLTAGE_VALUE, params);
		}

		Controller.conf = conf;

		log.exiting("Controller", "setConf");

	}

	/**
	 * To know if the camera is enabled
	 * @return boolean
	 */
	public boolean isCamEnabled() {
		log.entering("Controller", "isCamEnabled");
		log.exiting("Controller", "isCamEnabled");
		return Controller.camEnabled;
	}

	/**
	 * Set the camera enable or not
	 * @param camEnabled enabled or not
	 */
	public synchronized void setCamEnabled(boolean camEnabled) {
		log.entering("Controller", "setCamEnabled");
		log.exiting("Controller", "setCamEnabled");
		Controller.camEnabled = camEnabled;
	}

	/**
	 * Start The Robot Vigilance
	 * @throws IOException serial communication error
	 */
	public synchronized void start() throws IOException {
		log.entering("Controller", "start");
		if (!started) {
			log.info("Starting Vigilance");
			Controller.started = true;

			sendConfiguration();


			if (conf.isEnableSaveVideoAllTime()) {
				log.info("Saving All Vigilance Video");
				String saveVideoLocation = properties.getParameter(SavedConfiguration.SAVE_VIDEO_LOCATION) + "//vigilance-" + Utils.getStringDate(new Date()) + ".avi";
				saveVideoRunner.setSaveVideoPath(saveVideoLocation);
				Controller.threadSaveVideo = new Thread(saveVideoRunner);
				threadSaveVideo.start();
			}


			if (conf.isEnableRouteMovement() && conf.getActiveRoute() != null){
				log.info("Robot Controller Start a Programed Route: " + conf.getActiveRoute());
				sendInitilRouteMoves();
			}

			sendOperation(Operations.START);


			scanRunning = false;
			synchronized(heartbeatRunner){
				heartbeatRunner.notifyAll();
			}
			synchronized(moveScanRunner){
				moveScanRunner.notifyAll();
			}
			synchronized(termalScanRunner){
				termalScanRunner.notifyAll();
			}

			log.info("Controller Starts the Robot Vigilance");
		}
		else {
			log.info("Robot Vigilance is started before");
		}
		log.exiting("Controller", "start");
	}


	/**
	 * Send to the robot the initial move of programmed route
	 * @throws IOException serial communication error
	 */
	public void sendInitilRouteMoves() throws IOException {
		Route route = routes.get(conf.getActiveRoute());
		char params[] = new char[Operations.ROUTE_ARRAY_LENGTH];
		log.info("Sending The initial Route moves for " + route.getName());
		for (int i = 0; i < Operations.ROUTE_ARRAY_LENGTH && route.getMovements().size() > i; i++){
			for (int k = 0; k < Operations.ROUTE_ARRAY_LENGTH ; k++){
				params[k] = '\n';
			}
			params[0] = route.getMovements().get(i).getMove();
			params[1] = ',';
			char checkPoint = String.valueOf(route.getMovements().get(i).getCheckMove()).toCharArray()[0];
			params[2] = checkPoint;
			params[3] = ',';
			String moveTime= String.valueOf(route.getMovements().get(i).getMoveDuration());
			CharSequence seq = new String (moveTime);
			for (int j = 0; j < seq.length(); j++){
				params[j+4] = seq.charAt(j);
			}
			route.setPosition(i+1);
			sendOperation(Operations.SEND_ROUTE_MOVE, params);
		}
	}


	/**
	 * Send a route Move
	 * @param again If the move was sended before
	 * @throws IOException serial communication error
	 */
	public void sendRouteMove(boolean again) throws IOException {
		Route route = routes.get(conf.getActiveRoute());
		int position = route.getPosition();

		if (position == route.getMovements().size() && !again){
			position = 0;
		}
		else if (position==0 && again){
			position = route.getMovements().size() - 1;
		}
		else if (again) position = position - 1;

		char params[] = new char[Operations.ROUTE_ARRAY_LENGTH];
		params[0] = route.getMovements().get(position).getMove();
		params[1] = ',';

		char checkPoint = String.valueOf(route.getMovements().get(position).getCheckMove()).toCharArray()[0];
		params[2] = checkPoint;
		params[3] = ',';

		String moveTime= String.valueOf(route.getMovements().get(position).getMoveDuration());


		CharSequence seq = new String (moveTime);
		for (int j = 0; j < seq.length(); j++){
			params[j+4] = seq.charAt(j);
		}

		log.info("Sending next route move...");
		sendOperation(Operations.SEND_ROUTE_MOVE, params);

		if (!again)
			route.setPosition(position+1);
	}


	/**
	 * Send the configuration to the robot
	 * @throws IOException serial communication error
	 */
	public synchronized void sendConfiguration() throws IOException {

		if (conf.isEnableMovementSensor())
			sendOperation(Operations.ENABLE_MOVE_SENSOR);
		else
			sendOperation(Operations.DISABLE_MOVE_SENSOR);

		if (conf.isEnableTermalSensor())
			sendOperation(Operations.ENABLE_TERMAL_SENSOR);
		else
			sendOperation(Operations.DISABLE_TERMAL_SENSOR);

		if (conf.isEnableFollowLinesMovement())
			sendOperation(Operations.ENABLE_FOLLOW_LINES_MOVE);
		else if (conf.isEnableRouteMovement())
			sendOperation(Operations.ENABLE_ROUTE_MOVE);
		else if (conf.isEnableStaticMovement())
			sendOperation(Operations.ENABLE_STATIC_MOVE);

		if (conf.isEnableSaveVideoAllTime())
			sendOperation(Operations.ENABLE_CAM);

		String distance = String.valueOf(((Double)Math.floor(conf.getProximityDistanceAlarm() * 29)).longValue());
		char params[] = new char[5];
		CharSequence seq = new String (distance);
		int j = 0;
		for (j = 0; j < seq.length(); j++){
			params[j] = seq.charAt(j);
		}


		sendOperation(Operations.SET_DISTANCE_FOR_ALARM, params);

		/* Send low voltage value*/
		String voltage = String.valueOf(( (Double) Math.floor( ((conf.getLowVoltageValueAlarm() * Constants.SCALE_ADC)) / (Constants.VREF * Constants.RESISTANCE_DIVISOR))).longValue());
		params = new char[5];
		seq = new String (voltage);
		for (j = 0; j < seq.length(); j++){
			params[j] = seq.charAt(j);
		}
		sendOperation(Operations.SET_LOW_VOLTAGE_VALUE, params);

		/* Send charge voltage */
		voltage = String.valueOf(( (Double) Math.floor(((conf.getChargeVoltageValue() * Constants.SCALE_ADC)) / (Constants.VREF * Constants.RESISTANCE_DIVISOR))).longValue());
		params = new char[5];
		seq = new String (voltage);
		for (j = 0; j < seq.length(); j++){
			params[j] = seq.charAt(j);
		}
		sendOperation(Operations.SET_CHARGE_VOLTAGE_VALUE, params);

	}

	/**
	 * Stops the vigilance
	 * @throws IOException serial communication error
	 */
	public synchronized void stop() throws IOException {
		log.entering("Controller", "stop");
		if (started) {
			log.info("Stopping Vigilance");
			Controller.started = false;


			if (conf.getKillsScansByStop()) {
				Controller.threadMoveScan.interrupt();
				Controller.threadTermalScan.interrupt();
			}
			else {
				synchronized(Controller.scanRunning) {
					if (Controller.scanRunning){
						try {
							Controller.scanRunning.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							log.severe(e.getMessage());
						}
						Controller.scanRunning = false;
					}
				}
			}
			sendOperation(Operations.CENTER_MOVE_CAMERA);


			if (conf.isEnableSaveVideoAllTime()) {
				synchronized(saveVideoRunner){
					saveVideoRunner.notify();
				}
			}


			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendOperation(Operations.STOP);
			log.info("Robot Controller Stop the Robot Vigilance");
		}

		else {
			log.info("Robot Vigilance is stopped.");
		}
		log.exiting("Controller", "stop");
	}

	/**
	 * Check for a termal captures received
	 * @param captures termal captures separated by commas
	 */
	public void checkTermalCaptures(String captures){
		int i,j;
		log.info("Termal Scan Captures: " + captures);
		String capturesArray[] =captures.split(",");
		for (i = 0; i < capturesArray.length; i++){
			try {
				for (j = i+1; j < capturesArray.length; j++){
					int capture1 = Integer.parseInt(capturesArray[i]);
					int capture2 = Integer.parseInt(capturesArray[j]);
					if (Math.abs(capture1 - capture2) >= 8){
						setTermalAlarmDetected(captures);
						i = capturesArray.length;
						break;
					}
				}
			}catch (NumberFormatException e){
				log.info(e.getMessage());
				break;
			}
		}
	}

	/**
	 * Get schedule configuration
	 * @return Schedule
	 */
	public ScheduleProgram getSchedule() {
		log.entering("Controller", "ScheduleProgram");
		log.exiting("Controller", "ScheduleProgram");
		return scheduleRunner.getScheduleProgram();
	}

	/**
	 * Set a new schedule configuration
	 * @param schedule schedule to set
	 */
	public void setSchedule(ScheduleProgram schedule) {
		log.entering("Controller", "setSchedule");

		synchronized (scheduleRunner) {
			if (schedule.isEnabled() && schedule.getCalendar(true) != null && schedule.getCalendar(false) != null) {
				log.info("Wake up Schedule Thread...");
				scheduleRunner.notify();
			}
			log.info("Setting New Schedule Configuration");
			Controller.scheduleRunner.setScheduleProgram(schedule);
			log.exiting("Controller", "setSchedule");
		}
	}

	/**
	 * Send operation with parameters
	 * @param operation id for the operation
	 * @param params parameters
	 * @throws IOException serial communication error
	 */
	public synchronized static void sendOperation (char operation, char[] params) throws IOException {
		Logger log = Logger.getLogger("pcf.control");
		log.info("Send Operation : " + Operations.getOperation(operation) + "  Hex : " + String.format("%04x", (int) operation));
		try {

			serial.write(new String(new char[] {operation}).getBytes("US-ASCII"));
			if (params != null) {
				String paramString = new String(params) ;
				paramString = paramString + '\r';
				log.info("Parameters: " + paramString );
				byte[] bytes = paramString.getBytes("US-ASCII");
				serial.write(bytes);
			}
			Thread.sleep(150);

		} catch (InterruptedException e){
			Thread.currentThread().interrupt();
			Logger.getLogger("pfc.control").info("Send Operation Interrupted");
		}
	}

	/**
	 * Send a operation to the robot
	 * @param operation Id for the operation
	 * @throws IOException serial communication error
	 */
	public synchronized static void sendOperation (char operation) throws IOException {
		Logger log = Logger.getLogger("pcf.control");
		Controller.sendOperation(operation,null);
	}

	/**
	 * Set last heartbeat received request received
	 * @param value boolean
	 */
	public void setLastHeartbeat(boolean value) {
		Controller.heartbeatRunner.setLastHeartbeat(value);
	}

	/**
	 * to know if the robot vigilance is started
	 * @return boolean
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Set the boolean started variable
	 * @param started boolean
	 */
	public void setStarted(boolean started) {
		Controller.started = started;
	}

	/**
	 * Exit with a status
	 * @param status stauts exit value
	 */
	public static void exit (int status) {
		VideoController.stopVideoTransmitter();
		System.exit(status);
	}

	protected MoveScanRunner getMoveScanRunner() {
		return moveScanRunner;
	}


	protected TermalScanRunner getTermalScanRunner() {
		return termalScanRunner;
	}

	/**
	 * To know if the robot is locked
	 * @return boolean
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 *  Set a lock/unlock
	 * @param isLocked boolean
	 */
	public void setLocked(boolean isLocked) {
		Controller.isLocked = isLocked;
	}

	/**
	 * Send a route definition configuration to the robot
	 * @throws IOException serial communication error
	 */
	public void startRouteDefinition() throws IOException {
		routeDefinition = new Route();
		if (programmingRoute) sendOperation(Operations.ROUTE_END);
		else programmingRoute = true;
		sendOperation(Operations.CENTER_MOVE_CAMERA);
		sendOperation(Operations.ROUTE_START);
	}


	private void savePropertiesConfiguration(Configuration conf){
		String receivers = "";


		properties.setParameter(SavedConfiguration.SAVE_VIDEO_TIME, String.valueOf(conf.getTimeSaveVideo()));
		properties.setParameter(SavedConfiguration.HEARTBEAT_PERIOD, String.valueOf(conf.getPeriodHeartbeat()));
		properties.setParameter(SavedConfiguration.SAVE_VIDEO_ALARM, Boolean.toString(conf.isEnableSaveVideoAlarm()));
		properties.setParameter(SavedConfiguration.ENABLE_MOVE_SCAN, Boolean.toString(conf.isEnableMovementSensor()));
		properties.setParameter(SavedConfiguration.ENABLE_TERMAL_SCAN, Boolean.toString(conf.isEnableTermalSensor()));
		properties.setParameter(SavedConfiguration.MOVE_SCAN_PERIOD, String.valueOf(conf.getPeriodMovementSensor()));
		properties.setParameter(SavedConfiguration.TERMAL_SCAN_PERIOD, String.valueOf(conf.getPeriodTermalSensor()));
		properties.setParameter(SavedConfiguration.TERMAL_ORIENTATION_PERIOD, String.valueOf(conf.getPeriodOrientationTermalSensor()));
		properties.setParameter(SavedConfiguration.MOVE_ORIENTATION_PERIOD, String.valueOf(conf.getPeriodOrientationMovementSensor()));
		properties.setParameter(SavedConfiguration.SAVE_VIDEO_ALL_TIME, Boolean.toString(conf.isEnableSaveVideoAllTime()));
		properties.setParameter(SavedConfiguration.ENABLE_MAIL, Boolean.toString(conf.isEnableEmail()));
		properties.setParameter(SavedConfiguration.KILL_SCANS_BY_STOP, Boolean.toString(conf.getKillsScansByStop()));

		if (conf.isEnableFollowLinesMovement())
			properties.setParameter(SavedConfiguration.KIND_VIGILANCE, Configuration.FOLLOW_LINES);
		else if (conf.isEnableRouteMovement()) {
			properties.setParameter(SavedConfiguration.KIND_VIGILANCE, Configuration.PROGRAM_ROUTE);
		}

		else if (conf.isEnableStaticMovement())
			properties.setParameter(SavedConfiguration.KIND_VIGILANCE, Configuration.STATIC);

		if (conf.isEnableRouteMovement())
			properties.setParameter(SavedConfiguration.ACTIVE_ROUTE, conf.getActiveRoute());

		properties.setParameter(SavedConfiguration.PROXIMITY_DISTANCE_ALARM, String.valueOf(conf.getProximityDistanceAlarm()));
		properties.setParameter(SavedConfiguration.LOW_VOLTAGE_VALUE, String.valueOf(conf.getLowVoltageValueAlarm()));
		properties.setParameter(SavedConfiguration.CHARGE_VOLTAGE_VALUE, String.valueOf(conf.getChargeVoltageValue()));

		for (int i = 0; i < conf.getEmailReceivers().length; i++){
			if ((i == 0))
				receivers = conf.getEmailReceivers()[i];
			else
				receivers = receivers + ";" + conf.getEmailReceivers()[i];
		}
		properties.setParameter(SavedConfiguration.MAIL_RECEIVER, receivers);


	}

	/**
	 * Get actual route definition
	 * @return Actual Route
	 */
	public Route getRouteDefinition() {
		return routeDefinition;
	}

	protected Boolean isScanRunning() {
		return scanRunning;
	}

	protected void setScanRunning(Boolean scanRunning) {
		Controller.scanRunning = scanRunning;
	}

	/**
	 *  Get the actual distance alarm record
	 * @return Record for last distance alarm
	 */
	public RegAlarm getDistanceAlarm() {
		return distanceAlarm;
	}

	/**
	 * Get the actual low voltage alarm record
	 * @return Record for last low voltage alarm
	 */
	public RegAlarm getLowVoltageAlarm() {
		return lowVoltageAlarm;
	}

	/**
	 * Get if is active camera device by a client
	 * @return boolean
	 */
	public static boolean isActiveVideoClient() {
		return activeVideoClient;
	}

	/**
	 * Set if is active camera device by a client
	 * @param activeVideoClient boolean
	 */
	public static void setActiveVideoClient(boolean activeVideoClient) {
		Controller.activeVideoClient = activeVideoClient;
	}



}
