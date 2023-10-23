package counter;

import jvn.annotation.JvnLockMethod;
import jvn.annotation.JvnLockType;

import java.io.Serializable;

public interface Counter extends Serializable {

	@JvnLockMethod(lockType = JvnLockType.READ)
	long getValue();

	@JvnLockMethod(lockType = JvnLockType.WRITE)
	void increment();
}
