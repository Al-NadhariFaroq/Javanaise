package irc;

import jvn.JvnException;
import jvn.JvnProxy;
import jvn.JvnServerImpl;

public class IrcBurst {
	private final static int nbIterations = 1000;

	public static void main(String[] args) throws JvnException {
		int id = 0;
		if (args.length >= 1) {
			id = Integer.parseInt(args[0]);
		}

		// create a proxy to access at a sentence
		Sentence sentence = (Sentence) JvnProxy.newInstance(SentenceImpl.class, "IRC");

		for (int i = 1; i <= nbIterations; i++) {
			String read = sentence.read();
			int counter = read.isEmpty() ? 0 : Integer.parseInt(read);
			System.out.println(id + ": " + counter);
			sentence.write(String.valueOf(counter + 1));
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
