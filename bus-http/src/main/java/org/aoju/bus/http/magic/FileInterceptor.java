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
package org.aoju.bus.http.magic;

import org.aoju.bus.core.io.Buffer;
import org.aoju.bus.core.io.BufferSource;
import org.aoju.bus.core.io.DelegateSource;
import org.aoju.bus.core.io.Source;
import org.aoju.bus.core.lang.MediaType;
import org.aoju.bus.core.toolkit.IoKit;
import org.aoju.bus.http.Response;
import org.aoju.bus.http.bodys.ResponseBody;
import org.aoju.bus.http.metric.Interceptor;

import java.io.IOException;

/**
 * 请求参数-文件处理
 *
 * @author Kimi Liu
 * @version 6.1.0
 * @since JDK 1.8+
 */
public abstract class FileInterceptor implements Interceptor, ProgressListener {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response rsp = chain.proceed(chain.request());
        return rsp.newBuilder()
                .body(new DownloadFileProgressResponseBody(rsp.body(), this))
                .build();
    }

    public abstract void updateProgress(long downloadLenth, long totalLength, boolean isFinish);

    public static class DownloadFileProgressResponseBody extends ResponseBody {

        private final ResponseBody body;
        private final ProgressListener progressListener;
        private BufferSource bufferedSource;

        public DownloadFileProgressResponseBody(ResponseBody body, ProgressListener progressListener) {
            this.body = body;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return body.contentType();
        }

        @Override
        public long contentLength() {
            return body.contentLength();
        }

        @Override
        public BufferSource source() {
            if (bufferedSource == null) {
                bufferedSource = IoKit.buffer(source(body.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new DelegateSource(source) {
                long downloadLenth = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    boolean isFinish = (bytesRead == -1);
                    if (!isFinish) {
                        downloadLenth += bytesRead;
                    }
                    progressListener.updateProgress(downloadLenth, body.contentLength(), isFinish);
                    return bytesRead;
                }
            };
        }

    }

}
