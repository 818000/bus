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
package org.miaixz.bus.vortex;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * api definition
 *
 * @author Justubborn
 * @since Java 17+
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class Assets {

    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 服务器地址
     */
    private String host;
    /**
     * 上下文路径
     */
    private String path;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 方法URL
     */
    private String url;
    /**
     * 方法
     */
    private String method;
    /**
     * 路由模式 1.HTTP 2.MQ 3.MCP
     */
    private Integer mode;
    /**
     * 请求类型 1.GET 2.POST 3.HEAD 4.PUT 5.PATCH 6.DELETE 7.OPTIONS 8.TRACE
     */
    private Integer type;
    /**
     * 授权 0.不做校验 1.校验
     */
    private Integer token;
    /**
     * 签名
     *
     */
    private Integer sign;
    /**
     * 例外规则
     */
    private Integer firewall;
    /**
     * 版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;

    /**
     * 超时时间
     */
    private long timeout = 10000;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (null == o || getClass() != o.getClass())
            return false;
        Assets assets = (Assets) o;
        return id.equals(assets.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
