package jvn;

import jvn.api.JvnObject;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnProxy implements InvocationHandler {
	private JvnObject jo;

	private JvnProxy(JvnObject jo) {
		this.jo = jo;
	}

	public static Object newInstance(JvnObject jo) {
		return java.lang.reflect.Proxy.newProxyInstance(jo.getClass().getClassLoader(),
														jo.getClass().getInterfaces(),
														new JvnProxy(jo)
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
