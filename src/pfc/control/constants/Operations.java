package pfc.control.constants;

import java.util.HashMap;

/**
 * This constant class define all the operations that is possible to send to 
 * the vigilance robot
 * 
 * @author Rodrigo Jimenez
 * @version 1.0
 *
 */
public class Operations {


	/**                                            
	 * operation to enable the movement sensor      
	 */                                             
	public final static char ENABLE_MOVE_SENSOR = 0x01;      

	/**                                            
	 * operation to disable the movement sensor     
	 */                                             
	public final static char DISABLE_MOVE_SENSOR = 0x02;     

	/**                                            
	 * operation to enale the termal sensor         
	 */                                             
	public final static char ENABLE_TERMAL_SENSOR = 0x03;    

	/**                                            
	 * operationn to disable the termal sensor      
	 */                                             
	public final static char DISABLE_TERMAL_SENSOR = 0x04;  


	/**
	 * operation to run a new termal scan
	 */
	public final static char RUN_TERMAL_SCAN = 0x05;
	
	
	/**                                            
	 * operation to report a termal alarm   
	 */                                            
	public final static char SET_TERMAL_SCAN = 0x33;


	/**
	 * operation to run a new move scan
	 */
	public final static char STOP_TERMAL_SCAN = 0x07;

	/**
	 * operation to run a new move scan
	 */
	public final static char STOP_MOVE_SCAN = 0x08;


	/**                                            
	 * operation to kwon that the robot is alive   
	 */                                            
	public final static char HEARTBEAT = 0x09;

	/**                                            
	 * operation to stop forward move          
	 */                                             
	public final static char FORWARD_MOVE_STOP = 0x0B;   

	/**                                            
	 * operation to start backward move  
	 */                                             
	public final static char BACKWARD_MOVE = 0x0C;          

	/**                                            
	 * operation to stop backward move  
	 */                                             
	public final static char BACKWARD_MOVE_STOP = 0x0D;     

	/**                                            
	 * operation to start left move      
	 */                                             
	public final static char LEFT_MOVE = 0x0E;    

	/**                                            
	 * operation to stop left move      
	 */                                             
	public final static char LEFT_MOVE_STOP = 0x0F;

	/**                                            
	 * operation to start right move     
	 */                                             
	public final static char RIGHT_MOVE = 0x10;             

	/**                                            
	 * operation to stop right move      
	 */                                             
	public final static char RIGHT_MOVE_STOP = 0x11;

	/**                                            
	 * operation to enable the manual move          
	 */                                             
	public final static char ENABLE_MANUAL_MOVE = 0x12;

	/**                                            
	 * operation to enable the route move          
	 */                                             
	public final static char ENABLE_ROUTE_MOVE = 0x13;

	/**                                            
	 * operation to enable follow lines move
	 */                                             
	public final static char ENABLE_STATIC_MOVE = 0x14;

	/**                                            
	 * operation to enable the follow lines movement
	 */                                             
	public final static char ENABLE_FOLLOW_LINES_MOVE = 0x15; 

	/**                                            
	 * operation to disable the manual move          
	 */                                             
	public final static char DISABLE_MANUAL_MOVE = 0x16;

	/**                                            
	 * operation to start the vigilance   
	 */                                            
	public final static char START = 0x17;

	/**                                            
	 * operation to stop the vigilance   
	 */                                            
	public final static char STOP = 0x18;

	/**
	 * operation to run a new move scan
	 */
	public final static char RUN_MOVE_SCAN = 0x19;

	/**                                            
	 * operation to start up move camera          
	 */                                             
	public final static char UP_MOVE_CAMERA = 0x1A; 

	/**                                            
	 * operation to stop up move camera         
	 */                                             
	public final static char UP_MOVE_CAMERA_STOP = 0x1B;   

	/**                                            
	 * operation to start down move camera   
	 */                                             
	public final static char DOWN_MOVE_CAMERA = 0x1C;          

	/**                                            
	 * operation to stop down move camera  
	 */                                             
	public final static char DOWN_MOVE_CAMERA_STOP = 0x1D;

	/**                                            
	 * operation to center the camera      
	 */                                             
	public final static char CENTER_MOVE_CAMERA = 0x1E; 

	/**                                            
	 * operation to start left move camera      
	 */                                             
	public final static char LEFT_MOVE_CAMERA = 0x1F;    

	/**                                            
	 * operation to stop left move camera      
	 */                                             
	public final static char LEFT_MOVE_CAMERA_STOP = 0x20;

	/**                                            
	 * operation to start right move camera      
	 */                                             
	public final static char RIGHT_MOVE_CAMERA = 0x21;             

