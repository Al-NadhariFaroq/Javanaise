package burst;

import jvn.JvnException;
import jvn.JvnProxy;

public class CounterRunnable implements Runnable {
    long iterations;

    public CounterRunnable(long iterations){
        this.iterations = iterations;
    }

    @Override
    public void run() {
        long th = Thread.currentThread().getId();
        try {
            ICounter counter = (ICounter) JvnProxy.newInstance(new Counter(), "Counter");
            System.out.println("Thread Created: " +th);
            for(int i = 0; i < iterations; i++){
                counter.incrementByOne();
                System.out.println(th + "-> " +i);
            }
        } catch (JvnException e) {
            throw new RuntimeException(e);
        }
    }
}
