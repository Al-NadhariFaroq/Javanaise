/*
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * Implementation of the Javanaise central coordinator.
 */

package jvn;

import jvn.api.JvnObject;
import jvn.api.JvnRemoteCoord;
import jvn.api.JvnRemoteServer;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Implementation of the JVN Coordinator.
 */
class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
	@Serial
	private static final long serialVersionUID = 1L;

	private int nextId;

	private final Hashtable<String, Integer> names;
	private final Hashtable<Integer, JvnObjectData> objects;

	/**
	 * Default constructor.
	 *
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public JvnCoordImpl() throws RemoteException, JvnException {
		nextId = 0;
		objects = new Hashtable<>();
		names = new Hashtable<>();

		// Bind the coordinator remote object's stub in the RMI registry
		Registry registry = LocateRegistry.createRegistry(1224);
		registry.rebind("Coordinator", this);
		System.out.println("Javanaise central coordinator is ready.");
	}

	public synchronized int jvnGetObjectId() throws RemoteException, JvnException {
		return nextId++;
	}

	public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
	throws RemoteException, JvnException {
		if (jo == null) {
			throw new JvnException("Unable to register the object: invalid null object");
		} else if (names.containsKey(jon)) {
			throw new JvnException("Unable to register the object: the symbolic name '" + jon + "' is already use");
		}

		int joi = jo.jvnGetObjectId();
		names.put(jon, joi);

		if (!objects.containsKey(joi)) {
			objects.put(joi, new JvnObjectData(new JvnObjectImpl(jo.jvnGetSharedObject(), joi, JvnLockState.NL), js));
		}

		System.out.println("Registration of object " + jo.jvnGetObjectId() + " as '" + jon + "'");
	}

	public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
		if (!names.containsKey(jon)) {
			System.out.println("Not found object named '" + jon + "'");
			return null;
		}

		System.out.println("Found the object named '" + jon + "'");
		JvnObjectData data = objects.get(names.get(jon));
		data.getServers().add(js);
		return data.getJvnObject();
	}

	public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		JvnObjectData data = objects.get(joi);

		JvnRemoteServer writeServer = data.getWriteServer();
		if (writeServer != null) {
			Serializable jos = writeServer.jvnInvalidateWriterForReader(joi);
			data.setJvnObject(new JvnObjectImpl(jos, joi, JvnLockState.NL));
			data.getReadServers().add(writeServer);
			data.setWriteServer(null);
		}

		System.out.println("Object " + joi + " lock for reading");
		data.getReadServers().add(js);
		return data.getJvnObject().jvnGetSharedObject();
	}

	public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		JvnObjectData data = objects.get(joi);

		Iterator<JvnRemoteServer> readServersIt = data.getReadServers().iterator();
		while (readServersIt.hasNext()) {
			JvnRemoteServer readServer = readServersIt.next();
			if (!readServer.equals(js)) {
				readServer.jvnInvalidateReader(joi);
			}
			readServersIt.remove();
		}

		JvnRemoteServer writeServer = data.getWriteServer();
		if (writeServer != null) {
			Serializable jos = writeServer.jvnInvalidateWriter(joi);
			data.setJvnObject(new JvnObjectImpl(jos, joi, JvnLockState.NL));
			data.setWriteServer(null);
		}

		System.out.println("Object " + joi + " lock for writing");
		data.setWriteServer(js);
		return data.getJvnObject().jvnGetSharedObject();
	}

	public synchronized void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
		for (JvnObjectData data : objects.values()) {
			if (data.getWriteServer() == js) {
				Serializable jos = js.jvnInvalidateWriter(data.getJvnObject().jvnGetObjectId());
				data.setJvnObject(new JvnObjectImpl(jos, data.getJvnObject().jvnGetObjectId(), JvnLockState.NL));
				data.setWriteServer(null);
			}
			data.getReadServers().remove(js);
			data.getServers().remove(js);
		}
		System.out.println("Terminate server");
	}
}
