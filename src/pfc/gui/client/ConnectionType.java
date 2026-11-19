package pfc.gui.client;

/** Specifies the types of connections that can be made to the server.
 * @author Rodrigo Jimenez 
 * @version 1.0
 */
public enum ConnectionType {
    /** an RMI based server. */
    RMI,
    /** direct connect - no network involved. */
    DIRECT
}