	/**                                            
	 * operation to stop right move camera       
	 */                                             
	public final static char RIGHT_MOVE_STOP_CAMERA_STOP = 0x22;

	/**                                            
	 * operation to report a movement alarm   
	 */                                            
	public final static char SET_MOVE_ALARM = 0x23;
	
	/**                                            
	 * operation to enable camera recording  
	 */                                            
	public final static char ENABLE_CAM = 0x24;
	
	/**                                            
	 * operation to disable camera recording  
	 */                                            
	public final static char DISABLE_CAM = 0x25;
	
	/**
	 * Indicate to the robot that the route definition is started
	 */
	public final static char ROUTE_START = 0x26;
	
	/**
	 * Indicate to the robot that the route is saved
	 */
	public final static char ROUTE_END = 0x27;
	
	/**
	 * When a route move at at a route derfinition is received 
	 */
	public final static char SEND_MOVE_DEFINITION = 0x28;
	
	/**
	 * Indicate to the server that an error is happened
	 */
	public final static char ERROR = 0x29;
	
	/**
	 * Send a route move to the robot
	 */
	public final static char SEND_ROUTE_MOVE = 0x30;
	
	/**                                            
	 * operation to move the robot forward          
	 */                                             
	public final static char FORWARD_MOVE = 0x31; 
	
	
	/**
	* Receive an operation for send the next move
	*/
	public final static char REQUEST_FOR_MOVE = 0x32;
	
	/**
	* Receive an operation for send the next move
	*/
	//public final static char LAST_ROUTE_MOVE = 0x34;
	
	
	/**
	* Operation to Set the distance for alarm
	*/
	public final static char SET_DISTANCE_FOR_ALARM= 0x35;
	
	
	/**
	* Operation to Set a proximity alarm
	*/
	public final static char SET_DISTANCE_ALARM = 0x37;
	
	
	/**
	* Operation to Set a low voltage alarm
	*/
	public final static char SET_LOW_VOLTAGE_ALARM = 0x3B;
	
	
	/**
	* Repeat the last request move
	*/
	public final static char REPEAT_REQUEST_MOVE = 0x39;

	
	/**
	* Receive an operation to set the low voltage value for alarm
	*/
	public final static char SET_LOW_VOLTAGE_VALUE = 0x40;
	
	/**
	* Receive an operation to set the low voltage value for alarm
	*/
	public final static char SET_CHARGE_VOLTAGE_VALUE = 0x41;
	
	/**
	 * Indicate to longitude of the array movements at the robot
	 */
	public final static char ROUTE_ARRAY_LENGTH = 8;


	private static HashMap<String, String> operationRelation = null;


