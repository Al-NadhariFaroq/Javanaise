package jvn;

import jvn.api.JvnLocalServer;
import jvn.api.JvnObject;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnProxy implements InvocationHandler {
	private JvnObject jo;

	private JvnProxy(Serializable jos) throws JvnException {
		// initialize JVN server
		JvnLocalServer js = JvnServerImpl.jvnGetServer();

		// look up the IRC object in the JVN server if not found, create it, and register it in the JVN server
		jo = js.jvnLookupObject("IRC");
		if (jo == null) {
			jo = js.jvnCreateObject(jos);
			// after creation, I have a write-lock on the object
			jo.jvnUnLock();
			js.jvnRegisterObject("IRC", jo);
		}
	}

	public static Object newInstance(Serializable jos) throws JvnException {
		return java.lang.reflect.Proxy.newProxyInstance(jos.getClass().getClassLoader(),
														jos.getClass().getInterfaces(),
														new JvnProxy(jos)
		);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
			case "read":
				jo.jvnLockRead();
				break;
			case "write":
				jo.jvnLockWrite();
				break;
			default:
				throw new JvnException("Invoke error: unknown method name");
		}
		Object ret = method.invoke(jo.jvnGetSharedObject(), args);
		jo.jvnUnLock();
		return ret;
	}
}
