package pfc.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import pfc.remote.DataFactory;

/**
 * RegData starts the rmi registry on the client machine. Registers the
 * Data object for the RMI naming service.
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public class RegData {
    /**
     * Since this is a utility class (it only exists for other classes to call
     * it's static methods), lets stop users creating unneeded instances of
     * this class by creating a private constructor.
     */
    private RegData() {
    }

    /**
     * Creates the DvdDatabase class and binds it to the name "Data".
     *
     * @throws RemoteException on network error.
     */
    public static void register()
    throws RemoteException {
	register(java.rmi.registry.Registry.REGISTRY_PORT);
    }

    /**
     *
     * @param rmiPort the port the RMI Registry will listen on.
     * @throws RemoteException on network error.
     */
    public static void register(int rmiPort)
    throws RemoteException {
	register(rmiPort, null);
    }

    public static void register(int rmiPort, String publishedHost)
    throws RemoteException {
	if (publishedHost != null && publishedHost.trim().length() > 0) {
	    System.setProperty("java.rmi.server.hostname", publishedHost.trim());
	}
	Registry r = java.rmi.registry.LocateRegistry.createRegistry(rmiPort);

	/* make a database instance on a random port and register
	 our service name and our port number on the RMI registry.*/
	//r.rebind("pfc", new DataFactory());
	r.rebind("pfc", new DataRemote(DataRemote.REMOTE_OBJECT_PORT));
    }


    /**
     * Simple entry point so that the RMI server can be started manually for
     * testing purposes.
     *
     * @param args the command line arguments which will be ignored.
     * @throws RemoteException on network error.
     */
    public static void main(String[] args) throws RemoteException {
	register();
    }
}
