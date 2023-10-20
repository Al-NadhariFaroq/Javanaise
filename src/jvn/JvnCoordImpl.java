/*
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * Implementation of the Javanaise central coordinator.
 */

package jvn;

import jvn.api.JvnObject;
import jvn.api.JvnRemoteCoord;
import jvn.api.JvnRemoteServer;

import java.io.*;
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

	private Hashtable<String, Integer> names;
	private Hashtable<Integer, JvnObjectData> objects;

	private final String SERVER_FILE_STATUS = "status.serv";


	/**
	 * Default constructor.
	 *
	 * @throws RemoteException Java RMI exception
	 * @throws JvnException    JVN exception
	 **/
	public JvnCoordImpl() throws RemoteException, JvnException {
		objects = new Hashtable<>();
		names = new Hashtable<>();
		nextId = 1;

		// Bind the coordinator remote object's stub in the RMI registry
		Registry registry = LocateRegistry.createRegistry(1224);
		registry.rebind("Coordinator", this);
		System.out.println("Javanaise central coordinator is ready.");
		restoreStatus();
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
		System.out.println("Read lock demanded");
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
		System.out.println("Write lock demanded");
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
		saveStatus();
		return data.getJvnObject().jvnGetSharedObject();
	}

	public synchronized void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
		for (JvnObjectData data : objects.values()) {
			if (js.equals(data.getWriteServer())) {
				Serializable jos = js.jvnInvalidateWriter(data.getJvnObject().jvnGetObjectId());
				data.setJvnObject(new JvnObjectImpl(jos, data.getJvnObject().jvnGetObjectId(), JvnLockState.NL));
				data.setWriteServer(null);
			}
			data.getReadServers().remove(js);
			data.getServers().remove(js);
		}
		System.out.println("Terminate server");
	}

	void saveStatus(){
		try {
			FileOutputStream fileOut = new FileOutputStream(SERVER_FILE_STATUS);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
			System.out.println("Object has been serialized and written to myObject.ser");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void restoreStatus(){
		try {
			FileInputStream fileIn = new FileInputStream(SERVER_FILE_STATUS);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			JvnCoordImpl loadedObject = (JvnCoordImpl) in.readObject();

			nextId = loadedObject.nextId;
			objects = loadedObject.objects;
			names = loadedObject.names;

			in.close();
			fileIn.close();
			// Now, 'loadedObject' contains the deserialized object
		} catch (IOException e){

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


}
