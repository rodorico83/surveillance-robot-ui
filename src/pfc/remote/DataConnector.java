package pfc.remote;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import pfc.remote.interfaces.IDataFactory;
import pfc.remote.interfaces.IDataRemote;

/**
 * A  DataConnector is used in cases where the GUI client wants to make a
 * network connection. In this case, that connection is an RMI connection.
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public class DataConnector {

    /**
     * Since this is a utility class (it only exists for other classes to call
     * it's static methods), lets stop users creating unneeded instances of
     * this class by creating a private constructor.
     */
    private DataConnector() {
    }

    /**
     * Static method that creates an RMI connection.
     * The DB is a remote object.
     *
     * @param hostname The ip or ddress of the host machine.
     * @param port the port the RMI Registry is listening on.
     * @return A DB instance.
     * @throws RemoteException Indicates that a remote instance of the DB
     * interface cannot be created.
     */
    public static IDataRemote getRemote(String hostname, String port)
    throws RemoteException {
	String url = "//" + hostname + ":" + port + "/pfc";

	try {
	    //System.setProperty("java.rmi.server.hostname", hostname);
	    
	    Registry registry = LocateRegistry.getRegistry(hostname, Integer.parseInt(port));
	    //Registry registry = LocateRegistry.getRegistry(hostname);
	    //IDataFactory factory = (IDataFactory) registry.lookup("pfc");
	    return (IDataRemote) registry.lookup("pfc");
	    
	    //IDataFactory factory = (IDataFactory) Naming.lookup(url);
	    
	    
	    //return (IDataRemote) factory.getClient();
	} catch (NotBoundException e) {
	    System.err.println("pfc not registered: "
		    + e.getMessage());
	    e.printStackTrace();
	    throw new RemoteException("pfc not registered: ", e);
	    
	}/* catch (java.net.MalformedURLException e) {
	    System.err.println(hostname + " not valid: " + e.getMessage());
	    e.printStackTrace();
	    throw new RemoteException("cannot connect to " + hostname, e);
	    
	}*/
	catch (Exception e) {
	    System.err.println( " error: " + e.getMessage());
	    e.printStackTrace();
	    throw new RemoteException("cannot connect to " + hostname, e);
	    
	}
    }
}
