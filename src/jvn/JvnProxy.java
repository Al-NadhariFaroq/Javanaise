package jvn;

import irc.Sentence;
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
			jo = js.jvnCreateObject(new Sentence());
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
		String methodName = method.getName();
		if (methodName.equals("read")) {
			jo.jvnLockRead();
		} else if (methodName.equals("write")) {
			jo.jvnLockWrite();
		} else {
			throw new JvnException("Error");
		}

		Object ret = method.invoke(jo.jvnGetSharedObject(), args);
		jo.jvnUnLock();
		return ret;
	}
}
