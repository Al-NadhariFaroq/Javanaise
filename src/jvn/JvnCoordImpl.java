/*
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 */

package jvn;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
	@Serial
	private static final long serialVersionUID = 1L;

	private final Hashtable<String, JvnObject> objects;
	private final Set<JvnRemoteServer> servers;
	private int nextId;

	/**
	 * Default constructor
	 *
	 * @throws JvnException JVN exception
	 **/
	public JvnCoordImpl() throws Exception {
		objects = new Hashtable<>();
		servers = new HashSet<>();
		nextId = 0;
	}


	/**
	 * Allocate a NEW JVN object id (usually allocated to a newly created JVN object)
	 *
	 * @return the JVN object identification
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public int jvnGetObjectId() throws RemoteException, JvnException {
		// to be completed
		return ++nextId;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon the JVN object name
	 * @param jo  the JVN object
	 * @param js  the remote reference of the JVN server
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws RemoteException, JvnException {
		objects.put(jon, jo);
		servers.add(js);
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server
	 *
	 * @param jon the JVN object name
	 * @param js  the remote reference of the JVN server
	 * @return the JVN object
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
		return objects.get(jon);
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server
	 *
	 * @param joi the JVN object identification
	 * @param js  the remote reference of the JVN server
	 * @return the current JVN object state
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		// to be completed
		return null;
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server
	 *
	 * @param joi the JVN object identification
	 * @param js  the remote reference of the JVN server
	 * @return the current JVN object state
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		// to be completed
		return null;
	}

	/**
	 * A JVN server terminates
	 *
	 * @param js the remote reference of the JVN server
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
		// to be completed
	}
}
