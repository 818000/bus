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
package org.aoju.bus.limiter.metadata;

import org.aoju.bus.limiter.Handler;
import org.aoju.bus.limiter.Injector;
import org.aoju.bus.limiter.Limiter;
import org.aoju.bus.limiter.Resolver;
import org.aoju.bus.limiter.resource.LimitedResource;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author Kimi Liu
 * @version 5.8.8
 * @since JDK 1.8+
 */
public interface LimitedResourceMetadata<T extends LimitedResource> {

    Class<?> getTargetClass();

    Method getTargetMethod();

    T getLimitedResource();

    Limiter getLimiter();

    Handler getErrorHandler();

    Resolver getFallback();

    Collection<Injector> getArgumentInjectors();

    Map<String, Object> getLimiterParameters();

}
