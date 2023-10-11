/*
 * JAVANAISE Implementation
 * JvnObjectImpl class
 * Implementation of a Javanaise object.
 */

package jvn;

import java.io.Serial;
import java.io.Serializable;

/**
 * Implementation of a JVN object. A JVN object is used to acquire read/write locks to access a given shared object.
 */
public class JvnObjectImpl implements JvnObject {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int joi;
    private Serializable jos;
    private JvnLockState jol;

    public JvnObjectImpl(Serializable jos, int joi) {
        this.joi = joi;
        this.jos = jos;
        this.jol = JvnLockState.W;
    }

    public synchronized int jvnGetObjectId() throws JvnException {
        return joi;
    }

    public synchronized Serializable jvnGetSharedObject() throws JvnException {
        return jos;
    }

    public synchronized void jvnLockRead() throws JvnException {
        switch (jol) {
            case NL:
                jos = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
                jol = JvnLockState.R;
                break;
            case RC:
                jol = JvnLockState.R;
                break;
            case WC:
                jol = JvnLockState.RWC;
                break;
            default:
                break;
            }
    }

    public synchronized void jvnLockWrite() throws JvnException {
        switch (jol) {
            case NL:
            case R:
            case RC:
                jos = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
                jol = JvnLockState.W;
                break;
            case WC:
            case RWC:
                jol = JvnLockState.W;
                break;
            default:
                break;
        }
    }

    public synchronized void jvnUnLock() throws JvnException {
        switch (jol) {
            case R:
                jol = JvnLockState.RC;
                break;
            case W:
            case RWC:
                jol = JvnLockState.WC;
                break;
            default:
                break;
        }
        this.notifyAll();
    }

    public synchronized void jvnInvalidateReader() throws JvnException {
        switch (jol) {
            case R:
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new JvnException(e.toString());
                }
                jol = JvnLockState.NL;
                break;
            case RC:
                jol = JvnLockState.NL;
                break;
            case NL:
                throw new JvnException("Unable to invalidate read lock: the object is currently unlock");
            case W:
            case WC:
            case RWC:
                throw new JvnException("Unable to invalidate read lock: the object is currently lock for writing");
        }
    }

    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        switch (jol) {
            case W:
            case RWC:
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new JvnException(e.toString());
                }
                jol = JvnLockState.NL;
                break;
            case WC:
                jol = JvnLockState.NL;
                break;
            case NL:
                throw new JvnException("Unable to invalidate write lock: the object is currently unlock");
            case R:
            case RC:
                throw new JvnException("Unable to invalidate write lock: the object is currently lock for reading");
        }
        return jos;
    }

    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        switch (jol) {
            case W:
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new JvnException(e.toString());
                }
                jol = JvnLockState.R;
                break;
            case WC:
            case RWC:
                jol = JvnLockState.R;
                break;
            case NL:
                throw new JvnException("Unable to invalidate write lock: the object is currently unlock");
            case R:
            case RC:
                throw new JvnException("Unable to invalidate write lock: the object is already lock for reading");
        }
        return jos;
    }
}
