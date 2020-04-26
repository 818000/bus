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
package org.aoju.bus.image.metric.service;

import org.aoju.bus.image.Dimse;
import org.aoju.bus.image.Status;
import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.metric.Association;
import org.aoju.bus.image.metric.Commands;
import org.aoju.bus.image.metric.internal.pdu.PresentationContext;
import org.aoju.bus.image.metric.internal.pdv.PDVInputStream;

import java.io.IOException;

/**
 * @author Kimi Liu
 * @version 5.8.8
 * @since JDK 1.8+
 */
public class BasicCStoreSCP extends AbstractService {

    public BasicCStoreSCP() {
        super("*");
    }

    public BasicCStoreSCP(String... sopClasses) {
        super(sopClasses);
    }

    @Override
    public void onDimse(Association as,
                        PresentationContext pc,
                        Dimse dimse,
                        Attributes rq,
                        PDVInputStream data) throws IOException {
        if (dimse != Dimse.C_STORE_RQ)
            throw new ServiceException(Status.UnrecognizedOperation);

        Attributes rsp = Commands.mkCStoreRSP(rq, Status.Success);
        store(as, pc, rq, data, rsp);
        as.tryWriteDimseRSP(pc, rsp);
    }

    protected void store(Association as, PresentationContext pc, Attributes rq,
                         PDVInputStream data, Attributes rsp) throws IOException {
    }

    @Override
    protected void onDimse(Association as,
                           PresentationContext pc,
                           Dimse dimse,
                           Attributes cmd,
                           Attributes data) {
        throw new UnsupportedOperationException();
    }

}
