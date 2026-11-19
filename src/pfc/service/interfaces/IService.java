package pfc.service.interfaces;


import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import pfc.control.RegAlarm;
import pfc.control.ScheduleProgram;
import pfc.control.configuration.Configuration;
import pfc.route.Route;


/**
 * Interface for the rmi clients this are the remote methods 
 * to execute at the server.
 * @author Rodrigo Jimenez
 * @version 1.0
 *
 */
public interface IService extends Remote {

        
    /**
     * Stop the movement f the robot
     * @param lockCookie cookie to unlock
     * @return boolean indicating the result of the operation
     * @throws RemoteException RMI Exception 
     * @throws IOException serial port exception
     */
    public boolean releaseRobotControl(long lockCookie) throws RemoteException, IOException;
    

    /**
     * Take the control of the robot by a client
     * @return cookie for the client
     * @throws RemoteException RMI exception
     * @throws IOException serial port exception
     */
    public long takeRobotControl() throws RemoteException, IOException;
       
    
    /** 
     * Move the robot in forward direction
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException  Serial port exception
     */
    public void forwardRobotMovement(boolean value) throws RemoteException, IOException;
    
    /**
     * Move the robot in backward direction 
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void backwardRobotMovement(boolean value) throws RemoteException, IOException;
    
    
    /**
     * Move the robot in right direction 
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void rightRobotMovement(boolean value) throws RemoteException, IOException;
    
    
    /**
     * Move the robot in left direction
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void leftRobotMovement(boolean value) throws RemoteException, IOException;
    
    
    /** 
     * Move the camera in up direction
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void upCameraMovement(boolean value) throws RemoteException, IOException;
    
    /**
     * Move the camera in down direction 
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void downCameraMovement(boolean value) throws RemoteException, IOException;
    
    
    /**
     * Move the camera in right direction 
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void rightCameraMovement(boolean value) throws RemoteException, IOException;
    
    
    /**
     * Move the camera in left direction
     * @param value boolean indicating the result of the operation
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void leftCameraMovement(boolean value) throws RemoteException, IOException;
    
    /**
     * Move the camera in left direction
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void centerCameraMovement() throws RemoteException, IOException;
    
     
    /**
     * 
     * @return Actual configuration of the robot 
     * @throws RemoteException RMI exception
     */
    public Configuration getConf() throws RemoteException;
    
    
    /**
     * Set a new configuration
     * @param conf new configuration to set
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void setConf(Configuration conf) throws RemoteException, IOException;
    
    /**
     * Get last move alarm record
     * @return Register with the last move alarm detected
     * @throws RemoteException RMI exception
     */
    public RegAlarm getMoveAlarm() throws RemoteException;
    
    /**
     * Get last move alarm record
     * @return Register with the last termal alarm detected
     * @throws RemoteException RMI exception
     */
    public RegAlarm getTermalAlarm() throws RemoteException;
    
    
    /**
     * Get last proximity alarm record
     * @return Register with the last proximity alarm detected
     * @throws RemoteException RMI exception
     */
    public RegAlarm getDistanceAlarm() throws RemoteException;
    
    
    /**
     * Get last low voltage alarm record
     * @return Register with the last low voltage alarm detected
     * @throws RemoteException RMI exception
     */
    public RegAlarm getLowVoltageAlarm() throws RemoteException;
    
    /**
     * To know if the camera device is enabled or not
     * @return boolean
     * @throws RemoteException RMI exception
     */
    public boolean isCamEnabled() throws RemoteException;
    
    /**
     * Enable camera device or not
     * @param camEnabled boolean
     * @throws RemoteException RMI exception
     */
    public void setCamEnabled(boolean camEnabled) throws RemoteException;
    
    /**
     * Start the Robot Vigilance
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void start()throws RemoteException, IOException;
    
    /**
     * Stop the robot vigilance
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void stop()throws RemoteException, IOException;
    
    /**
     * Get Schedule configuration
     * @return ScheduleProgram
     * @throws RemoteException RMI exception
     */
    public ScheduleProgram getSchedule() throws RemoteException;
    
    /**
     * Set Schedule Configuration
     * @param schedule Schedule to set
     * @throws RemoteException RMI exception
     */
    public void setSchedule(ScheduleProgram schedule) throws RemoteException;
    
    
    /**
     * Get last heartbeat alarm record
     * @return Register with the last heartbeat alarm detected
     * @throws RemoteException RMI exception 
     */
    public RegAlarm getHeartbeatAlarm() throws RemoteException;
    
    
    /**
     * Get all the route names saved at the server
     * @return String[]
     * @throws RemoteException RMI exception
     */
    public String[] getRouteNames() throws RemoteException;
    
    /**
     * Add a new Route definition
     * @param route Route definition to add
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void addRoute(Route route) throws RemoteException, IOException;
    
    /**
     * To start a route definition  
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void startRouteDefinition() throws RemoteException, IOException;
    
    /**
     * To know if the vigilance is started or not
     * @return boolean
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public boolean isStarted() throws RemoteException, IOException;
    
    /**
     * To enable or not the camera at the robot
     * @param value boolean
     * @throws RemoteException RMI exception
     * @throws IOException Serial port exception
     */
    public void enableRobotCam(boolean value) throws RemoteException, IOException;
    
}
