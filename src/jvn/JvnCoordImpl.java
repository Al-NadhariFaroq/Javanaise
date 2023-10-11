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
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

/**
 * Implementation of the JVN Coordinator.
 */
public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Hashtable<Integer, JvnObjectData> objects;
    private int nextId;

    /**
     * Default constructor.
     *
     * @throws RemoteException Java RMI exception
     * @throws JvnException JVN exception
     **/
    public JvnCoordImpl() throws RemoteException, JvnException {
        objects = new Hashtable<>();
        nextId = 0;
    }

    public synchronized int jvnGetObjectId() throws RemoteException, JvnException {
        return ++nextId;
    }

    public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws RemoteException, JvnException {
        int joi = jo.jvnGetObjectId();
        if (objects.get(joi) == null) {
            JvnObjectData data = new JvnObjectData(joi, jo, jon, js);
            objects.put(joi, data);
        } else {
            objects.get(joi).addName(jon);
        }
    }

    public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObjectData data = null;
        for (JvnObjectData curData : objects.values()) {
            if (curData.containsName(jon)) {
                data = curData;
                data.addServer(js);
                jvnLockWrite(data.getJvnObjectId(),js);
                break;
            }
        }

        return data != null ? data.getJvnObject() : null;
    }

    public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObjectData objectData = objects.get(joi);
        JvnRemoteServer serverW = objectData.findWriteLockServer();
        if (serverW != null) {
            if(serverW.equals(js)){
                objectData.setJvnObject(new JvnObjectImpl(serverW.jvnInvalidateWriterForReader(joi), joi));
                objectData.updateLock(serverW, JvnLockState.R);
            }
            else{
                objectData.setJvnObject(new JvnObjectImpl(serverW.jvnInvalidateWriter(joi), joi));
                objectData.updateLock(serverW, JvnLockState.NL);
            }
        }
        objectData.updateLock(js, JvnLockState.R);
        return objectData.getJvnObject().jvnGetSharedObject();
    }

    public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObjectData objectData = objects.get(joi);
        JvnRemoteServer serverW = objectData.findWriteLockServer();
        if (serverW != null) {
            objectData.setJvnObject(new JvnObjectImpl(serverW.jvnInvalidateWriter(joi), joi));
            objectData.updateLock(serverW, JvnLockState.NL);
        } else {
            for (JvnRemoteServer server : objectData.findReadLockServers()) {
                if (!server.equals(js)) {
                    server.jvnInvalidateReader(joi);
                    objectData.updateLock(server, JvnLockState.NL);
                }
            }
        }
        objectData.updateLock(js, JvnLockState.W);
        return objectData.getJvnObject().jvnGetSharedObject();
    }

    public synchronized void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
        for (JvnObjectData data : objects.values()) {
            if (data.containsServer(js)) {
                JvnLockState lock = data.getServerLock(js);
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
            txt.append(data.toString()).append("\n");
        }
        if (!objects.values().isEmpty()) {
            txt.delete(txt.length() - 1, txt.length());
        }
        return txt.toString();
    }
}
