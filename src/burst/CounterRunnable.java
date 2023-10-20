package burst;

import jvn.JvnException;
import jvn.JvnProxy;

public class CounterRunnable implements Runnable {
	long nbIterations;

	public CounterRunnable(long nbIterations) {
		this.nbIterations = nbIterations;
	}

	@Override
	public void run() {
		long tid = Thread.currentThread().getId();
		try {
			Counter counter = (Counter) JvnProxy.newInstance(new CounterImpl(), "Counter");
			System.out.println("Thread " + tid + ": shared counter found (or created)");
			for (int i = 1; i <= nbIterations; i++) {
				System.out.println("Thread " + tid + ": start iteration " + i);
				counter.incrementByOne();
				System.out.println("Thread " + tid + ": end iteration " + i);
			}
			System.out.println("Thread " + tid + ": all iterations ended");
		} catch (JvnException e) {
			throw new RuntimeException(e);
		}
	}
}
