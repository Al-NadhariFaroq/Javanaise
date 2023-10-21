package jvn;

import jvn.annotation.JvnLockMethod;
import jvn.api.JvnLocalServer;
import jvn.api.JvnObject;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * {@code JvnProxy} is an invocation handler for creating dynamic proxies that interact with {@code JvnObjects}.
 */
public class JvnProxy implements InvocationHandler {
	private JvnObject jo;

	/**
	 * Constructor for {@code JvnProxy}.
	 *
	 * @param obj  The shared object to be proxied.
	 * @param name The symbolic name of the shared object.
	 * @throws JvnException If there's an issue with Javanaise server initialization or object creation.
	 */
	private JvnProxy(Serializable obj, String name) throws JvnException {
		// Initialize the Javanaise server
		JvnLocalServer js = JvnServerImpl.jvnGetServer();

		// Look up the shared object in the Javanaise server
		jo = js.jvnLookupObject(name);

		// If not found, create it, and register it in the Javanaise server
		if (jo == null) {
			jo = js.jvnCreateObject(obj);
			jo.jvnUnLock();
			js.jvnRegisterObject(name, jo);
		}
	}

	/**
	 * Create a new {@code JvnProxy} instance for a given object.
	 *
	 * @param obj  The shared object to be proxied.
	 * @param name The symbolic name of the shared object.
	 * @return A proxy instance for the provided object.
	 * @throws JvnException If there's an issue with Javanaise server initialization or object creation.
	 */
	public synchronized static Object newInstance(Serializable obj, String name) throws JvnException {
		return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(),
														obj.getClass().getInterfaces(),
														new JvnProxy(obj, name)
		);
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
