/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.core.lang;

import java.awt.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举元素通用接口，在自定义枚举上实现此接口可以用于数据转换 数据库保存时建议保存 intVal()而非ordinal()防备需求变更
 *
 * @param <E> Enum类型
 * @author Kimi Liu
 * @since Java 17+
 */
public interface EnumValue<E extends EnumValue<E>> extends Enumers {

    /**
     * 操作类型
     */
    @Getter
    @AllArgsConstructor
    enum Action {

        /**
         * 新增
         */
        INSERT,
        /**
         * 删除
         */
        DELETE,
        /**
         * 修改
         */
        UPDATE,
        /**
         * 授权
         */
        SELECT,
        /**
         * 导入
         */
        IMPORT,
        /**
         * 导出
         */
        EXPORT,
        /**
         * 授权
         */
        GRANT,
        /**
         * 清空数据
         */
        CLEAN,
        /**
         * 其它
         */
        OTHER,
    }

    /**
     * 对齐方式枚举
     *
     * @author Kimi Liu
     * @since Java 17+
     */
    @Getter
    @AllArgsConstructor
    enum Align {
        /**
         * 左对齐
         */
        LEFT,
        /**
         * 右对齐
         */
        RIGHT,
        /**
         * 居中
         */
        CENTER
    }

    /**
     * 追加模式
     *
     */
    enum AppendMode {

        /**
         * 追加至首位
         */
        FIRST,

        /**
         * 追加至末尾
         */
        LAST

    }

    /**
     * 比较模式
     */
    @Getter
    @AllArgsConstructor
    enum Compare {

        /**
         * 等于（equal to）
         */
        EQ("="),
        /**
         * 不等于（not equal to）
         */
        NE("!="),
        /**
         * 小于（less than）
         */
        LT("<"),
        /**
         * 小于等于（less than or equal to）
         */
        LE("<="),
        /**
         * 大于（greater than）
         */
        GT(">"),
        /**
         * 大于等于（greater than or equal to）
         */
        GE(">="),

        LIKE("LIKE");

        /**
         * 代码值
         */
        String code;

    }

    /**
     * 节日类型
     */
    @Getter
    @AllArgsConstructor
    enum Festival {

        DAY(0, "日期"), TERM(1, "节气"), EVE(2, "除夕");

        /**
         * 代码
         */
        private final int code;

        /**
         * 名称
         */
        private final String name;

        public static Festival fromCode(Integer code) {
            if (null == code) {
                return null;
            }
            for (Festival item : values()) {
                if (item.getCode() == code) {
                    return item;
                }
            }
            return null;
        }

