package counter;

import jvn.JvnException;
import jvn.JvnProxy;
import jvn.JvnServerImpl;

public class CounterBurst {
	private final static int nbIterations = 1000;

	public static void main(String[] args) throws JvnException {
		int id = 0;
		if (args.length >= 1) {
			id = Integer.parseInt(args[0]);
		}

		// create a proxy to access at a sentence
		Counter counter = (Counter) JvnProxy.newInstance(CounterImpl.class, "counter");

		for (int i = 1; i <= nbIterations; i++) {
			System.out.println(id + ": " + counter.getValue());
			counter.increment();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new JvnException();
			}
		}

		JvnServerImpl.jvnGetServer().jvnTerminate();
		System.exit(0);
	}
}
