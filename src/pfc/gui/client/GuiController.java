package pfc.gui.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.*;
import gnu.io.NoSuchPortException;
import pfc.service.interfaces.IService;


/**
 * Handles all interactions between the GUI layer and the data layer.
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 * @see GuiControllerException
 */
public class GuiController {

	/**
	 * Holds a reference to the client interface of the <code>pfc.control.Controller</code>.
	 */
	private IService connection;


	private long lockCookie = 0;

	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>sampleproject.gui</code>.
	 */
	private Logger log = Logger.getLogger("pfc.gui.client");


	/**
	 * Creates a <code>GuiController</code> instance with a specified connection
	 * type.
	 *
	 * @param connectionType the method of connecting the client and database.
	 * @param host the path to the data file, or the network address of
	 * the server hosting the data file.
	 * @param port the port the network server is listening on.
	 * @param portcomm serial port identifier
	 * @param device camera device
	 * @param rtpport RTP port
	 * @throws NoSuchPortException NoSuchPortException
	 * @throws IOException IOException
	 * @throws Exception Exception 
	 */
	public GuiController(ConnectionType connectionType, String host, String port, String portcomm, String device, String rtpport) throws NoSuchPortException, IOException, Exception {
		switch (connectionType) {
		case DIRECT:
			log.info("Creating Local connection.");
			if (device != null && rtpport != null)
				connection = pfc.direct.DataConnector.getLocal(portcomm, device, rtpport);
			else 
				connection = pfc.direct.DataConnector.getLocal(portcomm);
			break;
		case RMI:
			log.info("Creating RMI Remote connection " + host + ":" + port);
			connection = pfc.remote.DataConnector.getRemote(host, port);
			break;
		default:
			throw new IllegalArgumentException ("Invalid connection type specified");
		}
	}


	/**
	 * Get a RMI Service for client-srever communication
	 * @return IService Service Interface
	 * @throws RemoteException RMI Exception
	 */
	public IService getConnection() throws RemoteException {
		return this.connection;
	}

	
	/**
	 * Take the control of the robot by the client
	 * @return long look cookie
	 * @throws RemoteException RMI Exception
	 * @throws IOException RS232 Exception
	 */
	public long takeRobotControl() throws RemoteException, IOException {
		this.lockCookie = connection.takeRobotControl();
		return this.lockCookie; 
	}

	/**
	 * Release the robot control by the client
	 * @return boolean indication the operation result
	 * @throws RemoteException RMI Exception
	 * @throws IOException RS232 Exception
	 */
	public boolean releaseRobotControl() throws RemoteException, IOException {
		return connection.releaseRobotControl(lockCookie);
	}
	
}

