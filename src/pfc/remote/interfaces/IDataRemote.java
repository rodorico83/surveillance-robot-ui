package pfc.remote.interfaces;

import java.io.Serializable;
import java.rmi.Remote;

import pfc.service.interfaces.IService;


/**
 * The remote interface for the GUI-Client.
 * Exactly matches the DB interface in the db package.
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public interface IDataRemote extends Remote, IService {
//public interface IDataRemote  extends  Serializable, IService {

}
