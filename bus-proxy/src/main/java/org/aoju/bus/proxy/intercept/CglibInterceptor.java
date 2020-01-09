package org.aoju.bus.proxy.intercept;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aoju.bus.proxy.aspects.Aspectj;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Cglib实现的动态代理切面
 *
 * @author Kimi Liu
 * @version 5.5.2
 * @since JDK 1.8+
 */
public class CglibInterceptor implements MethodInterceptor {

    private Object target;
    private Aspectj aspectj;

    /**
     * 构造
     *
     * @param target  被代理对象
     * @param aspectj 切面实现
     */
    public CglibInterceptor(Object target, Aspectj aspectj) {
        this.target = target;
        this.aspectj = aspectj;
    }

    public Object getTarget() {
        return this.target;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;

        // 开始前回调
        if (aspectj.before(target, method, args)) {
            try {
                result = proxy.invokeSuper(obj, args);
            } catch (InvocationTargetException e) {
                // 异常回调（只捕获业务代码导致的异常,而非反射导致的异常）
                if (aspectj.afterException(target, method, args, e.getTargetException())) {
                    throw e;
                }
            }
        }

        // 结束执行回调
        if (aspectj.after(target, method, args, result)) {
            return result;
        }
        return null;
    }

}
