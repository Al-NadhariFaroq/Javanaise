package burst;

import jvn.JvnException;
import jvn.JvnProxy;

public class Burst {
	static final int nbClients = 3;
	static final long nbIterations = 1000;

	public static void main(String[] args) {
		Counter counter = null;
		try {
			System.out.println("Main thread: start");

			Thread[] threads = new Thread[nbClients];
			for (int i = 0; i < nbClients; i++) {
				threads[i] = new Thread(new CounterRunnable(nbIterations));
				threads[i].start();
				System.out.println("Main thread: thread " + threads[i].getId() + " started");
			}

			System.out.println("Main thread: all thread created");

			//wait for all threads to finish
			for (int i = 0; i < nbClients; i++) {
				System.out.println("Main thread: wait for thread " + threads[i].getId() + " to finish");
				threads[i].join();
				System.out.println("Main thread: thread " + threads[i].getId() + " finished");
			}

			counter = (Counter) JvnProxy.newInstance(new CounterImpl(), "Counter");
			System.out.println("Actual value = " + counter.getValue());
			System.out.println("Expected value = " + nbClients * nbIterations);
		} catch (JvnException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
