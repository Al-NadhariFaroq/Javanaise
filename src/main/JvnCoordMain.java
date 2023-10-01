package main;

import jvn.JvnCoordImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JvnCoordMain {
    public static void main(String[] args) {
        try {
            // Create a coordinator remote object
            JvnCoordImpl jvnCoord = new JvnCoordImpl();

            // Bind the coordinator remote object's stub in the RMI registry
            Registry registry = LocateRegistry.createRegistry(1224);
            registry.rebind("Coordinator", jvnCoord);
            System.out.println("Coordinator server is ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}
