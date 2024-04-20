/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2024 miaixz.org and other contributors.                    *
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
package org.miaixz.bus.extra.emoji;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiParser.FitzpatrickAction;

import java.util.List;
import java.util.Set;

/**
 * 基于emoji-java的Emoji表情工具类
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class EmojiKit {

    /**
     * 是否为Emoji表情的Unicode符
     *
     * @param text 被测试的字符串
     * @return 是否为Emoji表情的Unicode符
     */
    public static boolean isEmoji(String text) {
        return EmojiManager.isEmoji(text);
    }

    /**
     * 通过tag方式获取对应的所有Emoji表情
     *
     * @param tag tag标签,例如“happy”
     * @return Emoji表情集合, 如果找不到返回null
     */
    public static Set<Emoji> getByTag(String tag) {
        return EmojiManager.getForTag(tag);
    }

    /**
     * 通过别名获取Emoji
     *
     * @param alias 别名,例如“smile”
     * @return Emoji对象, 如果找不到返回null
     */
    public static Emoji get(String alias) {
        return EmojiManager.getForAlias(alias);
    }

    /**
     * 将子串中的Emoji别名和其HTML表示形式替换为为Unicode Emoji符号
     * <p>
     * 例如：
     *
     * <pre>
     *  <code>:smile:</code>  替换为 <code>😄</code>
     * <code>&amp;#128516;</code> 替换为 <code>😄</code>
     * <code>:boy|type_6:</code> 替换为 <code>👦🏿</code>
     * </pre>
     *
     * @param text 包含Emoji别名或者HTML表现形式的字符串
     * @return 替换后的字符串
     */
    public static String toUnicode(String text) {
        return EmojiParser.parseToUnicode(text);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为别名表现形式
     * <p>
     * 例如： <code>😄</code> 转换为 <code>:smile:</code>
     *
     * <p>
     * {@link FitzpatrickAction}参数被设置为{@link FitzpatrickAction#PARSE},则别名后会追加fitzpatrick类型
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy|type_6:</code>
     *
     * <p>
     * {@link FitzpatrickAction}参数被设置为{@link FitzpatrickAction#REMOVE},则别名后的"|"和类型将被去除
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy:</code>
     *
     * <p>
     * {@link FitzpatrickAction}参数被设置为{@link FitzpatrickAction#IGNORE},则别名后的类型将被忽略
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy:🏿</code>
     *
     * @param text 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    public static String toAlias(String text) {
        return toAlias(text, FitzpatrickAction.PARSE);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为别名表现形式,别名后会增加"|"并追加fitzpatrick类型
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy|type_6:</code>
     *
     * @param text              包含Emoji Unicode字符的字符串
     * @param fitzpatrickAction 修饰符
     * @return 替换后的字符串
     */
    public static String toAlias(String text, FitzpatrickAction fitzpatrickAction) {
        return EmojiParser.parseToAliases(text, fitzpatrickAction);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为HTML 16进制表现形式
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>&amp;#x1f466;</code>
     *
     * @param text 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    public static String toHtmlHex(String text) {
        return toHtml(text, true);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为HTML表现形式（Hex方式）
     * 例如：<code>👦🏿</code> 转换为 <code>&amp;#x1f466;</code>
     *
     * @param text 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    public static String toHtml(String text) {
        return toHtml(text, true);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为HTML表现形式，例如：
     * <pre>
     * 如果为hex形式，<code>👦🏿</code> 转换为 <code>&amp;#x1f466;</code>
     * 否则，<code>👦🏿</code> 转换为 <code>&amp;#128102;</code>
     * </pre>
     *
     * @param text  包含Emoji Unicode字符的字符串
     * @param isHex 是否hex形式
     * @return 替换后的字符串
     */
    public static String toHtml(String text, boolean isHex) {
        return isHex ? EmojiParser.parseToHtmlHexadecimal(text) :
                EmojiParser.parseToHtmlDecimal(text);
    }

    /**
     * 去除字符串中所有的Emoji Unicode字符
     *
     * @param text 包含Emoji字符的字符串
     * @return 替换后的字符串
     */
    public static String removeAllEmojis(String text) {
        return EmojiParser.removeAllEmojis(text);
    }

    /**
     * 提取字符串中所有的Emoji Unicode
     *
     * @param text 包含Emoji字符的字符串
     * @return Emoji字符列表
     */
    public static List<String> extractEmojis(String text) {
        return EmojiParser.extractEmojis(text);
    }

}
