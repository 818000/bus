/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
/**
 * bus.mapper
 * 
 * @author Kimi Liu
 * @since Java 17+
 */
module bus.mapper {

    requires java.desktop;

    requires bus.core;
    requires bus.logger;

    requires lombok;
    requires jakarta.persistence;
    requires org.mybatis;
    requires org.mybatis.spring;

    exports org.miaixz.bus.mapper;
    exports org.miaixz.bus.mapper.annotation;
    exports org.miaixz.bus.mapper.binding;
    exports org.miaixz.bus.mapper.builder;
    exports org.miaixz.bus.mapper.criteria;
    exports org.miaixz.bus.mapper.handler;
    exports org.miaixz.bus.mapper.parsing;
    exports org.miaixz.bus.mapper.provider;
    exports org.miaixz.bus.mapper.support;
    exports org.miaixz.bus.mapper.binding.basic;
    exports org.miaixz.bus.mapper.binding.condition;
    exports org.miaixz.bus.mapper.binding.cursor;
    exports org.miaixz.bus.mapper.binding.function;
    exports org.miaixz.bus.mapper.binding.list;
    exports org.miaixz.bus.mapper.binding.logical;
    exports org.miaixz.bus.mapper.support.keysql;

}
