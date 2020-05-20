/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.proxy.factory.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.proxy.Interceptor;
import org.aoju.bus.proxy.Invoker;
import org.aoju.bus.proxy.Provider;
import org.aoju.bus.proxy.aspects.Aspectj;
import org.aoju.bus.proxy.factory.AbstractFactory;
import org.aoju.bus.proxy.factory.AbstractProxyClass;
import org.aoju.bus.proxy.factory.ProxyClassCache;
import org.aoju.bus.proxy.intercept.JavassistInterceptor;

import java.lang.reflect.Method;

/**
 * Javassist 3.0或更高版本
 *
 * @author Kimi Liu
 * @version 5.9.2
 * @since JDK 1.8+
 */
public class JavassistFactory extends AbstractFactory {

    private static final ProxyClassCache delegatingProxyClassCache = new ProxyClassCache(
            new DelegatingProxyClassGenerator());
    private static final ProxyClassCache interceptorProxyClassCache = new ProxyClassCache(
            new InterceptorProxyClassGenerator());
    private static final ProxyClassCache invocationHandlerProxyClassCache = new ProxyClassCache(
            new InvokerProxyClassGenerator());

    @Override
    public <T> T proxy(T target, Aspectj aspectj) {
        return (T) newProxyInstance(
                target.getClass().getClassLoader(),
                new JavassistInterceptor(target, aspectj),
                target.getClass().getInterfaces());
    }

    public Object createDelegatorProxy(ClassLoader classLoader, Provider targetProvider,
                                       Class[] proxyClasses) {
        try {
            final Class clazz = delegatingProxyClassCache.getProxyClass(classLoader, proxyClasses);
            return clazz.getConstructor(new Class[]{Provider.class})
                    .newInstance(targetProvider);
        } catch (Exception e) {
            throw new InstrumentException("Unable to instantiate proxy from generated proxy class.", e);
        }
    }

    public Object createInterceptorProxy(ClassLoader classLoader, Object target, Interceptor interceptor,
                                         Class[] proxyClasses) {
        try {
            final Class clazz = interceptorProxyClassCache.getProxyClass(classLoader, proxyClasses);
            final Method[] methods = AbstractProxyClass.getImplementationMethods(proxyClasses);
            return clazz.getConstructor(new Class[]{Method[].class, Object.class, Interceptor.class})
                    .newInstance(methods, target, interceptor);
        } catch (Exception e) {
            throw new InstrumentException("Unable to instantiate proxy class instance.", e);
        }
    }

    public Object createInvokerProxy(ClassLoader classLoader, Invoker invoker,
                                     Class[] proxyClasses) {
        try {
            final Class clazz = invocationHandlerProxyClassCache.getProxyClass(classLoader, proxyClasses);
            final Method[] methods = AbstractProxyClass.getImplementationMethods(proxyClasses);
            return clazz.getConstructor(new Class[]{Method[].class, Invoker.class})
                    .newInstance(methods, invoker);
        } catch (Exception e) {
            throw new InstrumentException("Unable to instantiate proxy from generated proxy class.", e);
        }
    }

    private static class InvokerProxyClassGenerator extends AbstractProxyClass {

        public Class createProxy(ClassLoader classLoader, Class[] proxyClasses) {
            try {
                final CtClass proxyClass = JavassistUtils.createClass(getSuperclass(proxyClasses));
                final Method[] methods = getImplementationMethods(proxyClasses);
                JavassistUtils.addInterfaces(proxyClass, toInterfaces(proxyClasses));
                JavassistUtils.addField(Method[].class, "methods", proxyClass);
                JavassistUtils.addField(Invoker.class, "invoker", proxyClass);
                final CtConstructor proxyConstructor = new CtConstructor(
                        JavassistUtils.resolve(
                                new Class[]{Method[].class, Invoker.class}),
                        proxyClass);
                proxyConstructor
                        .setBody("{\n\tthis.methods = $1;\n\tthis.invoker = $2; }");
                proxyClass.addConstructor(proxyConstructor);
                for (int i = 0; i < methods.length; ++i) {
                    final CtMethod method = new CtMethod(JavassistUtils.resolve(methods[i].getReturnType()),
                            methods[i].getName(),
                            JavassistUtils.resolve(methods[i].getParameterTypes()),
                            proxyClass);
                    final String body = "{\n\t return ( $r ) invoker.invoke( this, methods[" + i +
                            "], $args );\n }";
                    method.setBody(body);
                    proxyClass.addMethod(method);
                }
                return proxyClass.toClass(classLoader, null);
            } catch (CannotCompileException e) {
                throw new InstrumentException("Could not compile class.", e);
            }
        }

    }

