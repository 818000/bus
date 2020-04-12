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
package org.aoju.bus.tracer.binding.quartz;

import org.aoju.bus.tracer.Backend;
import org.aoju.bus.tracer.Builder;
import org.aoju.bus.tracer.config.TraceFilterConfiguration;
import org.aoju.bus.tracer.consts.TraceConsts;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * @author Kimi Liu
 * @version 5.8.3
 * @since JDK 1.8+
 */
public class TraceContextInjector {

    private final Backend backend;

    private final String profile;

    public TraceContextInjector() {
        this(Builder.getBackend(), TraceConsts.DEFAULT);
    }

    public TraceContextInjector(final String profile) {
        this(Builder.getBackend(), profile);
    }

    TraceContextInjector(final Backend backend, final String profile) {
        this.backend = backend;
        this.profile = profile;
    }

    public void injectContext(Trigger trigger) {
        injectContext(trigger.getJobDataMap());
    }

    public void injectContext(JobDetail jobDetail) {
        injectContext(jobDetail.getJobDataMap());
    }

    public void injectContext(JobDataMap jobDataMap) {
        final TraceFilterConfiguration configuration = backend.getConfiguration(profile);
        if (!backend.isEmpty() && configuration.shouldProcessContext(TraceFilterConfiguration.Channel.AsyncDispatch)) {
            jobDataMap.put(TraceConsts.TPIC_HEADER,
                    backend.getConfiguration(profile).filterDeniedParams(backend.copyToMap(),
                            TraceFilterConfiguration.Channel.AsyncDispatch));
        }
    }

}
