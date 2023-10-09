/*
 * JAVANAISE API
 */

package jvn;

import java.io.*;

/**
 * A JVN object is used to acquire read/write locks to access a given shared object
 */
public class JvnObjectImpl implements JvnObject {
    /* A JvnObject should be serializable in order to be able to transfer a reference to a JVN object remotely */
    @Serial
    private static final long serialVersionUID = 1L;

    Serializable object;
    int joi;
    State lock;

    public JvnObjectImpl(Serializable object, int joi) {
        this.object = object;
        this.joi = joi;
        this.lock = State.NL;
    }

    /**
     * Get a Read lock on the shared object
     *
     * @throws JvnException JVN exception
     **/
    public synchronized void jvnLockRead() throws JvnException {
        switch (lock) {
            case NL:
                object = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
                lock = State.R;
                break;
            case RC:
                lock = State.R;
                break;
            case WC:
                lock = State.RWC;
                break;
            default:
                break;
        }
    }

    /**
     * Get a Write lock on the object
     *
     * @throws JvnException JVN exception
     **/
    public synchronized void jvnLockWrite() throws JvnException {
        switch (lock) {
            case NL:
            case R:
            case RC:
                object = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
                lock = State.W;
                break;
            case WC:
            case RWC:
                lock = State.W;
                break;
            default:
                break;
        }
    }

    /**
     * Unlock the object
     *
     * @throws JvnException JVN exception
     **/
    public synchronized void jvnUnLock() throws JvnException {
        switch (lock) {
            case R:
                lock = State.RC;
                break;
            case W:
                lock = State.WC;
                break;
            default:
                break;
        }
        this.notifyAll();
    }

    /**
     * Get the object identification
     *
     * @return the JVN object identification
     * @throws JvnException JVN exception
     **/
    public synchronized int jvnGetObjectId() throws JvnException {
        return joi;
    }

    /**
     * Get the shared object associated to this JVN object
     *
     * @return the object
     * @throws JvnException JVN exception
     **/
    public synchronized Serializable jvnGetSharedObject() throws JvnException {
        return object;
    }

    /**
     * Invalidate the Read lock of the JVN object
     *
     * @throws JvnException JVN exception
     **/
    public synchronized void jvnInvalidateReader() throws JvnException {
        switch (lock) {
            case NL:
                throw new JvnException("No lock taken");
            case R:
            case RWC:
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new JvnException(e.toString());
                }
                lock = State.NL;
                break;
            case RC:
                lock = State.NL;
                break;
            case W:
            case WC:
                throw new JvnException("Impossible to invalide reader because lock is writer");
        }
    }

    /**
     * Invalidate the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException JVN exception
     **/
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        switch (lock) {
            case NL:
                throw new JvnException("No lock taken");
            case R:
            case RC:
                throw new JvnException("Impossible to invalide writer because lock is reader");
            case W:
            case RWC:
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new JvnException(e.toString());
                }
                lock = State.NL;
                break;
            case WC:
                lock = State.NL;
                break;
        }
        return object;
    }

    /**
     * Reduce the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException JVN exception
     **/
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        switch (lock) {
            case NL:
                throw new JvnException("No lock taken");
            case R:
            case RC:
                throw new JvnException("Impossible to invalide writer because lock is reader");
            case W:
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new JvnException(e.toString());
                }
                lock = State.R;
                break;
            case WC:
            case RWC:
                lock = State.R;
                break;
        }
        return object;
    }
}
