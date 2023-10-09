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

    Hashtable<Integer, JvnObjectData> objects;
    private int nextId;

    /**
     * Default constructor
     *
     * @throws JvnException JVN exception
     **/
    public JvnCoordImpl() throws Exception {
        objects = new Hashtable<>();
        nextId = 0;
    }

    /**
     * Allocate a NEW JVN object id (usually allocated to a newly created JVN object)
     *
     * @return the JVN object identification
     * @throws RemoteException Java RMI exception
     * @throws JvnException    JVN exception
     **/
    public synchronized int jvnGetObjectId() throws RemoteException, JvnException {
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
    public synchronized void jvnRegisterObject(String jon,
                                               JvnObject jo,
                                               JvnRemoteServer js) throws RemoteException, JvnException {
        int joi = jo.jvnGetObjectId();
        System.out.println("Register object of id: " + joi + " with the name: " + jon);
        if (objects.get(joi) == null) {
            JvnObjectData data = new JvnObjectData(joi, jo, jon, js);
            objects.put(joi, data);
        } else {
            objects.get(joi).addName(jon);
        }
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
    public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
        System.out.print("Look up for object named: " + jon + " ");
        JvnObjectData data = null;
        for (JvnObjectData curData : objects.values()) {
            if (curData.containsName(jon)) {
                data = curData;
                break;
            }
        }
        if (data == null) {
            System.out.println("Not find!");
            return null;
        }
        System.out.println("Find!");
        data.addServer(js);
        return data.getJvnObject();
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
    public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObjectData objectData = objects.get(joi);

        JvnRemoteServer serverW = objectData.findWriteLockServer();
        if (serverW != null) {
            if (!serverW.equals(js)) {
                objectData.setJvnObject(new JvnObjectImpl(serverW.jvnInvalidateWriter(joi), joi));
                objectData.updateLock(serverW, State.NL);
            }
        }

        objectData.updateLock(js, State.R);
        return objectData.getJvnObject().jvnGetSharedObject();
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
    public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObjectData objectData = objects.get(joi);

        JvnRemoteServer serverW = objectData.findWriteLockServer();
        if (serverW != null) {
            objectData.setJvnObject(new JvnObjectImpl(serverW.jvnInvalidateWriter(joi), joi));
            objectData.updateLock(serverW, State.NL);
        } else {
            for (JvnRemoteServer server : objectData.findReadLockServers()) {
                if (!server.equals(js)) {
                    server.jvnInvalidateReader(joi);
                    objectData.updateLock(server, State.NL);
                }
            }
        }

        objectData.updateLock(js, State.W);
        return objectData.getJvnObject().jvnGetSharedObject();
    }

    /**
     * A JVN server terminates
     *
     * @param js the remote reference of the JVN server
     * @throws RemoteException Java RMI exception
     * @throws JvnException    JVN exception
     **/
    public synchronized void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
        for (JvnObjectData data : objects.values()) {
            if (data.containsServer(js)) {
                State lock = data.getServerLock(js);
                switch (lock) {
                    case R:
                        js.jvnInvalidateReader(data.getJvnObjectId());
                        break;
                    case W:
                        data.setJvnObject(new JvnObjectImpl(js.jvnInvalidateWriter(data.getJvnObjectId()), data.getJvnObjectId()));
                        break;
                    default:
                        break;
                }
            }
            data.removeServer(js);
        }
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        for (JvnObjectData data : objects.values()) {
            txt.append(data.toString());
        }
        return txt.toString();
    }
}
