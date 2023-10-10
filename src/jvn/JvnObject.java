/*
 * JAVANAISE API
 */

package jvn;

import java.io.Serializable;

/**
 * Interface of a JVN object. A JVN object is used to acquire read/write locks to access a given shared object.
 */
public interface JvnObject extends Serializable {
	/* A JvnObject should be serializable in order to be able to transfer a reference to a JVN object remotely */

	/**
	 * Get the object identification
	 *
	 * @return the JVN object identification
	 * @throws JvnException JVN exception
	 **/
	int jvnGetObjectId() throws JvnException;

	/**
	 * Get the shared object associated to this JVN object
	 *
	 * @return the object
	 * @throws JvnException JVN exception
	 **/
	Serializable jvnGetSharedObject() throws JvnException;

	/**
	 * Get a Read lock on the shared object
	 *
	 * @throws JvnException JVN exception
	 **/
	void jvnLockRead() throws JvnException;

	/**
	 * Get a Write lock on the object
	 *
	 * @throws JvnException JVN exception
	 **/
	void jvnLockWrite() throws JvnException;

	/**
	 * Unlock the object
	 *
	 * @throws JvnException JVN exception
	 **/
	void jvnUnLock() throws JvnException;

	/**
	 * Invalidate the Read lock of the JVN object
	 *
	 * @throws JvnException JVN exception
	 **/
	void jvnInvalidateReader() throws JvnException;

	/**
	 * Invalidate the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException JVN exception
	 **/
	Serializable jvnInvalidateWriter() throws JvnException;

	/**
	 * Reduce the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException JVN exception
	 **/
	Serializable jvnInvalidateWriterForReader() throws JvnException;
}
