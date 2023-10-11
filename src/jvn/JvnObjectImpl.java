/*
 * JAVANAISE Implementation
 * JvnObjectImpl class
 * Implementation of a Javanaise object.
 */

package jvn;

import jvn.api.JvnObject;

import java.io.Serial;
import java.io.Serializable;

/**
 * Implementation of a JVN object. A JVN object is used to acquire read/write locks to access a given shared object.
 */
public class JvnObjectImpl implements JvnObject {
	@Serial
	private static final long serialVersionUID = 1L;

	enum JvnLockState {NL, R, W, RC, WC, RWC}

	private final int jvnObjectId;
	private Serializable jvnObjectState;
	private JvnLockState jvnObjectLock;

	public JvnObjectImpl(Serializable jos, int joi) {
		jvnObjectId = joi;
		jvnObjectState = jos;
		jvnObjectLock = JvnLockState.NL;
	}

	public synchronized int jvnGetObjectId() throws JvnException {
		return jvnObjectId;
	}

	public synchronized Serializable jvnGetSharedObject() throws JvnException {
		/*if (jvnObjectLock.equals(JvnLockState.R) || jvnObjectLock.equals(JvnLockState.W) ||
			jvnObjectLock.equals(JvnLockState.RWC)) {*/
		return jvnObjectState;
		/*} else {
			throw new JvnException("Unable to get the shared object: the object is not locked");
		}*/
	}

	public synchronized void jvnLockRead() throws JvnException {
		switch (jvnObjectLock) {
			case NL:
				jvnObjectState = JvnServerImpl.jvnGetServer().jvnLockRead(jvnObjectId);
				jvnObjectLock = JvnLockState.R;
				break;
			case RC:
				jvnObjectLock = JvnLockState.R;
				break;
			case WC:
				jvnObjectLock = JvnLockState.RWC;
				break;
			case R:
				throw new JvnException("Unable to read lock: the object is already lock for reading");
			case W:
				throw new JvnException("Unable to read lock: the object is currently lock for writing");
		}
		System.out.println("Object lock for reading");
	}

	public synchronized void jvnLockWrite() throws JvnException {
		switch (jvnObjectLock) {
			case NL:
			case R:
			case RC:
				jvnObjectState = JvnServerImpl.jvnGetServer().jvnLockWrite(jvnObjectId);
				jvnObjectLock = JvnLockState.W;
				break;
			case WC:
			case RWC:
				jvnObjectLock = JvnLockState.W;
				break;
			case W:
				throw new JvnException("Unable to write lock: the object is already lock for writing");
		}
		System.out.println("Object lock for writing");
	}

	public synchronized void jvnUnLock() throws JvnException {
		switch (jvnObjectLock) {
			case R:
				jvnObjectLock = JvnLockState.RC;
				break;
			case W:
			case RWC:
				jvnObjectLock = JvnLockState.WC;
				break;
			default:
				throw new JvnException("Unable to unlock: the object is already unlock");
		}
		System.out.println("Object unlock");
		this.notifyAll();
	}

	/**
	 * Wait to be notified by the unlock method.
	 *
	 * @throws JvnException JVN exception
	 */
	private void waitUnlock() throws JvnException {
		try {
			this.wait();
		} catch (InterruptedException e) {
			throw new JvnException(e.toString());
		}
	}

	public synchronized void jvnInvalidateReader() throws JvnException {
		switch (jvnObjectLock) {
			case R:
				waitUnlock();
				jvnObjectLock = JvnLockState.NL;
				break;
			case RC:
				jvnObjectLock = JvnLockState.NL;
				break;
			case NL:
				throw new JvnException("Unable to invalidate read lock: the object is currently unlock");
			case W:
			case WC:
			case RWC:
				throw new JvnException("Unable to invalidate read lock: the object is currently lock for writing");
		}
		System.out.println("Invalidate read lock");
	}

	public synchronized Serializable jvnInvalidateWriter() throws JvnException {
		switch (jvnObjectLock) {
			case W:
			case RWC:
				waitUnlock();
				jvnObjectLock = JvnLockState.NL;
				break;
			case WC:
				jvnObjectLock = JvnLockState.NL;
				break;
			case NL:
				throw new JvnException("Unable to invalidate write lock: the object is currently unlock");
			case R:
			case RC:
				throw new JvnException("Unable to invalidate write lock: the object is currently lock for reading");
		}
		System.out.println("Invalidate write lock");
		return jvnObjectState;
	}

	public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
		switch (jvnObjectLock) {
			case W:
				waitUnlock();
				jvnObjectLock = JvnLockState.R;
				break;
			case WC:
				jvnObjectLock = JvnLockState.RC;
				break;
			case RWC:
				jvnObjectLock = JvnLockState.R;
				break;
			case NL:
				throw new JvnException("Unable to invalidate write lock: the object is currently unlock");
			case R:
			case RC:
				throw new JvnException("Unable to invalidate write lock: the object is already lock for reading");
		}
		System.out.println("Invalidate write lock for read lock");
		return jvnObjectState;
	}
}
