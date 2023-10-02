/*
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Javanaise server
 */

package jvn;

import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {
	@Serial
	private static final long serialVersionUID = 1L;

	private static JvnServerImpl js = null; // A JVN server is managed as a singleton
	private final JvnRemoteCoord coordinator;

	/**
	 * Default constructor
	 *
	 * @throws JvnException JVN exception
	 **/
	private JvnServerImpl() throws Exception {
		super();
		Registry registry = LocateRegistry.getRegistry(1224);
		coordinator = (JvnRemoteCoord) registry.lookup("Coordinator");
	}

	/**
	 * Static method allowing an application to get a reference to a JVN server instance
	 *
	 * @return the JVN server
	 * @throws JvnException JVN exception
	 **/
	public static JvnServerImpl jvnGetServer() throws Exception {
		if (js == null) {
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				System.out.println("Error creating the Javanaise Server!\n" + e);
				return null;
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 *
	 * @throws JvnException JVN exception
	 **/
	public void jvnTerminate() throws JvnException {
		try {
			coordinator.jvnTerminate(this);
		} catch (RemoteException e) {
			throw new JvnException("Terminate server error!\n" + e);
		}
	}

	/**
	 * Creation of a JVN object
	 *
	 * @param o the JVN object state
	 * @return the new JVN object
	 * @throws JvnException JVN exception
	 **/
	public JvnObject jvnCreateObject(Serializable o) throws JvnException {
		try {
			return new JvnObjectImpl(o, coordinator.jvnGetObjectId());
		} catch (RemoteException e) {
			throw new JvnException("Create object error!\n" + e);
		}
	}

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon the JVN object name
	 * @param jo  the JVN object
	 * @throws JvnException JVN exception
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
		try {
			coordinator.jvnRegisterObject(jon, jo, this);
		} catch (RemoteException e) {
			throw new JvnException("Register object error!\n" + e);
		}
	}

	/**
	 * Provide the reference of a JVN object being given its symbolic name
	 *
	 * @param jon the JVN object name
	 * @return the JVN object
	 * @throws JvnException JVN exception
	 **/
	public JvnObject jvnLookupObject(String jon) throws JvnException {
		try {
			return coordinator.jvnLookupObject(jon,this);
		} catch (RemoteException e) {
			throw new JvnException("Lookup object error!\n" + e);
		}
	}

	/**
	 * Get a Read lock on a JVN object
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException JVN exception
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException {
		try {
			return coordinator.jvnLockRead(joi,this);
		} catch (RemoteException e) {
			throw new JvnException("Lock Read error!\n" + e);
		}
	}

	/**
	 * Get a Write lock on a JVN object
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException JVN exception
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		try {
			return coordinator.jvnLockWrite(joi,this);
		} catch (RemoteException e) {
			throw new JvnException("Lock Write error!\n" + e);
		}
	}

	/**
	 * Invalidate the Read lock of the JVN object identified by id called by the JVN coordinator
	 *
	 * @param joi the JVN object identification
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public void jvnInvalidateReader(int joi) throws RemoteException, JvnException {
		// to be completed
	}

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public Serializable jvnInvalidateWriter(int joi) throws RemoteException, JvnException {
		// to be completed
		return null;
	}

	/**
	 * Reduce the Write lock of the JVN object identified by id
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi) throws RemoteException, JvnException {
		// to be completed
		return null;
	}
}
