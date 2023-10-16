package jvn;

public class BurstTest {
	private static final int N = 3;

	public static void main(String[] args) throws InterruptedException {
		new Thread(() -> JvnCoordMain.main(null)).start();

		for (int i = 0; i < N; i++) {
			new Thread(() -> Irc.main(null)).start();
			Thread.sleep(500);
		}
	}
}
