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

import org.aoju.bus.image.Status;
import org.aoju.bus.image.Tag;
import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.galaxy.data.VR;
import org.aoju.bus.image.metric.Association;
import org.aoju.bus.image.metric.Commands;
import org.aoju.bus.image.metric.ImageException;
import org.aoju.bus.image.metric.internal.pdu.Presentation;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * @author Kimi Liu
 * @version 5.8.9
 * @since JDK 1.8+
 */
public class BasicQuery implements Query {

    protected final Association as;
    protected final Presentation pc;
    protected final Attributes rq;
    protected final Attributes keys;
    protected volatile boolean canceled;
    protected boolean optionalKeysNotSupported = false;

    public BasicQuery(Association as, Presentation pc,
                      Attributes rq, Attributes keys) {
        this.as = as;
        this.pc = pc;
        this.rq = rq;
        this.keys = keys;
    }

    public boolean isOptionalKeysNotSupported() {
        return optionalKeysNotSupported;
    }

    public void setOptionalKeysNotSupported(boolean optionalKeysNotSupported) {
        this.optionalKeysNotSupported = optionalKeysNotSupported;
    }

    @Override
    public void onCancelRQ(Association as) {
        canceled = true;
    }

    @Override
    public void run() {
        try {
            int msgId = rq.getInt(Tag.MessageID, -1);
            as.addCancelRQHandler(msgId, this);
            try {
                while (!canceled && hasMoreMatches()) {
                    Attributes match = adjust(nextMatch());
                    if (match != null) {
                        int status = optionalKeysNotSupported
                                ? Status.PendingWarning
                                : Status.Pending;
                        as.writeDimseRSP(pc, Commands.mkCFindRSP(rq, status), match);
                    }
                }
                int status = canceled ? Status.Cancel : Status.Success;
                as.writeDimseRSP(pc, Commands.mkCFindRSP(rq, status));
            } catch (ImageException e) {
                Attributes rsp = e.mkRSP(0x8020, msgId);
                as.writeDimseRSP(pc, rsp, e.getDataset());
            } finally {
                as.removeCancelRQHandler(msgId);
                close();
            }
        } catch (IOException e) {
            // handled by Association
        }
    }

    protected void close() {
    }

    protected Attributes nextMatch() throws ImageException {
        throw new NoSuchElementException();
    }

    protected boolean hasMoreMatches() throws ImageException {
        return false;
    }

    protected Attributes adjust(Attributes match) {
        if (match == null)
            return null;

        Attributes filtered = new Attributes(match.size());
        // include SpecificCharacterSet also if not in keys
        if (!keys.contains(Tag.SpecificCharacterSet)) {
            String[] ss = match.getStrings(Tag.SpecificCharacterSet);
            if (ss != null)
                filtered.setString(Tag.SpecificCharacterSet, VR.CS, ss);
        }
        filtered.addSelected(match, keys);
        return filtered;
    }

}
