/*
 * JAVANAISE API
 * JvnLocalServer interface
 * Defines the local interface provided by a JVN server.
 * An application uses the Javanaise service through the local interface provided by the JVN server.
 */

package jvn.api;

import jvn.JvnException;

import java.io.Serializable;

/**
 * Local interface of a JVN server (used by the applications). An application can get the reference of a JVN server
 * through the static method jvnGetServer() (see JvnServerImpl).
 **/
public interface JvnLocalServer {
	/**
	 * Create of a JVN object
	 *
	 * @param jos the JVN object state
	 * @return the JVN object
	 * @throws JvnException JVN exception
	 **/
	JvnObject jvnCreateObject(Serializable jos) throws JvnException;

	/**
	 * Associate a symbolic name with a JVN object
	 *
	 * @param jon the JVN object name
	 * @param jo  the JVN object
	 * @throws JvnException JVN exception
	 **/
	void jvnRegisterObject(String jon, JvnObject jo) throws JvnException;

	/**
	 * Get the reference of a JVN object associated to a symbolic name
	 *
	 * @param jon the JVN object symbolic name
	 * @return the JVN object
	 * @throws JvnException JVN exception
	 **/
	JvnObject jvnLookupObject(String jon) throws JvnException;

	/**
	 * Get a Read lock on a JVN object
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException JVN exception
	 **/
	Serializable jvnLockRead(int joi) throws JvnException;

	/**
	 * Get a Write lock on a JVN object
	 *
	 * @param joi the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException JVN exception
	 **/
	Serializable jvnLockWrite(int joi) throws JvnException;

	/**
	 * The JVN service is not used anymore by the application
	 *
	 * @throws JvnException JVN exception
	 **/
	void jvnTerminate() throws JvnException;
}
