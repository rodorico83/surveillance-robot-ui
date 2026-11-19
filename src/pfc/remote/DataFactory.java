package pfc.remote;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import pfc.remote.interfaces.IDataFactory;
import pfc.remote.interfaces.IDataRemote;

/**
 * The implementation of our remote factory for client connectivity.
 * @author Rodrigo Jimenez
 * @version 1.0
 */
class DataFactory extends UnicastRemoteObject
implements IDataFactory {
    /**
     * A version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */ 
    private static final long serialVersionUID = 5165L;

    
    /**
     * Creates an instance of this factory, specifying where the database can
     * be found.
     * @throws RemoteException Thrown if a <code>DataImpl</code>
     * instance cannot be created. 
     */
    public DataFactory() throws RemoteException {

    }

    /** 
     * Gets a Client of Data 
     * @return Service
     */
    public IDataRemote getClient() throws RemoteException {
	return new DataRemote();
    }
}
