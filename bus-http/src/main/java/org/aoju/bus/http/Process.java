/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
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
package org.aoju.bus.http;

/**
 * 进度（上传或下载）
 *
 * @author Kimi Liu
 * @version 6.0.5
 * @since JDK 1.8+
 */
public class Process {

    public static final int DEFAULT_STEP_BYTES = 8192;

    // 总字节数
    private long totalBytes;
    // 已经完成字节数
    private long doneBytes;

    public Process(long totalBytes, long doneBytes) {
        this.totalBytes = totalBytes;
        this.doneBytes = doneBytes;
    }

    public double getRate() {
        return (double) doneBytes / totalBytes;
    }


    public long getTotalBytes() {
        return totalBytes;
    }

    public long getDoneBytes() {
        return doneBytes;
    }

    public boolean isDone() {
        return doneBytes >= totalBytes;
    }

    public void addDoneBytes(long delt) {
        doneBytes += delt;
    }

    public void increaseDoneBytes() {
        doneBytes++;
    }

    public boolean notDoneOrReached(long bytes) {
        return doneBytes < bytes && doneBytes < totalBytes;
    }

}
