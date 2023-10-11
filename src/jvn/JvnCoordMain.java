package jvn;

public class JvnCoordMain {
	public static void main(String[] args) {
		try {
			// Create a coordinator remote object
			new JvnCoordImpl();
		} catch (Exception e) {
			System.err.println("Server exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
