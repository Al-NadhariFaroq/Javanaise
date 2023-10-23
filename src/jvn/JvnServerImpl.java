/*
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Javanaise server.
 */

package jvn;

import jvn.api.JvnLocalServer;
import jvn.api.JvnObject;
import jvn.api.JvnRemoteCoord;
import jvn.api.JvnRemoteServer;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

/**
 * Implementation of a JVN server (used by the application and a remote JVN coordinator).
 */
public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {
	@Serial
	private static final long serialVersionUID = 1L;

	private static JvnServerImpl jvnServer = null; // A JVN server is managed as a singleton

	private JvnRemoteCoord coordinator;
	final Hashtable<Integer, JvnObject> objects;

	/**
	 * Default constructor.
	 *
	 * @throws Exception exception
	 **/
	private JvnServerImpl() throws Exception {
		super();
		Registry registry = LocateRegistry.getRegistry(1224);
		coordinator = (JvnRemoteCoord) registry.lookup("Coordinator");
		objects = new Hashtable<>();
	}

	/**
	 * Static method allowing an application to get a reference to a JVN server instance.
	 *
	 * @return the JVN server
	 * @throws JvnException JVN exception
	 **/
	public synchronized static JvnLocalServer jvnGetServer() throws JvnException {
		if (jvnServer == null) {
			try {
				jvnServer = new JvnServerImpl();
			} catch (Exception e) {
				throw new JvnException("Error creating the Javanaise server: coordinator offline");
			}
		}
		return jvnServer;
	}

	public synchronized void jvnTerminate() throws JvnException {
		try {
			coordinator.jvnTerminate(this);
		} catch (RemoteException e) {
			throw new JvnException("Error terminating the Javanaise server: coordinator offline");
		}
	}

	public synchronized JvnObject jvnCreateObject(Serializable o) throws JvnException {
		try {
			return new JvnObjectImpl(o, coordinator.jvnGetObjectId(), JvnLockState.W);
		} catch (RemoteException e) {
			throw new JvnException("Error creating the Javanaise object: coordinator offline");
		}
	}

	public synchronized void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
		try {
			coordinator.jvnRegisterObject(jon, jo, this);
			objects.put(jo.jvnGetObjectId(), jo);
		} catch (RemoteException e) {
			throw new JvnException("Error registering the Javanaise object: coordinator offline");
		}
	}

	public synchronized JvnObject jvnLookupObject(String jon) throws JvnException {
		try {
			JvnObject jo = coordinator.jvnLookupObject(jon, this);
			if (jo != null) {
				objects.put(jo.jvnGetObjectId(), jo);
			}
			return jo;
		} catch (RemoteException e) {
			throw new JvnException("Error looking up the Javanaise object: coordinator offline");
		}
	}

	public synchronized Serializable jvnLockRead(int joi) throws JvnException {
		try {
			return coordinator.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			throw new JvnException("Error read-locking the Javanaise object: coordinator offline");
		}
	}

	public synchronized Serializable jvnLockWrite(int joi) throws JvnException {
		try {
			return coordinator.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			throw new JvnException("Error write-locking the Javanaise object: coordinator offline");
		}
	}

	public synchronized void jvnInvalidateReader(int joi) throws RemoteException, JvnException {
		System.out.println("LocalServer invalidate reader");
		objects.get(joi).jvnInvalidateReader();
	}

	public synchronized Serializable jvnInvalidateWriter(int joi) throws RemoteException, JvnException {
		System.out.println("LocalServer invalidate write");
		return objects.get(joi).jvnInvalidateWriter();
	}

	public synchronized Serializable jvnInvalidateWriterForReader(int joi) throws RemoteException, JvnException {
		System.out.println("LocalServer invalidate writer for reader");
		return objects.get(joi).jvnInvalidateWriterForReader();
	}

	public synchronized void jvnCoordReconnect() throws RemoteException {
		try {
			Registry registry = LocateRegistry.getRegistry(1224);
			coordinator = (JvnRemoteCoord) registry.lookup("Coordinator");
			System.out.println("Coordinator is back online");
		} catch (NotBoundException e) {
			throw new RuntimeException(e);
		}
	}
}
