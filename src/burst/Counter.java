package burst;

import jvn.annotation.JvnLockMethod;

import java.io.Serializable;

public interface Counter extends Serializable {

	@JvnLockMethod(lockType = "read")
	long getValue();

	@JvnLockMethod(lockType = "write")
	void incrementByOne();
}
