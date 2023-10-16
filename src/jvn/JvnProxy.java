package jvn;

import jvn.annotation.JvnLockMethod;
import jvn.api.JvnLocalServer;
import jvn.api.JvnObject;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnProxy implements InvocationHandler {
	private JvnObject jo;

	private JvnProxy(Serializable jos, String name) throws JvnException {
		// initialize JVN server
		JvnLocalServer js = JvnServerImpl.jvnGetServer();

		// look up the IRC object in the JVN server if not found, create it, and register it in the JVN server
		jo = js.jvnLookupObject(name);
		if (jo == null) {
			jo = js.jvnCreateObject(jos);
			// after creation, I have a write-lock on the object
			jo.jvnUnLock();
			js.jvnRegisterObject(name, jo);
		}
	}

	public static Object newInstance(Serializable jos, String name) throws JvnException {
		return java.lang.reflect.Proxy.newProxyInstance(jos.getClass().getClassLoader(),
														jos.getClass().getInterfaces(),
														new JvnProxy(jos, name)
		);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		JvnLockMethod lockType = method.getAnnotation(JvnLockMethod.class);
		if (lockType != null) {
			switch (lockType.lockType()) {
				case "read":
					jo.jvnLockRead();
					break;
				case "write":
					jo.jvnLockWrite();
					break;
				default:
					throw new JvnException("Invoke error: unknown method lock type");
			}
		} else {
			throw new JvnException("Invoke error: method is not locked");
		}

		Object result = method.invoke(jo.jvnGetSharedObject(), args);
		jo.jvnUnLock();
		return result;
	}
}
