package burst;

import jvn.annotation.JvnLockMethod;

import java.io.Serializable;

public interface ICounter extends Serializable {

    @JvnLockMethod(lockType = "read")
    long getValue();

    @JvnLockMethod(lockType = "write")
    void incrementByOne();
}
