/*
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Javanaise server.
 */

package jvn;

import java.io.Serial;
import java.io.Serializable;
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

    private static JvnServerImpl js = null; // A JVN server is managed as a singleton

    private final JvnRemoteCoord coordinator;
    private final Hashtable<Integer, JvnObject> objects;

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
    public static JvnServerImpl jvnGetServer() throws JvnException {
        if (js == null) {
            try {
                js = new JvnServerImpl();
            } catch (Exception e) {
                throw new JvnException("Error creating the Javanaise Server!\n" + e);
            }
        }
        return js;
    }

    public void jvnTerminate() throws JvnException {
        try {
            coordinator.jvnTerminate(this);
        } catch (RemoteException e) {
            throw new JvnException("Terminate server error!\n" + e);
        }
    }

    public JvnObject jvnCreateObject(Serializable o) throws JvnException {
        try {
            return new JvnObjectImpl(o, coordinator.jvnGetObjectId());
        } catch (RemoteException e) {
            throw new JvnException("Create object error!\n" + e);
        }
    }

    public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
        try {
            coordinator.jvnRegisterObject(jon, jo, this);
            objects.put(jo.jvnGetObjectId(), jo);
        } catch (RemoteException e) {
            throw new JvnException("Register object error!\n" + e);
        }
    }

    public JvnObject jvnLookupObject(String jon) throws JvnException {
        try {
            JvnObject jo = coordinator.jvnLookupObject(jon, this);
            if (jo != null) {
                objects.put(jo.jvnGetObjectId(), jo);
            }
            return jo;
        } catch (RemoteException e) {
            throw new JvnException("Lookup object error!\n" + e);
        }
    }

    public Serializable jvnLockRead(int joi) throws JvnException {
        try {
            return coordinator.jvnLockRead(joi, this);
        } catch (RemoteException e) {
            throw new JvnException("Lock Read error!\n" + e);
        }
    }

    public Serializable jvnLockWrite(int joi) throws JvnException {
        try {
            return coordinator.jvnLockWrite(joi, this);
        } catch (RemoteException e) {
            throw new JvnException("Lock Write error!\n" + e);
        }
    }

    public void jvnInvalidateReader(int joi) throws RemoteException, JvnException {
        objects.get(joi).jvnInvalidateReader();
    }

    public Serializable jvnInvalidateWriter(int joi) throws RemoteException, JvnException {
        return objects.get(joi).jvnInvalidateWriter();
    }

    public Serializable jvnInvalidateWriterForReader(int joi) throws RemoteException, JvnException {
        return objects.get(joi).jvnInvalidateWriterForReader();
    }
}
