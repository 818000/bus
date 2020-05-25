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
package org.aoju.bus.storage.provider;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.storage.Builder;
import org.aoju.bus.storage.Context;
import org.aoju.bus.storage.magic.Attachs;
import org.aoju.bus.storage.magic.Message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * 存储服务-腾讯云
 *
 * @author Kimi Liu
 * @version 5.9.3
 * @since JDK 1.8+
 */
public class TencentCosProvider extends AbstractProvider {

    private COSClient client;

    public TencentCosProvider(Context context) {
        this.context = context;
        Assert.notBlank(this.context.getPrefix(), "[prefix] not defined");
        Assert.notBlank(this.context.getBucket(), "[bucket] not defined");
        Assert.notBlank(this.context.getAccessKey(), "[accessKey] not defined");
        Assert.notBlank(this.context.getSecretKey(), "[secretKey] not defined");
        Assert.notBlank(this.context.getRegion(), "[region] not defined");

        this.client = new COSClient(
                new BasicCOSCredentials(this.context.getAccessKey(), this.context.getSecretKey()),
                new ClientConfig(new Region(this.context.getRegion()))
        );
    }

    @Override
    public Message download(String fileName) {
        return null;
    }

    @Override
    public Message download(String bucket, String fileName) {
        this.client.getObjectMetadata(bucket, fileName);
        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                .build();
    }

    @Override
    public Message download(String bucket, String fileName, File file) {
        this.client.getObject(new GetObjectRequest(bucket, fileName), file);
        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                .build();
    }

    @Override
    public Message download(String fileName, File file) {
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message list() {
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message rename(String oldName, String newName) {
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message rename(String bucket, String oldName, String newName) {
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message upload(String fileName, byte[] content) {
        return upload(this.context.getBucket(), fileName, content);
    }

    @Override
    public Message upload(String bucket, String fileName, InputStream content) {
        if (!fileName.startsWith(File.separator)) {
            fileName = File.separator + fileName;
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            objectMetadata.setContentLength(content.available());
            PutObjectRequest request = new PutObjectRequest(this.context.getBucket(), fileName, content, objectMetadata);
            PutObjectResult result = this.client.putObject(request);
            if (StringUtils.isEmpty(result.getETag())) {
                return Message.builder()
                        .errcode(Builder.ErrorCode.FAILURE.getCode())
                        .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                        .build();
            }
            return Message.builder()
                    .errcode(Builder.ErrorCode.SUCCESS.getCode())
                    .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                    .data(Attachs.builder()
                            .path(this.context.getPrefix() + fileName)
                            .name(fileName))
                    .build();

        } catch (IOException e) {
            Logger.error("file upload failed", e.getMessage());
        }
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message upload(String bucket, String fileName, byte[] content) {
        if (!fileName.startsWith(File.separator)) {
            fileName = File.separator + fileName;
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 设置输入流长度为 500
        objectMetadata.setContentLength(content.length);
        PutObjectRequest request = new PutObjectRequest(this.context.getBucket(), fileName,
                new ByteArrayInputStream(content), objectMetadata);
        PutObjectResult result = this.client.putObject(request);
        if (StringUtils.isEmpty(result.getETag())) {
            return Message.builder()
                    .errcode(Builder.ErrorCode.FAILURE.getCode())
                    .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                    .build();
        }
        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                .data(Attachs.builder()
                        .name(fileName)
                        .path(this.context.getPrefix() + fileName))
                .build();
    }

    @Override
    public Message remove(String fileName) {
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message remove(String bucket, String fileName) {
        this.client.deleteObject(bucket, fileName);
        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                .data(Attachs.builder()
                        .name(fileName)
                        .path(this.context.getPrefix() + fileName))
                .build();
    }

    @Override
    public Message remove(String bucket, Path path) {
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

}
