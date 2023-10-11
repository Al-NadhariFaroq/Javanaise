package jvn;

import jvn.api.JvnObject;
import jvn.api.JvnRemoteCoord;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        try {
            Registry registry = LocateRegistry.getRegistry(1224);
            JvnRemoteCoord coordinator = (JvnRemoteCoord) registry.lookup("Coordinator");

            JvnServerImpl server = JvnServerImpl.jvnGetServer();

            Text txt = new Text();
            JvnObject jvnTxt = server.jvnCreateObject(txt);
            System.out.println(jvnTxt==null);
            coordinator.jvnRegisterObject("txt", jvnTxt, server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
