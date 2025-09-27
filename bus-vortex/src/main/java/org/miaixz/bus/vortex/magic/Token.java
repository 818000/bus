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
package org.miaixz.bus.vortex.magic;

import lombok.experimental.SuperBuilder;
import org.miaixz.bus.vortex.Assets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents an authentication token and its associated context.
 * This class encapsulates all necessary information for authorizing and processing a request,
 * including the token string, the source channel, related resource configurations,
 * and transient runtime data like tenant ID and API key.
 *
 * @author Justubborn
 * @since Java 17+
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class Token {

    /**
     * The unique authentication token string. This is the primary credential used
     * to identify and authenticate a user, session, or service. It is a required,
     * immutable field.
     */
    public final String token;

    /**
     * An integer identifier for the source channel of the request.
     * This can be used to differentiate between various clients or platforms,
     * such as a mobile app, web application, or third-party integrations
     * (e.g., WeChat, DingTalk). It is a required, immutable field.
     */
    public final int channel;

    /**
     * Contains configuration and information about the resources or "assets"
     * associated with this token. This could include permissions, rate limits,
     * or other service-specific settings. It is a required, immutable field.
     */
    public final Assets assets;

    /**
     * The identifier for the tenant in a multi-tenant architecture.
     * This field is marked as {@code transient} to prevent it from being included
     * in serialization, as it's typically used for runtime request routing and
     * context, not for persistent state.
     */
    public transient String tenant_id;

    /**
     * An API key that may be used for specific API requests, often for
     * server-to-server communication or as a supplementary authentication method.
     * Like {@code tenant_id}, it is marked as {@code transient} and is not
     * intended for serialization.
     */
    public transient String api_key;

}