        public static Festival fromName(String name) {
            if (null == name) {
                return null;
            }
            for (Festival item : values()) {
                if (item.getName().equals(name)) {
                    return item;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return getName();
        }

    }

    /**
     * FTP连接模式 见：https://www.cnblogs.com/huhaoshida/p/5412615.html
     *
     * @author Kimi Liu
     * @since Java 17+
     */
    @Getter
    @AllArgsConstructor
    enum FtpMode {

        /**
         * 主动模式
         */
        Active,
        /**
         * 被动模式
         */
        Passive
    }

    /**
     * 渐变方向
     */
    @Getter
    @AllArgsConstructor
    enum Gradient {
        /**
         * 上到下
         */
        TOP_BOTTOM,
        /**
         * 左到右
         */
        LEFT_RIGHT,
        /**
         * 左上到右下
         */
        LEFT_TOP_TO_RIGHT_BOTTOM,
        /**
         * 右上到左下
         */
        RIGHT_TOP_TO_LEFT_BOTTOM
    }

    /**
     * 脱敏类型
     */
    @Getter
    @AllArgsConstructor
    enum Masking {
        /**
         * 完全脱敏
         */
        FULL,

        /**
         * 部分脱敏
         */
        PARTIAL,

        /**
         * 替换脱敏
         */
        REPLACE,
        /**
         * 用户id
         */
        USER_ID,
        /**
         * 中文名
         */
        CHINESE_NAME,
        /**
         * 身份证号
         */
        ID_CARD,
        /**
         * 座机号
         */
        FIXED_PHONE,
        /**
         * 手机号
         */
        MOBILE_PHONE,
        /**
         * 地址
         */
        ADDRESS,
        /**
         * 电子邮件
         */
        EMAIL,
        /**
         * 密码
         */
        PASSWORD,
        /**
         * 中国大陆车牌，包含普通车辆、新能源车辆
         */
        CAR_LICENSE,
        /**
         * 银行卡
         */
        BANK_CARD,
        /**
         * IPv4地址
         */
        IPV4,
        /**
         * IPv6地址
         */
        IPV6,
        /**
         * 定义了一个first_mask的规则，只显示第一个字符。
         */
        FIRST_MASK,
        /**
         * 清空为null
         */
        CLEAR_TO_NULL,
        /**
         * 清空为""
         */
        CLEAR_TO_EMPTY
    }

    /**
     * 修饰符
     */
    @Getter
    @AllArgsConstructor
    enum Modifier {

        /**
         * public修饰符，所有类都能访问
         */
        PUBLIC(java.lang.reflect.Modifier.PUBLIC),
        /**
         * private修饰符，只能被自己访问和修改
         */
        PRIVATE(java.lang.reflect.Modifier.PRIVATE),
        /**
         * protected修饰符，自身、子类及同一个包中类可以访问
         */
        PROTECTED(java.lang.reflect.Modifier.PROTECTED),
        /**
         * static修饰符，（静态修饰符）指定变量被所有对象共享，即所有实例都可以使用该变量。变量属于这个类
         */
        STATIC(java.lang.reflect.Modifier.STATIC),
        /**
         * final修饰符，最终修饰符，指定此变量的值不能变，使用在方法上表示不能被重载
         */
        FINAL(java.lang.reflect.Modifier.FINAL),
        /**
         * synchronized，同步修饰符，在多个线程中，该修饰符用于在运行前，对他所属的方法加锁，以防止其他线程的访问，运行结束后解锁。
         */
        SYNCHRONIZED(java.lang.reflect.Modifier.SYNCHRONIZED),
        /**
         * （易失修饰符）指定该变量可以同时被几个线程控制和修改
         */
        VOLATILE(java.lang.reflect.Modifier.VOLATILE),
        /**
         * （过度修饰符）指定该变量是系统保留，暂无特别作用的临时性变量，序列化时忽略
         */
        TRANSIENT(java.lang.reflect.Modifier.TRANSIENT),
        /**
         * native，本地修饰符。指定此方法的方法体是用其他语言在程序外部编写的。
         */
        NATIVE(java.lang.reflect.Modifier.NATIVE),

        /**
         * abstract，将一个类声明为抽象类，没有实现的方法，需要子类提供方法实现。
         */
        ABSTRACT(java.lang.reflect.Modifier.ABSTRACT),
        /**
         * strictfp，一旦使用了关键字strictfp来声明某个类、接口或者方法时，那么在这个关键字所声明的范围内所有浮点运算都是精确的，符合IEEE-754规范的。
         */
        STRICT(java.lang.reflect.Modifier.STRICT);

        /**
         * 修饰符枚举对应的int修饰符值
         */
        private final int code;

        /**
         * 多个修饰符做“或”操作，表示存在任意一个修饰符
         *
         * @param modifierTypes 修饰符列表，元素不能为空
         * @return “或”之后的修饰符
         */
        public static int orToInt(final Modifier... modifierTypes) {
            int modifier = modifierTypes[0].getCode();
            for (int i = 1; i < modifierTypes.length; i++) {
                modifier |= modifierTypes[i].getCode();
            }
            return modifier;
        }

        /**
         * 多个修饰符做“或”操作，表示存在任意一个修饰符
         *
         * @param modifierTypes 修饰符列表，元素不能为空
         * @return “或”之后的修饰符
         */
        public static int orToInt(final int... modifierTypes) {
            int modifier = modifierTypes[0];
            for (int i = 1; i < modifierTypes.length; i++) {
                modifier |= modifierTypes[i];
            }
            return modifier;
        }

    }

    /**
     * 命名模式
     */
    @Getter
    @AllArgsConstructor
    enum Naming {

        /**
         * 默认/正常
         */
        NORMAL(0, "默认"),

        /**
         * 粗体或增加强度
         */
        BOLD(1, "粗体"),

        /**
         * 弱化（降低强度）
         */
        FAINT(2, "弱化"),
        /**
         * 斜体
         */
        ITALIC(3, "斜体"),
        /**
         * 转换为大写
         */
        UPPER_CASE(4, "大写"),
        /**
         * 转换为小写
         */
        LOWER_CASE(5, "小写"),
        /**
         * 驼峰形式
         */
        CAMEL(6, "驼峰"),
        /**
         * 驼峰转下划线大写形式
         */
        CAMEL_UNDERLINE_UPPER_CASE(7, "驼峰转下划线大写"),
        /**
         * 驼峰转下划线小写形式
         */
        CAMEL_UNDERLINE_LOWER_CASE(8, "驼峰转下划线小写");

        /**
         * 编码
         */
        private final long code;
        /**
         * 名称
         */
        private final String name;

    }

    /**
     * 参数来源枚举
     */
    enum Params {
        /**
         * 请求头
         */
        HEADER,
        /**
         * 请求参数（包括表单和 URL 参数）
         */
        PARAMETER,
        /**
         * JSON 请求体
         */
        JSON_BODY,
        /**
         * Cookie
         */
        COOKIE,
        /**
         * 路径变量
         */
        PATH_VARIABLE,
        /**
         * 文件上传参数
         */
        MULTIPART,
        /**
         * 线程上下文
         */
        CONTEXT,
        /**
         * 所有来源（按优先级：Header > Parameter > Path Variable > JSON Body > Cookie > Multipart > Context）
         */
        ALL
    }

    /**
     * 策略模式枚举
     */
    @Getter
    @AllArgsConstructor
    enum Povider {

        /**
         * 加解密
         */
        CRYPTO("CRYPTO"),
        /**
         * 验证码
         */
        CAPTCHA("CAPTCHA"),
        /**
         * 自然语言
         */
        NLP("NLP"),
        /**
         * 拼音
         */
        PINYIN("PINYIN"),
        /**
         * 模版
         */
        TEMPLATE("TEMPLATE"),
        /**
         * JSON
         */
        JSON("JSON"),
        /**
         * 日志
         */
        LOGGING("LOGGING"),
        /**
         * 热点/降级
         */
        LIMITER("LIMITER"),
        /**
         * 消息
         */
        NOTIFY("NOTIFY"),
        /**
         * 授权
         */
        AUTH("AUTH"),
        /**
         * 支付
         */
        PAY("PAY"),
        /**
         * 托名
         */
        SENSITIVE("SENSITIVE"),
        /**
         * 存储
         */
        STORAGE("STORAGE"),
        /**
         * 数据
         */
        VALIDATE("VALIDATE");

        /**
         * 代码值
         */
        String code;

    }

    /**
     * 排序方式
     */
    @Getter
    @AllArgsConstructor
    enum Probe {

        /**
         * 拒绝流量
         */
        REFUSE("refuse"),
        /**
         * 接受流量
         */
        ACCEPT("accept"),
        /**
         * 存活正常
         */
        CORRECT("correct"),
        /**
         * 存活异常
         */
        BROKEN("broken");

        private final String value;

    }

    /**
     * 排序方式
     */
    @Getter
    @AllArgsConstructor
    enum Sort {

        /**
         * 升序
         */
        ASC("ASC"),
        /**
         * 降序
         */
        DESC("DESC");

        /**
         * 代码值
         */
        String code;

    }

    /**
     * 开关枚举
     */
    enum Switch {
        /**
         * 开启状态
         */
        ON,

        /**
         * 关闭状态
         */
        OFF
    }

    /**
     * 图片缩略
     */
    @Getter
    @AllArgsConstructor
    enum Thumb {

        /**
         * 默认
         */
        DEFAULT(Image.SCALE_DEFAULT),
        /**
         * 快速
         */
        FAST(Image.SCALE_FAST),
        /**
         * 平滑
         */
        SMOOTH(Image.SCALE_SMOOTH),
        /**
         * 使用 ReplicateScaleFilter 类中包含的图像缩放算法
         */
        REPLICATE(Image.SCALE_REPLICATE),
        /**
         * Area Averaging算法
         */
        AREA_AVERAGING(Image.SCALE_AREA_AVERAGING);

        private final int code;

    }

    /**
     * 图片缩略模式
     */
    @Getter
    @AllArgsConstructor
    enum Zoom {
        /**
         * 原始比例，不缩放
         */
        ORIGIN,
        /**
         * 指定宽度，高度按比例
         */
        WIDTH,
        /**
         * 指定高度，宽度按比例
         */
        HEIGHT,
        /**
         * 自定义高度和宽度，强制缩放
         */
        OPTIONAL
    }

}
