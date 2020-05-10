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
package org.aoju.bus.tracer.binding.spring.boot;

import org.aoju.bus.tracer.config.TraceFilterConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Kimi Liu
 * @version 5.8.9
 * @since JDK 1.8+
 */
@ConfigurationProperties(prefix = "tracer")
public class TraceProperties {

    private int sessionIdLength = 32;

    private int invocationIdLength = 32;

    private Map<TraceFilterConfiguration.Channel, Pattern> filter = new HashMap<>();

    private Map<String, Profile> profile = new HashMap<>();
    private TraceFilterConfiguration delegate = new TraceFilterConfiguration() {
        @Override
        public boolean shouldProcessParam(String paramName, Channel channel) {
            return true;
        }

        @Override
        public Map<String, String> filterDeniedParams(Map<String, String> unfiltered, Channel channel) {
            return unfiltered;
        }

        @Override
        public boolean shouldProcessContext(Channel channel) {
            return true;
        }

        @Override
        public boolean shouldGenerateInvocationId() {
            return invocationIdLength > 0;
        }

        @Override
        public int generatedInvocationIdLength() {
            return invocationIdLength;
        }

        @Override
        public boolean shouldGenerateSessionId() {
            return sessionIdLength > 0;
        }

        @Override
        public int generatedSessionIdLength() {
            return sessionIdLength;
        }
    };

    public int getSessionIdLength() {
        return sessionIdLength;
    }

    public void setSessionIdLength(int sessionIdLength) {
        this.sessionIdLength = sessionIdLength;
    }

    public int getInvocationIdLength() {
        return invocationIdLength;
    }

    public void setInvocationIdLength(int invocationIdLength) {
        this.invocationIdLength = invocationIdLength;
    }

    public Map<TraceFilterConfiguration.Channel, Pattern> getFilter() {
        return filter;
    }

    public void setFilter(Map<TraceFilterConfiguration.Channel, Pattern> filter) {
        this.filter = filter;
    }

    public Map<String, Profile> getProfile() {
        return profile;
    }

    public void setProfile(Map<String, Profile> profile) {
        this.profile = profile;
    }

    public TraceFilterConfiguration getAsFilterConfiguration() {
        return delegate;
    }


    public static class Profile {

        private Map<TraceFilterConfiguration.Channel, Pattern> filter;

        public Map<TraceFilterConfiguration.Channel, Pattern> getFilter() {
            return filter;
        }

        public void setFilter(Map<TraceFilterConfiguration.Channel, Pattern> filter) {
            this.filter = filter;
        }
    }

}
