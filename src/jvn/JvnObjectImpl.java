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

    public JvnObjectImpl(Serializable object, int joi) {
        this.object = object;
        this.joi = joi;
    }

    /**
     * Get a Read lock on the shared object
     *
     * @throws JvnException JVN exception
     **/
    public void jvnLockRead() throws JvnException {
        try {
            JvnServerImpl.jvnGetServer().jvnLockRead(joi);
        } catch (Exception e) {
            throw new JvnException(e.toString());
        }
    }

    /**
     * Get a Write lock on the object
     *
     * @throws JvnException JVN exception
     **/
    public void jvnLockWrite() throws JvnException {
        try {
            JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
        } catch (Exception e) {
            throw new JvnException(e.toString());
        }
    }

    /**
     * Unlock the object
     *
     * @throws JvnException JVN exception
     **/
    public void jvnUnLock() throws JvnException {
        try {
            JvnServerImpl.jvnGetServer().jvnInvalidateReader(joi);
        } catch (Exception e) {
            throw new JvnException(e.toString());
        }
    }

    /**
     * Get the object identification
     *
     * @return the JVN object identification
     * @throws JvnException JVN exception
     **/
    public int jvnGetObjectId() throws JvnException {
        return joi;
    }

    /**
     * Get the shared object associated to this JVN object
     *
     * @return the object
     * @throws JvnException JVN exception
     **/
    public Serializable jvnGetSharedObject() throws JvnException {
        return object;
    }

    /**
     * Invalidate the Read lock of the JVN object
     *
     * @throws JvnException JVN exception
     **/
    public void jvnInvalidateReader() throws JvnException {
        try {
            JvnServerImpl.jvnGetServer().jvnInvalidateReader(joi);
        } catch (Exception e) {
            throw new JvnException(e.toString());
        }
    }

    /**
     * Invalidate the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException JVN exception
     **/
    public Serializable jvnInvalidateWriter() throws JvnException {
        try {
            return JvnServerImpl.jvnGetServer().jvnInvalidateWriter(joi);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Reduce the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException JVN exception
     **/
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        try {
            return JvnServerImpl.jvnGetServer().jvnInvalidateWriterForReader(joi);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