    private static class InterceptorProxyClassGenerator extends AbstractProxyClass {

        public Class createProxy(ClassLoader classLoader, Class[] proxyClasses) {
            try {
                final CtClass proxyClass = JavassistUtils.createClass(getSuperclass(proxyClasses));
                final Method[] methods = getImplementationMethods(proxyClasses);
                JavassistUtils.addInterfaces(proxyClass, toInterfaces(proxyClasses));
                JavassistUtils.addField(Method[].class, "methods", proxyClass);
                JavassistUtils.addField(Object.class, "target", proxyClass);
                JavassistUtils.addField(Interceptor.class, "intercept", proxyClass);
                final CtConstructor proxyConstructor = new CtConstructor(
                        JavassistUtils.resolve(
                                new Class[]{Method[].class, Object.class, Interceptor.class}),
                        proxyClass);
                proxyConstructor
                        .setBody(
                                "{\n\tthis.methods = $1;\n\tthis.target = $2;\n\tthis.intercept = $3; }");
                proxyClass.addConstructor(proxyConstructor);
                for (int i = 0; i < methods.length; ++i) {
                    final CtMethod method = new CtMethod(JavassistUtils.resolve(methods[i].getReturnType()),
                            methods[i].getName(),
                            JavassistUtils.resolve(methods[i].getParameterTypes()),
                            proxyClass);
                    final Class invocationClass = JavassistInvocation
                            .getMethodInvocationClass(classLoader, methods[i]);
                    final String body = "{\n\t return ( $r ) intercept.intercept( new " + invocationClass.getName() +
                            "( methods[" + i + "], target, $args ) );\n }";
                    method.setBody(body);
                    proxyClass.addMethod(method);

                }
                return proxyClass.toClass(classLoader, null);
            } catch (CannotCompileException e) {
                throw new InstrumentException("Could not compile class.", e);
            }
        }

    }

    private static class DelegatingProxyClassGenerator extends AbstractProxyClass {

        public Class createProxy(ClassLoader classLoader, Class[] proxyClasses) {
            try {
                final CtClass proxyClass = JavassistUtils.createClass(getSuperclass(proxyClasses));
                JavassistUtils.addField(Provider.class, "provider", proxyClass);
                final CtConstructor proxyConstructor = new CtConstructor(
                        JavassistUtils.resolve(new Class[]{Provider.class}),
                        proxyClass);
                proxyConstructor.setBody("{ this.provider = $1; }");
                proxyClass.addConstructor(proxyConstructor);
                JavassistUtils.addInterfaces(proxyClass, toInterfaces(proxyClasses));
                final Method[] methods = getImplementationMethods(proxyClasses);
                for (int i = 0; i < methods.length; ++i) {
                    final Method method = methods[i];
                    final CtMethod ctMethod = new CtMethod(JavassistUtils.resolve(method.getReturnType()),
                            method.getName(),
                            JavassistUtils.resolve(method.getParameterTypes()),
                            proxyClass);
                    final String body = "{ return ( $r ) ( ( " + method.getDeclaringClass().getName() +
                            " )provider.getObject() )." +
                            method.getName() + "($$); }";
                    ctMethod.setBody(body);
                    proxyClass.addMethod(ctMethod);

                }
                return proxyClass.toClass(classLoader, null);
            } catch (CannotCompileException e) {
                throw new InstrumentException("Could not compile class.", e);
            }
        }

    }

}

