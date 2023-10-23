package jvn.annotation;

/**
 * Enumeration representing the types of locks used by shared objects in a Javanaise environment. This enum defines two
 * lock types: READ and WRITE.
 */
public enum JvnLockType {
	/**
	 * Represents a READ lock type. Multiple clients can acquire READ locks simultaneously, allowing concurrent
	 * read-only access to a shared object.
	 */
	READ,

	/**
	 * Represents a WRITE lock type. Only one client can acquire a WRITE lock at a time, ensuring exclusive access to
	 * modify a shared object.
	 */
	WRITE
}
