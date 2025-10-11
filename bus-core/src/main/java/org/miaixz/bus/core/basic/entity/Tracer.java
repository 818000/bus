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
package org.miaixz.bus.core.basic.entity;

import java.io.Serial;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents tracing information for a request, extending authorization details.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Tracer extends Authorize {

    @Serial
    private static final long serialVersionUID = 2852291120377L;

    /**
     * The unique ID for the current request.
     */
    @Transient
    protected String x_request_id;
    /**
     * The main trace ID for the entire request chain.
     */
    @Transient
    protected String x_trace_id;

    /**
     * The ID of the calling service or component.
     */
    @Transient
    protected String x_span_id;

    /**
     * The ID of the service or component being called.
     */
    @Transient
    protected String x_child_id;

    /**
     * The local IP address of the service.
     */
    @Transient
    protected String x_local_ip;

    /**
     * The remote IP address of the client making the request.
     */
    @Transient
    protected String x_remote_ip;

    /**
     * The channel type of the requester. e.g., 1-WEB, 2-APP, 3-DingTalk, 4-WeChat Mini Program, 5-Other.
     */
    @Transient
    protected String x_remote_channel;

    /**
     * The terminal type of the requester. e.g., 1-PC, 2-Android, 3-iPhone, 4-iPad, 5-WinPhone, 6-HarmonyOS, 7-Other.
     */
    @Transient
    protected String x_remote_terminal;

    /**
     * The browser information of the requester. For native apps, this should be the operating system version.
     */
    @Transient
    protected String x_remote_browser;

}
