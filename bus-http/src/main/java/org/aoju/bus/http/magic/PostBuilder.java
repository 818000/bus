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
package org.aoju.bus.http.magic;

import org.aoju.bus.http.Httpd;
import org.aoju.bus.http.bodys.MultipartBody;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * POST参数构造器
 *
 * @author Kimi Liu
 * @version 5.9.0
 * @since JDK 1.8+
 */
public class PostBuilder extends RequestBuilder<PostBuilder> {

    private List<PostRequest.FileInfo> fileInfos;
    private String postBody;
    private MultipartBody multipartBody;

    public PostBuilder(Httpd httpd) {
        super(httpd);
        fileInfos = new ArrayList<>();
    }

    @Override
    public RequestCall build() {
        return new PostRequest(
                url,
                tag,
                params,
                encodedParams,
                headers,
                fileInfos,
                postBody,
                multipartBody,
                id).
                build(httpd);
    }

    public PostBuilder body(String postBody) {
        this.postBody = postBody;
        return this;
    }

    public PostBuilder multipartBody(MultipartBody multipartBody) {
        this.multipartBody = multipartBody;
        return this;
    }

    public PostBuilder addFile(String partName, String fileName, byte[] content) {
        PostRequest.FileInfo fileInfo = new PostRequest.FileInfo();
        fileInfo.partName = partName;
        fileInfo.fileName = fileName;
        fileInfo.fileContent = content;
        fileInfos.add(fileInfo);
        return this;
    }

    public PostBuilder addFile(String partName, String fileName, InputStream is) {
        PostRequest.FileInfo fileInfo = new PostRequest.FileInfo();
        fileInfo.partName = partName;
        fileInfo.fileName = fileName;
        fileInfo.fileInputStream = is;
        fileInfos.add(fileInfo);
        return this;
    }

    public PostBuilder addFile(String partName, String fileName, File file) {
        PostRequest.FileInfo fileInfo = new PostRequest.FileInfo();
        fileInfo.partName = partName;
        fileInfo.fileName = fileName;
        fileInfo.file = file;
        fileInfos.add(fileInfo);
        return this;
    }

    public PostBuilder addFile(String partName, String fileName, String content)
            throws UnsupportedEncodingException {
        return addFile(partName, fileName, content, StandardCharsets.UTF_8.toString());
    }

    public PostBuilder addFile(String partName, String fileName, String content, String charsetName)
            throws UnsupportedEncodingException {
        return addFile(partName, fileName, content.getBytes(charsetName));
    }

    public PostBuilder addFile(String partName, String fileName, byte[] content, String charsetName) {
        return addFile(partName, fileName, content);
    }

}
