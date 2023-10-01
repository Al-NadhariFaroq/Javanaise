/*
 * JAVANAISE API
 * JvnRemoteServer interface
 * Defines the remote interface provided by a JVN server
 * This interface is intended to be invoked by the Javanaise coordinator
 */

package jvn;

import java.rmi.*;
import java.io.*;

/**
 * Remote interface of a JVN server (used by a remote JVN coordinator)
 */
public interface JvnRemoteServer extends Remote {
	/**
	 * Invalidate the Read lock of a JVN object
	 *
	 * @param joi the JVN object identification
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	void jvnInvalidateReader(int joi) throws RemoteException, JvnException;

	/**
	 * Invalidate the Write lock of a JVN object
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	Serializable jvnInvalidateWriter(int joi) throws RemoteException, JvnException;

	/**
	 * Reduce the Write lock of a JVN object
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	Serializable jvnInvalidateWriterForReader(int joi) throws RemoteException, JvnException;
}
