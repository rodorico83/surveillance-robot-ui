package pfc.direct;


import pfc.service.Service;
import pfc.service.interfaces.IService;
import java.io.*;
import gnu.io.NoSuchPortException;

/**
 * A  DataConnector is used in cases where the GUI client wants to make a
 * connection to the data file. In this case, that connection is an direct
 * connection.
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
     * Static method that gets a server handle.
     * The controller and VideoController are a local object.
     * @param commport serial port name
     * @param device camera device name
     * @param rtpport rtp port to transmit
     * @return IService
     *
     * @throws Exception Exception  
     * @throws Exception Exception 
     * @throws IOException Thrown if an <code>IOException</code> is
     * encountered in the <code>Data</code> class.
     * <br> 
     */
    public static IService getLocal(String commport, String device, String rtpport) throws IOException, Exception
      {
	return new Service(commport, device, rtpport);
    }
    
    /**
     * Static method that gets a server handle.
     * The controller and VideoController are a local object.
     * @param commport serial port name
     * @return IService
     * 
     * @throws Exception Exception 
     * @throws NoSuchPortException NoSuchPortException 
     * @throws IOException Thrown if an <code>IOException</code> is
     * encountered in the <code>Data</code> class.
     * <br>  
     */
    public static IService getLocal(String commport) throws NoSuchPortException, Exception
      {
	return new Service(commport);
    }
    
    
    
    
}
