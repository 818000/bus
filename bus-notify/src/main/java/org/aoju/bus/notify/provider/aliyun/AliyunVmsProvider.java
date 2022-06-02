/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
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
package org.aoju.bus.notify.provider.aliyun;

import org.aoju.bus.core.lang.Fields;
import org.aoju.bus.core.lang.ZoneId;
import org.aoju.bus.core.toolkit.DateKit;
import org.aoju.bus.http.Httpx;
import org.aoju.bus.notify.Context;
import org.aoju.bus.notify.magic.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 阿里云语音通知
 *
 * @author Justubborn
 * @since Java 17+
 */
public class AliyunVmsProvider extends AliyunProvider<AliyunProperty, Context> {

    public AliyunVmsProvider(Context context) {
        super(context);
    }

    @Override
    public Message send(AliyunProperty entity) {
        Map<String, String> bodys = new HashMap<>();
        // 1. 系统参数
        bodys.put("SignatureMethod", "HMAC-SHA1");
        bodys.put("SignatureNonce", UUID.randomUUID().toString());
        bodys.put("AccessKeyId", context.getAppKey());
        bodys.put("SignatureVersion", "1.0");
        bodys.put("Timestamp", DateKit.format(new Date(), Fields.UTC_PATTERN, ZoneId.UTC.name()));
        bodys.put("Format", "JSON");

        // 2. 业务API参数
        bodys.put("Action", "SingleCallByTts");
        bodys.put("Version", "2017-05-25");
        bodys.put("RegionId", "cn-hangzhou");
        bodys.put("CalledNumber", entity.getReceive());
        bodys.put("CalledShowNumber", entity.getSender());
        bodys.put("PlayTimes", entity.getPlayTimes());
        bodys.put("TtsParam", entity.getParams());
        bodys.put("TtsCode", entity.getTemplate());
        bodys.put("Signature", getSign(bodys));

        Map<String, Object> map = new HashMap<>();
        for (String text : bodys.keySet()) {
            map.put(specialUrlEncode(text), specialUrlEncode(bodys.get(text)));
        }
        return checkResponse(Httpx.get(entity.getUrl(), map));
    }

}
