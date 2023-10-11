package jvn;

import jvn.api.JvnObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnProxy implements InvocationHandler {
	private final JvnObject jvnObject;

	public JvnProxy(JvnObject jo) {
		this.jvnObject = jo;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret;
		String lockType = method.getName();

		if (lockType.equals("read")) {
			jvnObject.jvnLockRead();
		} else if (lockType.equals("write")) {
			jvnObject.jvnLockWrite();
		} else {
			throw new JvnException("Error");
		}

		ret = method.invoke(jvnObject.jvnGetSharedObject(), args);
		jvnObject.jvnUnLock();
		return ret;
	}
}