	private static void initialize(){
		operationRelation = new HashMap<String, String>();
		operationRelation.put(String.valueOf(ENABLE_MOVE_SENSOR), "ENABLE_MOVE_SENSOR");
		operationRelation.put(String.valueOf(DISABLE_MOVE_SENSOR), "DISABLE_MOVE_SENSOR");
		operationRelation.put(String.valueOf(ENABLE_TERMAL_SENSOR), "ENABLE_TERMAL_SENSOR");
		operationRelation.put(String.valueOf(DISABLE_TERMAL_SENSOR), "DISABLE_TERMAL_SENSOR");
		operationRelation.put(String.valueOf(RUN_TERMAL_SCAN), "RUN_TERMAL_SCAN");
		operationRelation.put(String.valueOf(STOP_TERMAL_SCAN), "STOP_TERMAL_SCAN");
		operationRelation.put(String.valueOf(STOP_MOVE_SCAN), "STOP_MOVE_SCAN");
		operationRelation.put(String.valueOf(HEARTBEAT), "HEARTBEAT");
		operationRelation.put(String.valueOf(FORWARD_MOVE), "FORWARD_MOVE");
		operationRelation.put(String.valueOf(FORWARD_MOVE_STOP), "FORWARD_MOVE_STOP");
		operationRelation.put(String.valueOf(BACKWARD_MOVE), "BACKWARD_MOVE");
		operationRelation.put(String.valueOf(BACKWARD_MOVE_STOP), "BACKWARD_MOVE_STOP");
		operationRelation.put(String.valueOf(LEFT_MOVE), "LEFT_MOVE");
		operationRelation.put(String.valueOf(LEFT_MOVE_STOP), "LEFT_MOVE_STOP");
		operationRelation.put(String.valueOf(RIGHT_MOVE), "RIGHT_MOVE");
		operationRelation.put(String.valueOf(RIGHT_MOVE_STOP), "RIGHT_MOVE_STOP");
		operationRelation.put(String.valueOf(ENABLE_MANUAL_MOVE), "ENABLE_MANUAL_MOVE");
		operationRelation.put(String.valueOf(ENABLE_ROUTE_MOVE), "ENABLE_ROUTE_MOVE");
		operationRelation.put(String.valueOf(ENABLE_STATIC_MOVE), "ENABLE_STATIC_MOVE");
		operationRelation.put(String.valueOf(ENABLE_FOLLOW_LINES_MOVE), "ENABLE_FOLLOW_LINES_MOVE");
		operationRelation.put(String.valueOf(DISABLE_MANUAL_MOVE), "DISABLE_MANUAL_MOVE");
		operationRelation.put(String.valueOf(START), "START");
		operationRelation.put(String.valueOf(STOP), "STOP");
		operationRelation.put(String.valueOf(RUN_MOVE_SCAN), "RUN_MOVE_SCAN");
		operationRelation.put(String.valueOf(UP_MOVE_CAMERA), "UP_MOVE_CAMERA");
		operationRelation.put(String.valueOf(UP_MOVE_CAMERA_STOP), "UP_MOVE_CAMERA_STOP");
		operationRelation.put(String.valueOf(DOWN_MOVE_CAMERA), "DOWN_MOVE_CAMERA");
		operationRelation.put(String.valueOf(DOWN_MOVE_CAMERA_STOP), "DOWN_MOVE_CAMERA_STOP");
		operationRelation.put(String.valueOf(CENTER_MOVE_CAMERA), "CENTER_MOVE_CAMERA");
		operationRelation.put(String.valueOf(LEFT_MOVE_CAMERA), "LEFT_MOVE_CAMERA");
		operationRelation.put(String.valueOf(LEFT_MOVE_CAMERA_STOP), "LEFT_MOVE_CAMERA_STOP");
		operationRelation.put(String.valueOf(RIGHT_MOVE_CAMERA), "RIGHT_MOVE_CAMERA");
		operationRelation.put(String.valueOf(RIGHT_MOVE_STOP_CAMERA_STOP), "RIGHT_MOVE_STOP_CAMERA_STOP");
		operationRelation.put(String.valueOf(SET_MOVE_ALARM), "SET_MOVE_ALARM");
		operationRelation.put(String.valueOf(SET_TERMAL_SCAN), "SET_TERMAL_SCAN");
		operationRelation.put(String.valueOf(ENABLE_CAM), "ENABLE_CAM");
		operationRelation.put(String.valueOf(DISABLE_CAM), "DISABLE_CAM");
		operationRelation.put(String.valueOf(ROUTE_START), "ROUTE_START");
		operationRelation.put(String.valueOf(ROUTE_END), "ROUTE_END");
		operationRelation.put(String.valueOf(SEND_MOVE_DEFINITION), "SEND_MOVE_DEFINITION");
		operationRelation.put(String.valueOf(ERROR), "ERROR");
		operationRelation.put(String.valueOf(SEND_ROUTE_MOVE), "SEND_ROUTE_MOVE");
		//operationRelation.put(String.valueOf(LAST_ROUTE_MOVE), "LAST_ROUTE_MOVE");
		operationRelation.put(String.valueOf(SET_DISTANCE_ALARM), "SET_DISTANCE_ALARM");
		operationRelation.put(String.valueOf(SET_DISTANCE_FOR_ALARM), "SET_DISTANCE_FOR_ALARM");
		operationRelation.put(String.valueOf(REPEAT_REQUEST_MOVE), "REPEAT_REQUEST_MOVE");
		operationRelation.put(String.valueOf(SET_LOW_VOLTAGE_VALUE), "SET_LOW_VOLTAGE_VALUE");	
		operationRelation.put(String.valueOf(SET_CHARGE_VOLTAGE_VALUE), "SET_CHARGE_VOLTAGE_VALUE");
		
	}

	/**
	 * Get operation in String by char
	 * @param operation char
	 * @return String
	 */
	public static String getOperation (char operation){
		if (operationRelation == null) {
			initialize();
		}
		return operationRelation.get(String.valueOf(operation));
	}
	
	
	/**
	 * Get operation char by string
	 * @param operation String
	 * @return char
	 */
	public static char getOperation (String operation){
		if (operation.compareTo("FORWARD_MOVE") == 0)
			return FORWARD_MOVE;
		else if (operation.compareTo("BACKWARD_MOVE") == 0)
			return BACKWARD_MOVE;
		else if (operation.compareTo("LEFT_MOVE") == 0) 
			return LEFT_MOVE;
		else if (operation.compareTo("RIGHT_MOVE") == 0)
			return RIGHT_MOVE;
		else 
			return '\0';
	}
	

}
