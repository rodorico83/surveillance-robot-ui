package pfc.rs232;

import java.io.IOException;
import java.util.logging.Logger;
import pfc.control.Controller;
import pfc.control.constants.Operations;
import pfc.route.Route;
import pfc.route.Route.Movement;
import pfc.video.VideoController;

/**
 * Serial Reader Implementation
 * @author rodrigo
 *
 */
public class SerialReader implements Runnable{

	private Controller controller;
	private SerialCommunication serial;

	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.db</code>.
	 */
	private Logger log = Logger.getLogger("pfc.rs323");

	/**
	 * Constructor
	 * @param controller controller
	 * @param serial serial communication instance
	 */
	public SerialReader(Controller controller, SerialCommunication serial){
		this.controller = controller;
		this.serial = serial;
	}

	
	/*public static String toString(ByteArrayInputStream is) {
		int size = is.available();
		char[] theChars = new char[size];
		byte[] bytes    = new byte[size];

		is.read(bytes, 0, size);
		for (int i = 0; i < size;)
			theChars[i] = (char)(bytes[i++]&0xff);

		return new String(theChars);
	}*/

	public void run () {
		byte[] buffer = new byte[2048];
		int len = -1;
		int operation = 0;
		String str = "";

		/* variables para el control de parametros en las operaciones */
		int lastOperation = '-';
		boolean params = false;

		CharSequence seq = new String("\n");
		try {
			while (serial.isOpen() || str.length() > 1)
			{
				int available = serial.bytesAvailable();
				if (available <= 0) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
					len = 0;
				}
				else {
					len = serial.read(buffer, Math.min(buffer.length, available));
				}

				if (len > 0 || str.length() > 1) {
					if (len > 0)
						str = str + new String(buffer,0,len, "US-ASCII");
					String str2;

					//System.out.println(str);
					if (str.contains(seq)) {
						int index =  str.indexOf("\n");
						str2 = str.substring(0, index);
						if (str.length() > index+1)
							str = str.substring(index+1, str.length());
						else
							str = "";

						System.out.println(str2);


						if (!params)
							operation = str2.toCharArray()[0];

						
						if ((operation == Operations.SET_MOVE_ALARM && !params && controller.isStarted())) {
							log.info("Operation received: SET_MOVE_ALARM");
							controller.setMoveAlarmDetected();
						}

						else if ((operation  == Operations.SET_TERMAL_SCAN && !params ) ||
								params == true && lastOperation == Operations.SET_TERMAL_SCAN && controller.isStarted()) {

							if (params) {
								controller.checkTermalCaptures(str2);
								params = false;
							}
							else {
								lastOperation = Operations.SET_TERMAL_SCAN;
								params = true;
								log.info("Operation received: SET_TERMAL_ALARM");
							}
							
						}

						else if ((operation == Operations.HEARTBEAT && !params && controller.isStarted())) {
							lastOperation = Operations.HEARTBEAT;
							log.info("Operation received: HEARTBEAT");
							controller.setLastHeartbeat(true);
						}

						else if ((operation == Operations.SEND_MOVE_DEFINITION && !params ) || 
								lastOperation == Operations.SEND_MOVE_DEFINITION && params) {
							if (params) {
								String moveDefArray[] = str2.split(",");
								char moveChar = str2.toCharArray()[0];
								Route actualRoute = controller.getRouteDefinition();
								Movement move = actualRoute.new Movement((char) moveChar, Long.parseLong(moveDefArray[1]), Integer.parseInt(moveDefArray[2]));
								actualRoute.addMove(move);
								params = false;
							}
							else {
								lastOperation = Operations.SEND_MOVE_DEFINITION;
								log.info("Operation received: SEND_ROUTE_DEFINITION");
								params = true;
							}
							
						}
						else if ((operation == Operations.REQUEST_FOR_MOVE && !params && controller.getConf().isEnableRouteMovement()) && controller.isStarted()) {
							lastOperation = Operations.REQUEST_FOR_MOVE;
							log.info("Operacion recibida: REQUEST_FOR_MOVE");
							controller.sendRouteMove(false);
						}

						else if ((operation == Operations.ERROR && !params)){
							lastOperation = Operations.ERROR;
							log.severe("Error Receiverd from the Vigilance Robot.");

						}

						else if ((operation == Operations.SET_DISTANCE_ALARM && !params ) || 
								lastOperation == Operations.SET_DISTANCE_ALARM && params){
							if (params) {
								controller.setDistanceAlarmDetected(str2);
								params = false;
							}
							else {
								lastOperation = Operations.SET_DISTANCE_ALARM;
								log.info("Operation received: SET_DISTANCE_ALARM");
								params = true;
							
							}
							
						}	
						
						else if (operation == Operations.SET_LOW_VOLTAGE_ALARM && !params || 
								lastOperation == Operations.SET_LOW_VOLTAGE_ALARM && params){
							if (params) {
								controller.setLowVoltageAlarmDetected(str2);
								params = false;
							}
							else {
								lastOperation = Operations.SET_LOW_VOLTAGE_ALARM;
								log.info("Operation received: SET_LOW_VOLTAGE_ALARM");
								params = true;
							}
						}	
						
						else if (operation == Operations.REPEAT_REQUEST_MOVE && !params){
							lastOperation = operation;
							log.info("Operation received: REPEAT_REQUEST_MOVE");
							controller.sendRouteMove(true);
					}	
						
						
					}
				}
			}

		}
		catch ( IOException e )
		{
			log.severe(e.getMessage());
			VideoController.stopVideoTransmitter();
			e.printStackTrace();
			System.exit(-1);
		}
		catch ( Exception e )
		{
			log.severe(e.getMessage());
			VideoController.stopVideoTransmitter();
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
