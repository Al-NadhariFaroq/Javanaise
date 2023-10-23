package jvn;

import jvn.annotation.JvnLockMethod;
import jvn.api.JvnLocalServer;
import jvn.api.JvnObject;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@code JvnProxy} is an invocation handler for creating dynamic proxies that interact with {@code JvnObjects}.
 */
public class JvnProxy implements InvocationHandler {
	private JvnObject jo;

	/**
	 * Constructor for {@code JvnProxy}.
	 *
	 * @param c    The class of the shared object to be proxied.
	 * @param name The symbolic name of the shared object.
	 * @throws JvnException If there's an issue with Javanaise server initialization or object creation.
	 */
	private <T extends Serializable> JvnProxy(Class<T> c, String name) throws JvnException {
		// Initialize the Javanaise server
		JvnLocalServer js = JvnServerImpl.jvnGetServer();

		// Look up the shared object in the Javanaise server
		jo = js.jvnLookupObject(name);

		// If not found, create it, and register it in the Javanaise server
		if (jo == null) {
			try {
				jo = js.jvnCreateObject(c.getDeclaredConstructor().newInstance());
				jo.jvnUnLock();
				js.jvnRegisterObject(name, jo);
			} catch (Exception e) {
				throw new JvnException(e.getMessage());
			}
		}
	}

	/**
	 * Create a new {@code JvnProxy} instance for a given object.
	 *
	 * @param c    The class of the shared object to be proxied.
	 * @param name The symbolic name of the shared object.
	 * @return A proxy instance for the provided object.
	 * @throws JvnException If there's an issue with Javanaise server initialization or object creation.
	 */
	public synchronized static <T extends Serializable> Object newInstance(Class<T> c, String name)
	throws JvnException {
		return java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), c.getInterfaces(), new JvnProxy(c, name));
	}

	/**
	 * Invoke a method on the shared object, optionally applying a lock.
	 *
	 * @param proxy  The proxy object.
	 * @param method The method to invoke.
	 * @param args   The method arguments.
	 * @return The result of the method invocation.
	 * @throws Throwable If there's an issue during method invocation.
	 */
	public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result;

		// Check if the method has a JvnLockMethod annotation
		JvnLockMethod lockMethod = method.getAnnotation(JvnLockMethod.class);
		if (lockMethod == null) {
			// Execute the designated method without locking
			result = method.invoke(jo.jvnGetSharedObject(), args);
		} else {
			// Lock the JVN object based on the annotation's lock type
			switch (lockMethod.lockType()) {
				case READ:
					jo.jvnLockRead();
					break;
				case WRITE:
					jo.jvnLockWrite();
					break;
			}
			// Execute the designated method, then release any locks
			result = method.invoke(jo.jvnGetSharedObject(), args);
			jo.jvnUnLock();
		}
		return result;
	}
}
