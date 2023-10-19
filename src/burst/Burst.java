package burst;

import jvn.JvnException;
import jvn.JvnProxy;


public class Burst {
    static final int iterations = 1000;
    static final int  NbClient = 3;

    public static void main(String[] args) {
        ICounter counter = null;
        try {
            counter = (ICounter) JvnProxy.newInstance(new Counter(), "Counter");
            Thread.sleep(3000);

            Thread[] threads = new Thread[NbClient];
            for (int i = 0 ;i < NbClient; i++){
                threads[i] = new Thread(new CounterRunnable(iterations));
                threads[i].start();
            }

            //wait for all threads to finish
            for (int i = 0 ;i < NbClient; i++)
                threads[i].join();

            System.out.println("Actual Value = " + counter.getValue() + "; Expected Value = " + NbClient*iterations);
        } catch (JvnException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
