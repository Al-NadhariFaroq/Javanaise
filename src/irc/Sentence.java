package irc;

import jvn.annotation.JvnLockMethod;

import java.io.Serializable;

public interface Sentence extends Serializable {

	@JvnLockMethod(lockType = "write")
	void write(String text);

	@JvnLockMethod(lockType = "read")
	String read();
}
