package jvn;

import jvn.api.JvnObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnProxy implements InvocationHandler {
    private JvnObject obj;

    public JvnProxy(JvnObject obj){
        this.obj = obj;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret;
        String lockType = method.getName();

        if(lockType.equals("read"))
            obj.jvnLockRead();
        else if(lockType.equals("write"))
            obj.jvnLockWrite();
        else
            throw new JvnException("Error");

        ret = method.invoke(obj.jvnGetSharedObject(),args);
        obj.jvnUnLock();
        return ret;
    }
}
