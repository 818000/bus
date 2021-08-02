/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.health.builtin;

import org.aoju.bus.core.lang.System;
import org.aoju.bus.health.Platform;

import java.io.Serializable;

/**
 * 代表Java Virutal Machine Specification的信息
 *
 * @author Kimi Liu
 * @version 6.2.6
 * @since JDK 1.8+
 */
public class JvmSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 取得当前JVM spec.的名称(取自系统属性：<code>java.vm.specification.name</code>)
     *
     * <p>
     * 例如Sun JDK 1.4.2：<code>"Java Virtual Machine Specification"</code>
     * </p>
     *
     * @return 属性值, 如果不能取得(因为Java安全限制)或值不存在,则返回<code>null</code>
     */
    public final String getName() {
        return Platform.get(System.VM_SPECIFICATION_NAME, false);
    }

    /**
     * 取得当前JVM spec.的版本(取自系统属性：<code>java.vm.specification.version</code>)
     *
     * <p>
     * 例如Sun JDK 1.4.2：<code>"1.0"</code>
     * </p>
     *
     * @return 属性值, 如果不能取得(因为Java安全限制)或值不存在,则返回<code>null</code>
     */
    public final String getVersion() {
        return Platform.get(System.VM_SPECIFICATION_VERSION, false);
    }

    /**
     * 取得当前JVM spec.的厂商(取自系统属性：<code>java.vm.specification.vendor</code>)
     *
     * <p>
     * 例如Sun JDK 1.4.2：<code>"Sun Microsystems Inc."</code>
     * </p>
     *
     * @return 属性值, 如果不能取得(因为Java安全限制)或值不存在,则返回<code>null</code>
     */
    public final String getVendor() {
        return Platform.get(System.VM_SPECIFICATION_VENDOR, false);
    }

    /**
     * 将Java Virutal Machine Specification的信息转换成字符串
     *
     * @return JVM spec.的字符串表示
     */
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();

        Platform.append(builder, "JavaVM Spec. Name:    ", getName());
        Platform.append(builder, "JavaVM Spec. Version: ", getVersion());
        Platform.append(builder, "JavaVM Spec. Vendor:  ", getVendor());

        return builder.toString();
    }

}
