/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2023 aoju.org and other contributors.                      *
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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.storage.provider;

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.lang.MediaType;
import org.aoju.bus.core.toolkit.IoKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.storage.Builder;
import org.aoju.bus.storage.Context;
import org.aoju.bus.storage.magic.Message;
import org.aoju.bus.storage.magic.Property;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存储服务-MinIO
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class MinioOssProvider extends AbstractProvider {

    private final S3Client client;

    public MinioOssProvider(Context context) {
        this.context = context;
        Assert.notBlank(this.context.getPrefix(), "[prefix] not defined");
        Assert.notBlank(this.context.getEndpoint(), "[endpoint] not defined");
        Assert.notBlank(this.context.getBucket(), "[bucket] not defined");
        Assert.notBlank(this.context.getAccessKey(), "[accessKey] not defined");
        Assert.notBlank(this.context.getSecretKey(), "[secretKey] not defined");
        Assert.notNull(this.context.isSecure(), "[secure] not defined");

        this.client = S3Client.builder()
                .endpointOverride(URI.create(this.context.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(this.context.getAccessKey(), this.context.getSecretKey())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .region(Region.US_EAST_1)
                .build();
    }

    @Override
    public Message download(String fileName) {
        return download(this.context.getBucket(), fileName);
    }

    @Override
    public Message download(String bucket, String fileName) {
        try {
            InputStream inputStream = this.client.getObject(
                    GetObjectRequest.builder().bucket(bucket).key(fileName).build());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            return Message.builder()
                    .errcode(Builder.ErrorCode.SUCCESS.getCode())
                    .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                    .data(bufferedReader)
                    .build();
        } catch (Exception e) {
            Logger.error("file download failed", e.getMessage());
        }
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message download(String bucket, String fileName, File file) {
        Logger.debug("下载{}-{}", bucket, fileName);
        try {
            InputStream inputStream = this.client.getObject(
                    GetObjectRequest.builder().bucket(bucket).key(fileName).build());
            OutputStream outputStream = new FileOutputStream(file);
            IoKit.copy(inputStream, outputStream);
            return Message.builder()
                    .errcode(Builder.ErrorCode.SUCCESS.getCode())
                    .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                    .build();
        } catch (Exception e) {
            Logger.error(e, "file download failed,{}", e.getMessage());
        }
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message download(String fileName, File file) {
        return download(this.context.getBucket(), fileName, file);
    }

    @Override
    public Message list() {
        ListObjectsV2Response listResponse = this.client.listObjectsV2(
                ListObjectsV2Request.builder().bucket(this.context.getBucket()).build());
        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                .data(listResponse.contents().stream().map(item -> {
                    Property storageItem = new Property();
                    storageItem.setName(item.key());
                    storageItem.setSize(StringKit.toString(item.size()));
                    Map<String, Object> extend = new HashMap<>();
                    extend.put("tag", item.eTag());
                    extend.put("storageClass", item.storageClassAsString());
                    extend.put("lastModified", item.lastModified());
                    storageItem.setExtend(extend);
                    return storageItem;
                }).collect(Collectors.toList()))
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
    public Message upload(String bucket, byte[] content) {
        InputStream stream = new ByteArrayInputStream(content);
        return upload(this.context.getBucket(), bucket, stream);
    }

    @Override
    public Message upload(String bucket, String fileName, InputStream content) {
        Logger.debug("上传{}-{}", bucket, fileName);
        try {
            String contentType = getContentType(fileName);
            this.client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(content, content.available()));
            return Message.builder()
                    .errcode(Builder.ErrorCode.SUCCESS.getCode())
                    .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                    .data(Property.builder()
                            .name(fileName)
                            .path(this.context.getPrefix() + fileName))
                    .build();
        } catch (Exception e) {
            Logger.error(e, "file upload failed{}", e.getMessage());
        }
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message upload(String bucket, String fileName, byte[] content) {
        return upload(bucket, fileName, new ByteArrayInputStream(content));
    }

    @Override
    public Message remove(String fileName) {
        return remove(this.context.getBucket(), fileName);
    }

    @Override
    public Message remove(String bucket, String fileName) {
        try {
            this.client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .build());
            return Message.builder()
                    .errcode(Builder.ErrorCode.SUCCESS.getCode())
                    .errmsg(Builder.ErrorCode.SUCCESS.getMsg())
                    .build();
        } catch (Exception e) {
            Logger.error(e, "file remove failed {}", e.getMessage());
        }
        return Message.builder()
                .errcode(Builder.ErrorCode.FAILURE.getCode())
                .errmsg(Builder.ErrorCode.FAILURE.getMsg())
                .build();
    }

    @Override
    public Message remove(String bucket, Path path) {
        return remove(bucket, path.toString());
    }

    /**
     * 根据文件扩展名获取 MIME 类型
     *
     * @param fileName 文件名称
     * @return MIME 类型
     */
    private String getContentType(String fileName) {
        if (fileName == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        String extension = fileName.substring(lastDot + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "txt" -> MediaType.TEXT_PLAIN;
            case "html" -> MediaType.TEXT_HTML;
            case "json" -> MediaType.APPLICATION_JSON;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
