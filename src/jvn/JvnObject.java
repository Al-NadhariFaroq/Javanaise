/***
 * JAVANAISE API
 * Contact: 
 * <p>
 * Authors: 
 */

package jvn;

import java.io.*;

/**
 * Interface of a JVN object.
 * A JVN object is used to acquire read/write locks to access a given shared object
 */
public interface JvnObject extends Serializable {
    /* A JvnObject should be serializable in order to be able to transfer a reference to a JVN object remotely */

    /**
     * Get a Read lock on the shared object
     *
     * @throws JvnException Jvn exception
     **/
    public void jvnLockRead() throws JvnException;

    /**
     * Get a Write lock on the object
     *
     * @throws JvnException Jvn exception
     **/
    public void jvnLockWrite() throws JvnException;

    /**
     * Unlock  the object
     *
     * @throws JvnException Jvn exception
     **/
    public void jvnUnLock() throws JvnException;

    /**
     * Get the object identification
     *
     * @throws JvnException Jvn exception
     **/
    public int jvnGetObjectId() throws JvnException;

    /**
     * Get the shared object associated to this JvnObject
     *
     * @throws JvnException Jvn exception
     **/
    public Serializable jvnGetSharedObject() throws JvnException;

    /**
     * Invalidate the Read lock of the JVN object
     *
     * @throws JvnException Jvn exception
     **/
    public void jvnInvalidateReader() throws JvnException;

    /**
     * Invalidate the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException Jvn exception
     **/
    public Serializable jvnInvalidateWriter() throws JvnException;

    /**
     * Reduce the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException Jvn exception
     **/
    public Serializable jvnInvalidateWriterForReader() throws JvnException;
}
