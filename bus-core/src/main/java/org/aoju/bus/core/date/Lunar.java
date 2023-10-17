/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org 6tail and other contributors.                *
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
package org.aoju.bus.core.date;

import org.aoju.bus.core.lang.Fields;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.BooleanKit;
import org.aoju.bus.core.toolkit.ObjectKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.util.*;

/**
 * 农历日期
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Lunar {

    /**
     * 月份地支偏移量，因正月起寅
     */
    public static final int BASE_MONTH_ZHI_INDEX = 2;
    /**
     * 旬
     */
    public static final String[] XUN = {
            "甲子", "甲戌", "甲申", "甲午", "甲辰", "甲寅"
    };
    /**
     * 旬空
     */
    public static final String[] XUN_KONG = {
            "戌亥", "申酉", "午未", "辰巳", "寅卯", "子丑"
    };
    /**
     * 六曜
     */
    public static final String[] LIU_YAO = {
            "先胜", "友引", "先负", "佛灭", "大安", "赤口"
    };
    /**
     * 候
     */
    public static final String[] HOU = {
            "初候", "二候", "三候"
    };
    /**
     * 物候
     */
    public static final String[] WU_HOU = {
            "蚯蚓结", "麋角解", "水泉动", "雁北乡", "鹊始巢", "雉始雊", "鸡始乳", "征鸟厉疾",
            "水泽腹坚", "东风解冻", "蛰虫始振", "鱼陟负冰", "獭祭鱼", "候雁北", "草木萌动",
            "桃始华", "仓庚鸣", "鹰化为鸠", "玄鸟至", "雷乃发声", "始电", "桐始华", "田鼠化为鴽",
            "虹始见", "萍始生", "鸣鸠拂奇羽", "戴胜降于桑", "蝼蝈鸣", "蚯蚓出", "王瓜生",
            "苦菜秀", "靡草死", "麦秋至", "螳螂生", "鵙始鸣", "反舌无声", "鹿角解", "蜩始鸣",
            "半夏生", "温风至", "蟋蟀居壁", "鹰始挚", "腐草为萤", "土润溽暑", "大雨行时",
            "凉风至", "白露降", "寒蝉鸣", "鹰乃祭鸟", "天地始肃", "禾乃登", "鸿雁来", "玄鸟归",
            "群鸟养羞", "雷始收声", "蛰虫坯户", "水始涸", "鸿雁来宾", "雀入大水为蛤", "菊有黄花",
            "豺乃祭兽", "草木黄落", "蛰虫咸俯", "水始冰", "地始冻", "雉入大水为蜃", "虹藏不见",
            "天气上升地气下降", "闭塞而成冬", "鹖鴠不鸣", "虎始交", "荔挺出"
    };
    /**
     * 喜神方位，《喜神方位歌》：甲己在艮乙庚乾，丙辛坤位喜神安，丁壬只在离宫坐，戊癸原在在巽间。
     */
    public static final String[] POSITION_XI = {
            "艮", "乾", "坤", "离", "巽", "艮", "乾", "坤", "离", "巽"
    };
    /**
     * 阳贵方位，《阳贵神歌》：甲戊坤艮位，乙己是坤坎，庚辛居离艮，丙丁兑与乾，震巽属何日，壬癸贵神安。
     */
    public static final String[] POSITION_YANG_GUI = {
            "坤", "坤", "兑", "乾", "艮", "坎", "离", "艮", "震", "巽"
    };
    /**
     * 阴贵方位，《阴贵神歌》：甲戊见牛羊，乙己鼠猴乡，丙丁猪鸡位，壬癸蛇兔藏，庚辛逢虎马，此是贵神方。
     */
    public static final String[] POSITION_YIN_GUI = {
            "艮", "坎", "乾", "兑", "坤", "坤", "艮", "离", "巽", "震"
    };
    /**
     * 流派1《福神方位歌》：甲乙东南是福神，丙丁正东是堪宜，戊北己南庚辛坤，壬在乾方癸在西。
     */
    public static final String[] POSITION_FU = {
            "", "巽", "巽", "震", "震", "坎", "离", "坤", "坤", "乾", "兑"
    };
    /**
     * 流派2《福神方位歌》：甲己正北是福神，丙辛西北乾宫存，乙庚坤位戊癸艮，丁壬巽上好追寻。
     */
    public static final String[] POSITION_FU_2 = {
            "", "坎", "坤", "乾", "巽", "艮", "坎", "坤", "乾", "巽", "艮"
    };
    /**
     * 财神方位 ：甲乙东北是财神，丙丁向在西南寻，戊己正北坐方位，庚辛正东去安身，壬癸原来正南坐，便是财神方位真
     */
    public static final String[] POSITION_CAI = {
            "艮", "艮", "坤", "坤", "坎", "坎", "震", "震", "离", "离"
    };
    /**
     * 地支相冲（子午相冲，丑未相冲，寅申相冲，辰戌相冲，卯酉相冲，巳亥相冲），由于地支对应十二生肖，也就对应了生肖相冲
     */
    public static final String[] CHONG = {
            "午", "未", "申", "酉", "戌", "亥", "子", "丑", "寅", "卯", "辰", "巳"
    };
    /**
     * 天干相冲之无情之克（阳克阳，阴克阴）
     */
    public static final String[] CHONG_GAN = {
            "戊", "己", "庚", "辛", "壬", "癸", "甲", "乙", "丙", "丁"
    };
    /**
     * 天干相冲之有情之克（阳克阴，阴克阳）
     */
    public static final String[] CHONG_GAN_TIE = {
            "己", "戊", "辛", "庚", "癸", "壬", "乙", "甲", "丁", "丙"
    };
    /**
     * 天干四冲（无情之克中克得最严重的4个）
     */
    public static final String[] CHONG_GAN_4 = {
            "庚", "辛", "壬", "癸", "", "", "甲", "乙", "丙", "丁"
    };
    /**
     * 天干五合（有情之克中最有情的5个，甲己合，乙庚合，丙辛合，丁壬合，戊癸合）
     */
    public static final String[] HE_GAN_5 = {
            "己", "庚", "辛", "壬", "癸", "甲", "乙", "丙", "丁", "戊"
    };
    /**
     * 地支六合（子丑合，寅亥合，卯戌合，辰酉合，巳申合，午未合）
     */
    public static final String[] HE_ZHI_6 = {
            "丑", "子", "亥", "戌", "酉", "申", "未", "午", "巳", "辰", "卯", "寅"
    };
    /**
     * 十二值星
     */
    public static final String[] ZHI_XING = {
            "建", "除", "满", "平", "定", "执", "破", "危", "成", "收", "开", "闭"
    };
    /**
     * 十二天神
     */
    public static final String[] TIAN_SHEN = {
            "青龙", "明堂", "天刑", "朱雀", "金匮", "天德", "白虎", "玉堂", "天牢", "玄武", "司命", "勾陈"
    };
    /**
     * 彭祖百忌.天干
     */
    public static final String[] PENGZU_GAN = {
            "甲不开仓财物耗散", "乙不栽植千株不长", "丙不修灶必见灾殃", "丁不剃头头必生疮", "戊不受田田主不祥",
            "己不破券二比并亡", "庚不经络织机虚张", "辛不合酱主人不尝", "壬不泱水更难提防", "癸不词讼理弱敌强"
    };
    /**
     * 彭祖百忌.地支
     */
    public static final String[] PENGZU_ZHI = {
            "子不问卜自惹祸殃", "丑不冠带主不还乡", "寅不祭祀神鬼不尝", "卯不穿井水泉不香", "辰不哭泣必主重丧",
            "巳不远行财物伏藏", "午不苫盖屋主更张", "未不服药毒气入肠", "申不安床鬼祟入房", "酉不会客醉坐颠狂",
            "戌不吃犬作怪上床", "亥不嫁娶不利新郎"
    };
    /**
     * 月相，朔月也叫新月，望月也叫满月
     */
    public static final String[] YUE_XIANG = {
            "", "朔", "既朔", "蛾眉新", "蛾眉新", "蛾眉", "夕", "上弦", "上弦", "九夜", "宵", "宵", "宵",
            "渐盈凸", "小望", "望", "既望", "立待", "居待", "寝待", "更待", "渐亏凸", "下弦", "下弦",
            "有明", "有明", "蛾眉残", "蛾眉残", "残", "晓", "晦"
    };

    /**
     * 年太岁方位
     */
    public static final String[] POSITION_TAI_SUI_YEAR = {
            "坎", "艮", "艮", "震", "巽", "巽", "离", "坤", "坤", "兑", "坎", "坎"
    };
    /**
     * 天干方位
     */
    public static final String[] POSITION_GAN = {
            "震", "震", "离", "离", "中", "中", "兑", "兑", "坎", "坎"
    };
    /**
     * 地支方位
     */
    public static final String[] POSITION_ZHI = {
            "坎", "中", "震", "震", "中", "离", "离", "中", "兑", "兑", "中", "坎"
    };
    /**
     * 逐日胎神方位
     */
    public static final String[] POSITION_TAI_DAY = {
            "占门碓 外东南", "碓磨厕 外东南", "厨灶炉 外正南", "仓库门 外正南", "房床栖 外正南", "占门床 外正南",
            "占碓磨 外正南", "厕灶厨 外西南", "仓库炉 外西南", "房床门 外西南", "门碓栖 外西南", "碓磨床 外西南",
            "厨灶碓 外西南", "仓库厕 外正西", "房床炉 外正西", "占大门 外正西", "碓磨栖 外正西", "厨房床 外正西",
            "仓库碓 外西北", "房床厕 外西北", "占门炉 外西北", "门碓磨 外西北", "厨灶栖 外西北", "仓库床 外西北",
            "房床碓 外正北", "占门厕 外正北", "碓磨炉 外正北", "厨灶门 外正北", "仓库栖 外正北", "占房床 房内北",
            "占门碓 房内北", "碓磨厕 房内北", "厨灶炉 房内北", "门仓库 房内北", "床房栖 房内中", "占门床 房内中",
            "占碓磨 房内南", "厨磨厕 房内南", "仓库炉 房内南", "房床门 房内西", "门碓栖 房内东", "碓磨床 房内东",
            "厨灶碓 房内东", "仓库厕 房内东", "房床炉 房内中", "占大门 外东北", "碓磨栖 外东北", "厨灶床 外东北",
            "仓库碓 外东北", "房床厕 外东北", "占门炉 外东北", "门碓磨 外正东", "厨灶栖 外正东", "仓库床 外正东",
            "房床碓 外正东", "占门厕 外正东", "碓磨炉 外东南", "厨灶门 外东南", "仓库栖 外东南", "占房床 外东南"
    };
    /**
     * 逐月胎神方位
     */
    public static final String[] POSITION_TAI_MONTH = {
            "占房床", "占户窗", "占门堂", "占厨灶", "占房床", "占床仓", "占碓磨", "占厕户", "占门房", "占房床",
            "占灶炉", "占房床"
    };
    /**
     * 地支对应天神偏移下标
     */
    public static final Map<String, Integer> ZHI_TIAN_SHEN_OFFSET = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("子", 4);
            put("丑", 2);
            put("寅", 0);
            put("卯", 10);
            put("辰", 8);
            put("巳", 6);
            put("午", 4);
            put("未", 2);
            put("申", 0);
            put("酉", 10);
            put("戌", 8);
            put("亥", 6);
        }
    };
    /**
     * 天神类型：黄道，黑道
     */
    public static final Map<String, String> TIAN_SHEN_TYPE = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("青龙", "黄道");
            put("明堂", "黄道");
            put("金匮", "黄道");
            put("天德", "黄道");
            put("玉堂", "黄道");
            put("司命", "黄道");

            put("天刑", "黑道");
            put("朱雀", "黑道");
            put("白虎", "黑道");
            put("天牢", "黑道");
            put("玄武", "黑道");
            put("勾陈", "黑道");
        }
    };
    /**
     * 天神类型吉凶
     */
    public static final Map<String, String> TIAN_SHEN_TYPE_LUCK = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("黄道", "吉");
            put("黑道", "凶");
        }
    };
    /**
     * 兽
     */
    public static final Map<String, String> SHOU = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("东", "青龙");
            put("南", "朱雀");
            put("西", "白虎");
            put("北", "玄武");
        }
    };
    /**
     * 煞（逢巳日、酉日、丑日必煞东；亥日、卯日、未日必煞西；申日、子日、辰日必煞南；寅日、午日、戌日必煞北）
     */
    public static final Map<String, String> SHA = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("子", "南");
            put("丑", "东");
            put("寅", "北");
            put("卯", "西");
            put("辰", "南");
            put("巳", "东");
            put("午", "北");
            put("未", "西");
            put("申", "南");
            put("酉", "东");
            put("戌", "北");
            put("亥", "西");
        }
    };
    /**
     * 方位
     */
    public static final Map<String, String> POSITION_DESC = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("坎", "正北");
            put("艮", "东北");
            put("震", "正东");
            put("巽", "东南");
            put("离", "正南");
            put("坤", "西南");
            put("兑", "正西");
            put("乾", "西北");
            put("中", "中宫");
        }
    };
    /**
     * 宫
     */
    public static final Map<String, String> GONG = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("角", "东");
            put("井", "南");
            put("奎", "西");
            put("斗", "北");
            put("亢", "东");
            put("鬼", "南");
            put("娄", "西");
            put("牛", "北");
            put("氐", "东");
            put("柳", "南");
            put("胃", "西");
            put("女", "北");
            put("房", "东");
            put("星", "南");
            put("昴", "西");
            put("虚", "北");
            put("心", "东");
            put("张", "南");
            put("毕", "西");
            put("危", "北");
            put("尾", "东");
            put("翼", "南");
            put("觜", "西");
            put("室", "北");
            put("箕", "东");
            put("轸", "南");
            put("参", "西");
            put("壁", "北");
        }
    };
    /**
     * 政
     */
    public static final Map<String, String> ZHENG = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("角", "木");
            put("井", "木");
            put("奎", "木");
            put("斗", "木");
            put("亢", "金");
            put("鬼", "金");
            put("娄", "金");
            put("牛", "金");
            put("氐", "土");
            put("柳", "土");
            put("胃", "土");
            put("女", "土");
            put("房", "日");
            put("星", "日");
            put("昴", "日");
            put("虚", "日");
            put("心", "月");
            put("张", "月");
            put("毕", "月");
            put("危", "月");
            put("尾", "火");
            put("翼", "火");
            put("觜", "火");
            put("室", "火");
            put("箕", "水");
            put("轸", "水");
            put("参", "水");
            put("壁", "水");
        }
    };
    /**
     * 动物
     */
    public static final Map<String, String> ANIMAL = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("角", "蛟");
            put("斗", "獬");
            put("奎", "狼");
            put("井", "犴");
            put("亢", "龙");
            put("牛", "牛");
            put("娄", "狗");
            put("鬼", "羊");
            put("女", "蝠");
            put("氐", "貉");
            put("胃", "彘");
            put("柳", "獐");
            put("房", "兔");
            put("虚", "鼠");
            put("昴", "鸡");
            put("星", "马");
            put("心", "狐");
            put("危", "燕");
            put("毕", "乌");
            put("张", "鹿");
            put("尾", "虎");
            put("室", "猪");
            put("觜", "猴");
            put("翼", "蛇");
            put("箕", "豹");
            put("壁", "獝");
            put("参", "猿");
            put("轸", "蚓");
        }
    };
    /**
     * 天干五行
     */
    public static final Map<String, String> WU_XING_GAN = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("甲", "木");
            put("乙", "木");
            put("丙", "火");
            put("丁", "火");
            put("戊", "土");
            put("己", "土");
            put("庚", "金");
            put("辛", "金");
            put("壬", "水");
            put("癸", "水");
        }
    };
    /**
     * 地支五行
     */
    public static final Map<String, String> WU_XING_ZHI = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("寅", "木");
            put("卯", "木");
            put("巳", "火");
            put("午", "火");
            put("辰", "土");
            put("丑", "土");
            put("戌", "土");
            put("未", "土");
            put("申", "金");
            put("酉", "金");
            put("亥", "水");
            put("子", "水");
        }
    };
    /**
     * 纳音
     */
    public static final Map<String, String> NAYIN = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("甲子", "海中金");
            put("甲午", "沙中金");
            put("丙寅", "炉中火");
            put("丙申", "山下火");
            put("戊辰", "大林木");
            put("戊戌", "平地木");
            put("庚午", "路旁土");
            put("庚子", "壁上土");
            put("壬申", "剑锋金");
            put("壬寅", "金箔金");
            put("甲戌", "山头火");
            put("甲辰", "覆灯火");
            put("丙子", "涧下水");
            put("丙午", "天河水");
            put("戊寅", "城头土");
            put("戊申", "大驿土");
            put("庚辰", "白蜡金");
            put("庚戌", "钗钏金");
            put("壬午", "杨柳木");
            put("壬子", "桑柘木");
            put("甲申", "泉中水");
            put("甲寅", "大溪水");
            put("丙戌", "屋上土");
            put("丙辰", "沙中土");
            put("戊子", "霹雳火");
            put("戊午", "天上火");
            put("庚寅", "松柏木");
            put("庚申", "石榴木");
            put("壬辰", "长流水");
            put("壬戌", "大海水");
            put("乙丑", "海中金");
            put("乙未", "沙中金");
            put("丁卯", "炉中火");
            put("丁酉", "山下火");
            put("己巳", "大林木");
            put("己亥", "平地木");
            put("辛未", "路旁土");
            put("辛丑", "壁上土");
            put("癸酉", "剑锋金");
            put("癸卯", "金箔金");
            put("乙亥", "山头火");
            put("乙巳", "覆灯火");
            put("丁丑", "涧下水");
            put("丁未", "天河水");
            put("己卯", "城头土");
            put("己酉", "大驿土");
            put("辛巳", "白蜡金");
            put("辛亥", "钗钏金");
            put("癸未", "杨柳木");
            put("癸丑", "桑柘木");
            put("乙酉", "泉中水");
            put("乙卯", "大溪水");
            put("丁亥", "屋上土");
            put("丁巳", "沙中土");
            put("己丑", "霹雳火");
            put("己未", "天上火");
            put("辛卯", "松柏木");
            put("辛酉", "石榴木");
            put("癸巳", "长流水");
            put("癸亥", "大海水");
        }
    };
    /**
     * 天干十神，日主+天干为键
     */
    public static final Map<String, String> SHI_SHEN_GAN = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("甲甲", "比肩");
            put("甲乙", "劫财");
            put("甲丙", "食神");
            put("甲丁", "伤官");
            put("甲戊", "偏财");
            put("甲己", "正财");
            put("甲庚", "七杀");
            put("甲辛", "正官");
            put("甲壬", "偏印");
            put("甲癸", "正印");
            put("乙乙", "比肩");
            put("乙甲", "劫财");
            put("乙丁", "食神");
            put("乙丙", "伤官");
            put("乙己", "偏财");
            put("乙戊", "正财");
            put("乙辛", "七杀");
            put("乙庚", "正官");
            put("乙癸", "偏印");
            put("乙壬", "正印");
            put("丙丙", "比肩");
            put("丙丁", "劫财");
            put("丙戊", "食神");
            put("丙己", "伤官");
            put("丙庚", "偏财");
            put("丙辛", "正财");
            put("丙壬", "七杀");
            put("丙癸", "正官");
            put("丙甲", "偏印");
            put("丙乙", "正印");
            put("丁丁", "比肩");
            put("丁丙", "劫财");
            put("丁己", "食神");
            put("丁戊", "伤官");
            put("丁辛", "偏财");
            put("丁庚", "正财");
            put("丁癸", "七杀");
            put("丁壬", "正官");
            put("丁乙", "偏印");
            put("丁甲", "正印");
            put("戊戊", "比肩");
            put("戊己", "劫财");
            put("戊庚", "食神");
            put("戊辛", "伤官");
            put("戊壬", "偏财");
            put("戊癸", "正财");
            put("戊甲", "七杀");
            put("戊乙", "正官");
            put("戊丙", "偏印");
            put("戊丁", "正印");
            put("己己", "比肩");
            put("己戊", "劫财");
            put("己辛", "食神");
            put("己庚", "伤官");
            put("己癸", "偏财");
            put("己壬", "正财");
            put("己乙", "七杀");
            put("己甲", "正官");
            put("己丁", "偏印");
            put("己丙", "正印");
            put("庚庚", "比肩");
            put("庚辛", "劫财");
            put("庚壬", "食神");
            put("庚癸", "伤官");
            put("庚甲", "偏财");
            put("庚乙", "正财");
            put("庚丙", "七杀");
            put("庚丁", "正官");
            put("庚戊", "偏印");
            put("庚己", "正印");
            put("辛辛", "比肩");
            put("辛庚", "劫财");
            put("辛癸", "食神");
            put("辛壬", "伤官");
            put("辛乙", "偏财");
            put("辛甲", "正财");
            put("辛丁", "七杀");
            put("辛丙", "正官");
            put("辛己", "偏印");
            put("辛戊", "正印");
            put("壬壬", "比肩");
            put("壬癸", "劫财");
            put("壬甲", "食神");
            put("壬乙", "伤官");
            put("壬丙", "偏财");
            put("壬丁", "正财");
            put("壬戊", "七杀");
            put("壬己", "正官");
            put("壬庚", "偏印");
            put("壬辛", "正印");
            put("癸癸", "比肩");
            put("癸壬", "劫财");
            put("癸乙", "食神");
            put("癸甲", "伤官");
            put("癸丁", "偏财");
            put("癸丙", "正财");
            put("癸己", "七杀");
            put("癸戊", "正官");
            put("癸辛", "偏印");
            put("癸庚", "正印");
        }
    };
    /**
     * 地支十神，日主+地支藏干主气为键
     */
    public static final Map<String, String> SHI_SHEN_ZHI = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("甲子癸", "正印");
            put("甲丑癸", "正印");
            put("甲丑己", "正财");
            put("甲丑辛", "正官");
            put("甲寅丙", "食神");
            put("甲寅甲", "比肩");
            put("甲寅戊", "偏财");
            put("甲卯乙", "劫财");
            put("甲辰乙", "劫财");
            put("甲辰戊", "偏财");
            put("甲辰癸", "正印");
            put("甲巳戊", "偏财");
            put("甲巳丙", "食神");
            put("甲巳庚", "七杀");
            put("甲午丁", "伤官");
            put("甲午己", "正财");
            put("甲未乙", "劫财");
            put("甲未己", "正财");
            put("甲未丁", "伤官");
            put("甲申戊", "偏财");
            put("甲申庚", "七杀");
            put("甲申壬", "偏印");
            put("甲酉辛", "正官");
            put("甲戌辛", "正官");
            put("甲戌戊", "偏财");
            put("甲戌丁", "伤官");
            put("甲亥壬", "偏印");
            put("甲亥甲", "比肩");
            put("乙子癸", "偏印");
            put("乙丑癸", "偏印");
            put("乙丑己", "偏财");
            put("乙丑辛", "七杀");
            put("乙寅丙", "伤官");
            put("乙寅甲", "劫财");
            put("乙寅戊", "正财");
            put("乙卯乙", "比肩");
            put("乙辰乙", "比肩");
            put("乙辰戊", "正财");
            put("乙辰癸", "偏印");
            put("乙巳戊", "正财");
            put("乙巳丙", "伤官");
            put("乙巳庚", "正官");
            put("乙午丁", "食神");
            put("乙午己", "偏财");
            put("乙未乙", "比肩");
            put("乙未己", "偏财");
            put("乙未丁", "食神");
            put("乙申戊", "正财");
            put("乙申庚", "正官");
            put("乙申壬", "正印");
            put("乙酉辛", "七杀");
            put("乙戌辛", "七杀");
            put("乙戌戊", "正财");
            put("乙戌丁", "食神");
            put("乙亥壬", "正印");
            put("乙亥甲", "劫财");
            put("丙子癸", "正官");
            put("丙丑癸", "正官");
            put("丙丑己", "伤官");
            put("丙丑辛", "正财");
            put("丙寅丙", "比肩");
            put("丙寅甲", "偏印");
            put("丙寅戊", "食神");
            put("丙卯乙", "正印");
            put("丙辰乙", "正印");
            put("丙辰戊", "食神");
            put("丙辰癸", "正官");
            put("丙巳戊", "食神");
            put("丙巳丙", "比肩");
            put("丙巳庚", "偏财");
            put("丙午丁", "劫财");
            put("丙午己", "伤官");
            put("丙未乙", "正印");
            put("丙未己", "伤官");
            put("丙未丁", "劫财");
            put("丙申戊", "食神");
            put("丙申庚", "偏财");
            put("丙申壬", "七杀");
            put("丙酉辛", "正财");
            put("丙戌辛", "正财");
            put("丙戌戊", "食神");
            put("丙戌丁", "劫财");
            put("丙亥壬", "七杀");
            put("丙亥甲", "偏印");
            put("丁子癸", "七杀");
            put("丁丑癸", "七杀");
            put("丁丑己", "食神");
            put("丁丑辛", "偏财");
            put("丁寅丙", "劫财");
            put("丁寅甲", "正印");
            put("丁寅戊", "伤官");
            put("丁卯乙", "偏印");
            put("丁辰乙", "偏印");
            put("丁辰戊", "伤官");
            put("丁辰癸", "七杀");
            put("丁巳戊", "伤官");
            put("丁巳丙", "劫财");
            put("丁巳庚", "正财");
            put("丁午丁", "比肩");
            put("丁午己", "食神");
            put("丁未乙", "偏印");
            put("丁未己", "食神");
            put("丁未丁", "比肩");
            put("丁申戊", "伤官");
            put("丁申庚", "正财");
            put("丁申壬", "正官");
            put("丁酉辛", "偏财");
            put("丁戌辛", "偏财");
            put("丁戌戊", "伤官");
            put("丁戌丁", "比肩");
            put("丁亥壬", "正官");
            put("丁亥甲", "正印");
            put("戊子癸", "正财");
            put("戊丑癸", "正财");
            put("戊丑己", "劫财");
            put("戊丑辛", "伤官");
            put("戊寅丙", "偏印");
            put("戊寅甲", "七杀");
            put("戊寅戊", "比肩");
            put("戊卯乙", "正官");
            put("戊辰乙", "正官");
            put("戊辰戊", "比肩");
            put("戊辰癸", "正财");
            put("戊巳戊", "比肩");
            put("戊巳丙", "偏印");
            put("戊巳庚", "食神");
            put("戊午丁", "正印");
            put("戊午己", "劫财");
            put("戊未乙", "正官");
            put("戊未己", "劫财");
            put("戊未丁", "正印");
            put("戊申戊", "比肩");
            put("戊申庚", "食神");
            put("戊申壬", "偏财");
            put("戊酉辛", "伤官");
            put("戊戌辛", "伤官");
            put("戊戌戊", "比肩");
            put("戊戌丁", "正印");
            put("戊亥壬", "偏财");
            put("戊亥甲", "七杀");
            put("己子癸", "偏财");
            put("己丑癸", "偏财");
            put("己丑己", "比肩");
            put("己丑辛", "食神");
            put("己寅丙", "正印");
            put("己寅甲", "正官");
            put("己寅戊", "劫财");
            put("己卯乙", "七杀");
            put("己辰乙", "七杀");
            put("己辰戊", "劫财");
            put("己辰癸", "偏财");
            put("己巳戊", "劫财");
            put("己巳丙", "正印");
            put("己巳庚", "伤官");
            put("己午丁", "偏印");
            put("己午己", "比肩");
            put("己未乙", "七杀");
            put("己未己", "比肩");
            put("己未丁", "偏印");
            put("己申戊", "劫财");
            put("己申庚", "伤官");
            put("己申壬", "正财");
            put("己酉辛", "食神");
            put("己戌辛", "食神");
            put("己戌戊", "劫财");
            put("己戌丁", "偏印");
            put("己亥壬", "正财");
            put("己亥甲", "正官");
            put("庚子癸", "伤官");
            put("庚丑癸", "伤官");
            put("庚丑己", "正印");
            put("庚丑辛", "劫财");
            put("庚寅丙", "七杀");
            put("庚寅甲", "偏财");
            put("庚寅戊", "偏印");
            put("庚卯乙", "正财");
            put("庚辰乙", "正财");
            put("庚辰戊", "偏印");
            put("庚辰癸", "伤官");
            put("庚巳戊", "偏印");
            put("庚巳丙", "七杀");
            put("庚巳庚", "比肩");
            put("庚午丁", "正官");
            put("庚午己", "正印");
            put("庚未乙", "正财");
            put("庚未己", "正印");
            put("庚未丁", "正官");
            put("庚申戊", "偏印");
            put("庚申庚", "比肩");
            put("庚申壬", "食神");
            put("庚酉辛", "劫财");
            put("庚戌辛", "劫财");
            put("庚戌戊", "偏印");
            put("庚戌丁", "正官");
            put("庚亥壬", "食神");
            put("庚亥甲", "偏财");
            put("辛子癸", "食神");
            put("辛丑癸", "食神");
            put("辛丑己", "偏印");
            put("辛丑辛", "比肩");
            put("辛寅丙", "正官");
            put("辛寅甲", "正财");
            put("辛寅戊", "正印");
            put("辛卯乙", "偏财");
            put("辛辰乙", "偏财");
            put("辛辰戊", "正印");
            put("辛辰癸", "食神");
            put("辛巳戊", "正印");
            put("辛巳丙", "正官");
            put("辛巳庚", "劫财");
            put("辛午丁", "七杀");
            put("辛午己", "偏印");
            put("辛未乙", "偏财");
            put("辛未己", "偏印");
            put("辛未丁", "七杀");
            put("辛申戊", "正印");
            put("辛申庚", "劫财");
            put("辛申壬", "伤官");
            put("辛酉辛", "比肩");
            put("辛戌辛", "比肩");
            put("辛戌戊", "正印");
            put("辛戌丁", "七杀");
            put("辛亥壬", "伤官");
            put("辛亥甲", "正财");
            put("壬子癸", "劫财");
            put("壬丑癸", "劫财");
            put("壬丑己", "正官");
            put("壬丑辛", "正印");
            put("壬寅丙", "偏财");
            put("壬寅甲", "食神");
            put("壬寅戊", "七杀");
            put("壬卯乙", "伤官");
            put("壬辰乙", "伤官");
            put("壬辰戊", "七杀");
            put("壬辰癸", "劫财");
            put("壬巳戊", "七杀");
            put("壬巳丙", "偏财");
            put("壬巳庚", "偏印");
            put("壬午丁", "正财");
            put("壬午己", "正官");
            put("壬未乙", "伤官");
            put("壬未己", "正官");
            put("壬未丁", "正财");
            put("壬申戊", "七杀");
            put("壬申庚", "偏印");
            put("壬申壬", "比肩");
            put("壬酉辛", "正印");
            put("壬戌辛", "正印");
            put("壬戌戊", "七杀");
            put("壬戌丁", "正财");
            put("壬亥壬", "比肩");
            put("壬亥甲", "食神");
            put("癸子癸", "比肩");
            put("癸丑癸", "比肩");
            put("癸丑己", "七杀");
            put("癸丑辛", "偏印");
            put("癸寅丙", "正财");
            put("癸寅甲", "伤官");
            put("癸寅戊", "正官");
            put("癸卯乙", "食神");
            put("癸辰乙", "食神");
            put("癸辰戊", "正官");
            put("癸辰癸", "比肩");
            put("癸巳戊", "正官");
            put("癸巳丙", "正财");
            put("癸巳庚", "正印");
            put("癸午丁", "偏财");
            put("癸午己", "七杀");
            put("癸未乙", "食神");
            put("癸未己", "七杀");
            put("癸未丁", "偏财");
            put("癸申戊", "正官");
            put("癸申庚", "正印");
            put("癸申壬", "劫财");
            put("癸酉辛", "偏印");
            put("癸戌辛", "偏印");
            put("癸戌戊", "正官");
            put("癸戌丁", "偏财");
            put("癸亥壬", "劫财");
            put("癸亥甲", "伤官");
        }
    };
    /**
     * 地支藏干表，分别为主气、余气、杂气
     */
    public static final Map<String, List<String>> ZHI_HIDE_GAN = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("子", Collections.nCopies(1, "癸"));
            put("丑", Arrays.asList("己", "癸", "辛"));
            put("寅", Arrays.asList("甲", "丙", "戊"));
            put("卯", Collections.nCopies(1, "乙"));
            put("辰", Arrays.asList("戊", "乙", "癸"));
            put("巳", Arrays.asList("丙", "庚", "戊"));
            put("午", Arrays.asList("丁", "己"));
            put("未", Arrays.asList("己", "丁", "乙"));
            put("申", Arrays.asList("庚", "壬", "戊"));
            put("酉", Collections.nCopies(1, "辛"));
            put("戌", Arrays.asList("戊", "辛", "丁"));
            put("亥", Arrays.asList("壬", "甲"));
        }
    };
    /**
     * 农历日期对应的节日
     */
    public static final Map<String, String> FESTIVAL = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("1-1", "春节");
            put("1-15", "元宵节");
            put("2-2", "龙头节");
            put("5-5", "端午节");
            put("7-7", "七夕节");
            put("8-15", "中秋节");
            put("9-9", "重阳节");
            put("12-8", "腊八节");
            put("12-30", "除夕");
        }
    };
    /**
     * 传统节日
     */
    public static final Map<String, List<String>> OTHER_FESTIVAL = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("1-4", Collections.nCopies(1, "接神日"));
            put("1-5", Collections.nCopies(1, "隔开日"));
            put("1-7", Collections.nCopies(1, "人日"));
            put("1-8", Arrays.asList("谷日", "顺星节"));
            put("1-9", Collections.nCopies(1, "天日"));
            put("1-10", Collections.nCopies(1, "地日"));
            put("1-20", Collections.nCopies(1, "天穿节"));
            put("1-25", Collections.nCopies(1, "填仓节"));
            put("1-30", Collections.nCopies(1, "正月晦"));
            put("2-1", Collections.nCopies(1, "中和节"));
            put("2-2", Collections.nCopies(1, "社日节"));
            put("3-3", Collections.nCopies(1, "上巳节"));
            put("5-20", Collections.nCopies(1, "分龙节"));
            put("5-25", Collections.nCopies(1, "会龙节"));
            put("6-6", Collections.nCopies(1, "天贶节"));
            put("6-24", Collections.nCopies(1, "观莲节"));
            put("6-25", Collections.nCopies(1, "五谷母节"));
            put("7-14", Collections.nCopies(1, "中元节"));
            put("7-22", Collections.nCopies(1, "财神节"));
            put("7-29", Collections.nCopies(1, "地藏节"));
            put("8-1", Collections.nCopies(1, "天灸日"));
            put("10-1", Collections.nCopies(1, "寒衣节"));
            put("10-10", Collections.nCopies(1, "十成节"));
            put("10-15", Collections.nCopies(1, "下元节"));
            put("12-7", Collections.nCopies(1, "驱傩日"));
            put("12-16", Collections.nCopies(1, "尾牙"));
            put("12-24", Collections.nCopies(1, "祭灶日"));
        }
    };
    /**
     * 28星宿对照表，地支+星期
     */
    public static final Map<String, String> XIU = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("申1", "毕");
            put("申2", "翼");
            put("申3", "箕");
            put("申4", "奎");
            put("申5", "鬼");
            put("申6", "氐");
            put("申0", "虚");

            put("子1", "毕");
            put("子2", "翼");
            put("子3", "箕");
            put("子4", "奎");
            put("子5", "鬼");
            put("子6", "氐");
            put("子0", "虚");

            put("辰1", "毕");
            put("辰2", "翼");
            put("辰3", "箕");
            put("辰4", "奎");
            put("辰5", "鬼");
            put("辰6", "氐");
            put("辰0", "虚");

            put("巳1", "危");
            put("巳2", "觜");
            put("巳3", "轸");
            put("巳4", "斗");
            put("巳5", "娄");
            put("巳6", "柳");
            put("巳0", "房");

            put("酉1", "危");
            put("酉2", "觜");
            put("酉3", "轸");
            put("酉4", "斗");
            put("酉5", "娄");
            put("酉6", "柳");
            put("酉0", "房");

            put("丑1", "危");
            put("丑2", "觜");
            put("丑3", "轸");
            put("丑4", "斗");
            put("丑5", "娄");
            put("丑6", "柳");
            put("丑0", "房");

            put("寅1", "心");
            put("寅2", "室");
            put("寅3", "参");
            put("寅4", "角");
            put("寅5", "牛");
            put("寅6", "胃");
            put("寅0", "星");

            put("午1", "心");
            put("午2", "室");
            put("午3", "参");
            put("午4", "角");
            put("午5", "牛");
            put("午6", "胃");
            put("午0", "星");

            put("戌1", "心");
            put("戌2", "室");
            put("戌3", "参");
            put("戌4", "角");
            put("戌5", "牛");
            put("戌6", "胃");
            put("戌0", "星");

            put("亥1", "张");
            put("亥2", "尾");
            put("亥3", "壁");
            put("亥4", "井");
            put("亥5", "亢");
            put("亥6", "女");
            put("亥0", "昴");

            put("卯1", "张");
            put("卯2", "尾");
            put("卯3", "壁");
            put("卯4", "井");
            put("卯5", "亢");
            put("卯6", "女");
            put("卯0", "昴");

            put("未1", "张");
            put("未2", "尾");
            put("未3", "壁");
            put("未4", "井");
            put("未5", "亢");
            put("未6", "女");
            put("未0", "昴");
        }
    };
    /**
     * 星宿对应吉凶
     */
    public static final Map<String, String> XIU_LUCK = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("角", "吉");
            put("亢", "凶");
            put("氐", "凶");
            put("房", "吉");
            put("心", "凶");
            put("尾", "吉");
            put("箕", "吉");
            put("斗", "吉");
            put("牛", "凶");
            put("女", "凶");
            put("虚", "凶");
            put("危", "凶");
            put("室", "吉");
            put("壁", "吉");
            put("奎", "凶");
            put("娄", "吉");
            put("胃", "吉");
            put("昴", "凶");
            put("毕", "吉");
            put("觜", "凶");
            put("参", "吉");
            put("井", "吉");
            put("鬼", "凶");
            put("柳", "凶");
            put("星", "凶");
            put("张", "吉");
            put("翼", "凶");
            put("轸", "吉");
        }
    };
    /**
     * 星宿对应吉凶
     */
    public static final Map<String, String> XIU_SONG = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("角", "角星造作主荣昌，外进田财及女郎，嫁娶婚姻出贵子，文人及第见君王，惟有埋葬不可用，" +
                    "三年之后主瘟疫，起工修筑坟基地，堂前立见主人凶。");
            put("亢", "亢星造作长房当，十日之中主有殃，田地消磨官失职，接运定是虎狼伤，嫁娶婚姻用此日，" +
                    "儿孙新妇守空房，埋葬若还用此日，当时害祸主重伤。");
            put("氐", "氐星造作主灾凶，费尽田园仓库空，埋葬不可用此日，悬绳吊颈祸重重，若是婚姻离别散，" +
                    "夜招浪子入房中，行船必定遭沉没，更生聋哑子孙穷。");
            put("房", "房星造作田园进，钱财牛马遍山岗，更招外处田庄宅，荣华富贵福禄康，埋葬若然用此日，" +
                    "高官进职拜君王，嫁娶嫦娥至月殿，三年抱子至朝堂。");
            put("心", "心星造作大为凶，更遭刑讼狱囚中，忤逆官非宅产退，埋葬卒暴死相从，婚姻若是用此日，" +
                    "子死儿亡泪满胸，三年之内连遭祸，事事教君没始终。");
            put("尾", "尾星造作主天恩，富贵荣华福禄增，招财进宝兴家宅，和合婚姻贵子孙，埋葬若能依此日，" +
                    "男清女正子孙兴，开门放水招田宅，代代公侯远播名。");
            put("箕", "箕星造作主高强，岁岁年年大吉昌，埋葬修坟大吉利，田蚕牛马遍山岗，开门放水招田宅，" +
                    "箧满金银谷满仓，福荫高官加禄位，六亲丰禄乐安康。");
            put("斗", "斗星造作主招财，文武官员位鼎台，田宅家财千万进，坟堂修筑贵富来，开门放水招牛马，" +
                    "旺蚕男女主和谐，遇此吉宿来照护，时支福庆永无灾。");
            put("牛", "牛星造作主灾危，九横三灾不可推，家宅不安人口退，田蚕不利主人衰，嫁娶婚姻皆自损，" +
                    "金银财谷渐无之，若是开门并放水，牛猪羊马亦伤悲。");
            put("女", "女星造作损婆娘，兄弟相嫌似虎狼，埋葬生灾逢鬼怪，颠邪疾病主瘟惶，为事遭官财失散，" +
                    "泻利留连不可当，开门放水用此日，全家财散主离乡。");
            put("虚", "虚星造作主灾殃，男女孤眠不一双，内乱风声无礼节，儿孙媳妇伴人床，开门放水遭灾祸，" +
                    "虎咬蛇伤又卒亡，三三五五连年病，家破人亡不可当。");
            put("危", "危星不可造高楼，自遭刑吊见血光，三年孩子遭水厄，后生出外永不还，埋葬若还逢此日，" +
                    "周年百日取高堂，三年两载一悲伤，开门放水到官堂。");
            put("室", "室星修造进田牛，儿孙代代近王侯，家贵荣华天上至，寿如彭祖八千秋，开门放水招财帛，" +
                    "和合婚姻生贵儿，埋葬若能依此日，门庭兴旺福无休。");
            put("壁", "壁星造作主增财，丝蚕大熟福滔天，奴婢自来人口进，开门放水出英贤，埋葬招财官品进，" +
                    "家中诸事乐陶然，婚姻吉利主贵子，早播名誉著祖鞭。");
            put("奎", "奎星造作得祯祥，家内荣和大吉昌，若是埋葬阴卒死，当年定主两三伤，看看军令刑伤到，" +
                    "重重官事主瘟惶，开门放水遭灾祸，三年两次损儿郎。");
            put("娄", "娄星修造起门庭，财旺家和事事兴，外进钱财百日进，一家兄弟播高名，婚姻进益生贵子，" +
                    "玉帛金银箱满盈，放水开门皆吉利，男荣女贵寿康宁。");
            put("胃", "胃星造作事如何，家贵荣华喜气多，埋葬贵临官禄位，夫妇齐眉永保康，婚姻遇此家富贵，" +
                    "三灾九祸不逢他，从此门前多吉庆，儿孙代代拜金阶。");
            put("昴", "昴星造作进田牛，埋葬官灾不得休，重丧二日三人死，尽卖田园不记增，开门放水招灾祸，" +
                    "三岁孩儿白了头，婚姻不可逢此日，死别生离是可愁。");
            put("毕", "毕星造作主光前，买得田园有余钱，埋葬此日添官职，田蚕大熟永丰年，开门放水多吉庆，" +
                    "合家人口得安然，婚姻若得逢此日，生得孩儿福寿全。");
            put("觜", "觜星造作有徒刑，三年必定主伶丁，埋葬卒死多因此，取定寅年使杀人，三丧不止皆由此，" +
                    "一人药毒二人身，家门田地皆退败，仓库金银化作尘。");
            put("参", "参星造作旺人家，文星照耀大光华，只因造作田财旺，埋葬招疾哭黄沙，开门放水加官职，" +
                    "房房子孙见田加，婚姻许遁遭刑克，男女朝开幕落花。");
            put("井", "井星造作旺蚕田，金榜题名第一光，埋葬须防惊卒死，狂颠风疾入黄泉，开门放水招财帛，" +
                    "牛马猪羊旺莫言，贵人田塘来入宅，儿孙兴旺有余钱。");
            put("鬼", "鬼星起造卒人亡，堂前不见主人郎，埋葬此日官禄至，儿孙代代近君王，开门放水须伤死，" +
                    "嫁娶夫妻不久长，修土筑墙伤产女，手扶双女泪汪汪。");
            put("柳", "柳星造作主遭官，昼夜偷闭不暂安，埋葬瘟惶多疾病，田园退尽守冬寒，开门放水遭聋瞎，" +
                    "腰驼背曲似弓弯，更有棒刑宜谨慎，妇人随客走盘桓。");
            put("星", "星宿日好造新房，进职加官近帝王，不可埋葬并放水，凶星临位女人亡，生离死别无心恋，" +
                    "要自归休别嫁郎，孔子九曲殊难度，放水开门天命伤。");
            put("张", "张星日好造龙轩，年年并见进庄田，埋葬不久升官职，代代为官近帝前，开门放水招财帛，" +
                    "婚姻和合福绵绵，田蚕人满仓库满，百般顺意自安然。");
            put("翼", "翼星不利架高堂，三年二载见瘟惶，埋葬若还逢此日，子孙必定走他乡，婚姻此日不宜利，" +
                    "归家定是不相当，开门放水家须破，少女恋花贪外郎。");
            put("轸", "轸星临水造龙宫，代代为官受皇封，富贵荣华增寿禄，库满仓盈自昌隆，埋葬文昌来照助，" +
                    "宅舍安宁不见凶，更有为官沾帝宠，婚姻龙子入龙宫。");
        }
    };
    /**
     * 实际的节气表
     */
    public static final String[] JIE_QI_IN_USE = {
            "DA_XUE", "冬至", "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏",
            "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降",
            "立冬", "小雪", "大雪", "DONG_ZHI", "XIAO_HAN", "DA_HAN", "LI_CHUN", "YU_SHUI", "JING_ZHE"
    };
    /**
     * 禄（甲禄在寅，乙禄在卯，丙戊禄在巳、丁己禄在午、庚禄在申、辛禄在酉、壬禄在亥、癸禄在子）
     */
    public static final Map<String, String> LU = new HashMap<>() {
        private static final long serialVersionUID = -1L;

        {
            put("甲", "寅");
            put("乙", "卯");
            put("丙", "巳");
            put("丁", "午");
            put("戊", "巳");
            put("己", "午");
            put("庚", "申");
            put("辛", "酉");
            put("壬", "亥");
            put("癸", "子");

            put("寅", "甲");
            put("卯", "乙");
            put("巳", "丙,戊");
            put("午", "丁,己");
            put("申", "庚");
            put("酉", "辛");
            put("亥", "壬");
            put("子", "癸");
        }
    };
    /**
     * 宜忌
     */
    private static final String[] YI_JI = {
            "祭祀", "祈福", "求嗣", "开光", "塑绘", "齐醮", "斋醮", "沐浴", "酬神", "造庙", "祀灶",
            "焚香", "谢土", "出火", "雕刻", "嫁娶", "订婚", "纳采", "问名", "纳婿", "归宁", "安床",
            "合帐", "冠笄", "订盟", "进人口", "裁衣", "挽面", "开容", "修坟", "启钻", "破土", "安葬",
            "立碑", "成服", "除服", "开生坟", "合寿木", "入殓", "移柩", "普渡", "入宅", "安香", "安门",
            "修造", "起基", "动土", "上梁", "竖柱", "开井开池", "作陂放水", "拆卸", "破屋", "坏垣",
            "补垣", "伐木做梁", "作灶", "解除", "开柱眼", "穿屏扇架", "盖屋合脊", "开厕", "造仓", "塞穴",
            "平治道涂", "造桥", "作厕", "筑堤", "开池", "伐木", "开渠", "掘井", "扫舍", "放水", "造屋",
            "合脊", "造畜稠", "修门", "定磉", "作梁", "修饰垣墙", "架马", "开市", "挂匾", "纳财", "求财",
            "开仓", "买车", "置产", "雇庸", "出货财", "安机械", "造车器", "经络", "酝酿", "作染", "鼓铸",
            "造船", "割蜜", "栽种", "取渔", "结网", "牧养", "安碓磑", "习艺", "入学", "理发", "探病",
            "见贵", "乘船", "渡水", "针灸", "出行", "移徙", "分居", "剃头", "整手足甲", "纳畜", "捕捉",
            "畋猎", "教牛马", "会亲友", "赴任", "求医", "治病", "词讼", "起基动土", "破屋坏垣", "盖屋",
            "造仓库", "立券交易", "交易", "立券", "安机", "会友", "求医疗病", "诸事不宜", "馀事勿取",
            "行丧", "断蚁", "归岫", "无"
    };
    /**
     * 每日宜忌数据
     */
    private static final String DAY_YI_JI = "30=192531010D:838454151A4C200C1E23221D212726,030F522" +
            "E1F00=2430000C18:8319000776262322200C1E1D,06292C2E1F04=32020E1A26:791715795B0001025D" +
            ",0F522E38201D=162E3A0A22:790F181113332C2E2D302F157954,7001203810=0E1A263202:79026A17" +
            "6576036A,522E201F05=0D19250131:7911192C2E302F00030401060F1571292A75,707C20522F=0C182" +
            "43000:4F2C2E2B383F443D433663,0F01478A20151D=0E1A320226:3840,0001202B892F=14202C3808:" +
            "3807504089,8829=0E1A263202:383940,6370018A75202B454F6605=32020E1A26:38394089,0001202" +
            "B22=16223A0A2E:384C,8A2020=2B3707131F:2C2E5B000739337C38802D44484C2425201F1E272621,5" +
            "229701535=121E2A3606:2C2E2D2B156343364C,0F4729710D708A20036A1904=0D19250131:50402627" +
            "89,0F7129033B=202C380814:5040000738,0F7D7C584F012063452B35=1A2632020E:50400089,8813=" +
            "1A2632020E:69687011180F791966762627201E,0352292E8034=182430000C:291503000D332E53261F" +
            "2075,0F5238584F450B=000C182430:297170192C2E2D2F2B3E363F4C,0F52156320010347200B=131F2" +
            "B3707:297115030102195283840D332C2E,0F1F5863201D8A02=222E3A0A16:261F1E20232289,522900" +
            "58363F32=16222E3A0A:261F201E232289,8D39=0D19310125:262322271E201D21,52450F4F09=0D192" +
            "53101:262322271E202189,1F4526=16222E3A0A:262322271F1E20,712906=0F1B273303:1726232227" +
            "4050,80387C6B2C=0915212D39:1707702C2E71291F20,0F52000106111D15=16222E3A0A:170007386A" +
            "7448363F261F1E,030F79636F2026=030F1B2733:1784832C2E5B26201F,0F010D2913=182430000C:17" +
            "5447440D15838477656A49,2B2E1F8A202228=101C283404:70504C7889,8803=0D19250131:700F1811" +
            "26151E20001A7919,8D2F=0915212D39:705283845B0D2F71,0F202E4106=3606121E2A:70786289,068" +
            "02E1F23=1824000C30:70076A363F,292017=202C380814:700718111A302F717566,0F2B2E2026=3B0B" +
            "17232F:70545283842E71291A7933192A5D5A5040,090C384F45208A1D6B38=212D390915:7039170F45" +
            "513A2C2E7129242526271F201D,00010352153A=15212D3909:703911170E2C2E2D2F4B15712952633D," +
            "092B8A2027=010D192531:702D155483840F63262720,53292F017D4F38442B2E1F4717=16222E3A0A:7" +
            "05C4C39171A4F0E7971295B4C5248,0F2E1F1D37=1A2632020E:2E260F27201F,523815292F1A22=0E1A" +
            "260232:64262322271F2021,0F2F293822=2F3B0B1723:161A0F1526271F4C,586103473818=2430000C" +
            "18:161A7889,292E1F0F386131=17232F3B0B:04795B3F651A5D,0F5201062016=14202C3808:04170F7" +
            "9195D1A637566363F76,01522E8A2039=132B37071F:0470170F191A134C8384662426232227201E,8D0" +
            "8=0D19253101:040370181123220F1326271E2021,29153B=0D19310125:040307177938494C,0F26207" +
            "017=0E2632021A:0403010218111A17332C2E2D2B15713E6575,45382064291D=142C380820:04033918" +
            "110F0D2C2E7129332D2B72528384547566,8D1C=1830000C24:040318111A17332C15290D200C7A,4745" +
            "063835=0F2733031B:040318111A16175B795452848315302F6563395D,387029202E=14202C3808:040" +
            "31975363F6366,0F5401202C5283842E2F1E=0E1A320226:0403080618111A16332E2F152A0953791970" +
            "2C5445490D75072B,8063203820=182430000C:04067033392C7161262322271E1D210C,8D2F=101C283" +
            "404:3F4889,881C=2733030F1B:3F74397677658988,0F3847201D=293505111D:3F8B657789,0F20297" +
            "02E7D35=111D293505:3F8B6589,1F200A=020E1A2632:3F656477,0F2B71292005=111D290535:3F658" +
            "9,8810=0F1B273303:3F88,2B38200F1C=293505111D:0F83843D363F776424,15462F2C52032971152A" +
            "=0F1B273303:0F17795B54838458,52807C3811=121E2A3606:0F172C2E387129363F7566512C2E2D4E4" +
            "461,01034752203A=172F3B0B23:0F171511793F76584C,0347200C1D20=2D39091521:0F175B3975660" +
            "745514F2B4825201E211D,010352292E2E=0F1B273303:0F170070792C2E261F,040341232228=05111D" +
            "2935:0F1700707129385C363F3D1F1E232226,80412B202F14=14202C3808:0F17000728705448757A,5" +
            "22E1F15562F05=30000C1824:0F17000102061979454F3A15477677,241F8A2021=2F3B0B1723:0F1700" +
            "0102060370392E52838453331F,452F2C266A79292B203810=0C18243000:0F170001020E032A70692C2" +
            "E302F802D2B0D7129474C201F2322,5211183809615D34=1A2632020E:0F171170792F5B156677000103" +
            "2C2B802D,29387C207134=14202C3808:0F0D33000103452E528384297115752620,63386F7014=15212" +
            "D3909:0F7045332C2E71201F1D21,4701155229530327=101C283404:0F70161715232238838426271F2" +
            "0,7D035219=121E2A3606:0F705B0004037C5D15653F1F26,522B473809=131F2B0737:0F705215261E2" +
            "0,012E1F25=182430000C:0F707B7C00012F75,52201B=2531010D19:0F706A151E201D528384544466," +
            "47010C2E292F2C3820=14202C3808:0F707500261E20,382E1F05=3606121E2A:0F161A17452F0D33712" +
            "C2E2B5443633F,150170208A0327=0E1A263202:0F150370002E0D3979528384532971331F1E20,477D0" +
            "D=06121E2A36:0F5B8370000102060403161A494447,386A418A201A=17232F3B0B:0F03700D332C2E29" +
            "71152F52838463,01004547380C26=101C283404:0F03700D33195284835329711563,01260038206B0E" +
            "=131F2B3707:0F03706A4F0D332C528384532E29711563,450075000F=131F2B3707:0F0370010239332" +
            "E2C19528384532971156375262720,8D18=17232F3B0B:0F0370390D332C192E2971637547202322,581" +
            "528=0E1A263202:0F0302791566046F,29710D722A38528384202E4530=0E1A263202:0F030102392E15" +
            "634447001F1E,293845200D707538=1E2A360612:0F0300017039712952542D2C302F80380D2A363F334" +
            "9483E616320,1118150C1F2E20=33030F1B27:0F03000102700D29713963451F0C20,528338542F15806" +
            "128=121E2A3606:0F030001027039452971150D332C2F6327,2052838403=2C38081420:0F030001022A" +
            "0D3945297115528384630D7020,476A382E1F4426=010D192531:0F03390D332C1929711563261D2E232" +
            "2,382000521118750C706B15=131F2B3707:0F033915666A52261E272048,382E2F6329712C0114=0D19" +
            "253101:0F52838403700D332C29712E1F27201E2322,1545017505=131F2B3707:0F528400012E7129,0" +
            "92026=3707131F2B:0F528471295B795D2B155333565A446375661F201E272621,00016B0C4113=14202" +
            "C3808:0F280001363F8B4326232220,2E1F47032F7D35=16222E3A0A:0F0211195465756679,2F384570" +
            "202B6A10=15212D3909:0F0102700D332C2E2F0319528384531529716345261F2322,8D32=101C283404" +
            ":0F0102037039330D5284832971152E1F0C,0026206B37=16222E3A0A:0F003854,20521D2106=020E1A" +
            "2632:0F00175058,5D6B80382E16=1B2733030F:0F00701784831952712C2E1526271F,033806201F=2B" +
            "3707131F:0F00701A17830E544C5C0E78,7129632E1F38208A452F16=15212D3909:0F00040370396A74" +
            "2E15444948,458A384F2021=16222E3A0A:0F005B261F20,2E2F1D=2531010D19:0F0003450D3329712C" +
            "2E2F1575,528A63705A20587D7C12=17232F3B0B:0F00030D70332C2E3952838453542971156375,6B20" +
            "19=1B2733030F:0F000301020D297115332E1F0C,165220262E=121E2A3606:0F00030102700D332E2C1" +
            "92971155383846375261F1E20,8D1F=33030F1B27:0F00030102700D19297115332C2B535448,2E45208" +
            "A00=2632020E1A:0F00030102705283842E544779,2920454F754C3836=16222E3A0A:0F005203702971" +
            "0D332C15,7545584F8A201D2121=121E2A3606:0F00074850,8A2036=0D25310119:0F00071A706A7176" +
            "77492923221E202726,80522E1F39=1E2A360612:0F006A385040740717,1F70631E=212D390915:0F00" +
            "6A1938271779,565A4575522F801F1E632B=121E2A3606:0F00010D0302703352838453297115632E,20" +
            "8A454F2B=0E1A263202:0F000170390D332E2971152F63751F1E20,52846A381F=14202C3808:0F00010" +
            "6387129,2E1F24=14202C3808:0F0001062E7129,522010=0814202C38:0F0001062871292E7C5283840" +
            "32C5C2A15767765,11185D8A206B08=131F2B0737:0F0001067C1F20,522900=202C380814:0F0001020" +
            "D700339332C192A83842971152E1F0C20262322,065256386110=111D293505:0F000102700D332C2E29" +
            "7115383F631F20,0347562B=14202C3808:0F000102700D332C712E15261F201E,80036A61473831=0C1" +
            "8243000:0F000102700D335283845329711563,38048A7D45202A=14202C3808:0F000102702E15471F1" +
            "E,294F2B452C2F268011=0D19253101:0F0001022E792D3E75663D19,472063703852292B39=222E3A0A" +
            "16:0F0001022E154826271F1E203874362322,036312=0D19253101:0F000102032971152C2E19,47206" +
            "37038522B15=111D293505:0F000102030D70332E3919528384532971152B2F201F0C,8D1B=232F3B0B1" +
            "7:0F000102030D7033528384534529711520,63475814=131F2B3707:0F000102030D332C2E195283845" +
            "329716375261E2322,8D19=15212D3909:0F00010203700D332C2E1929711552838453637526202322,8" +
            "D09=111D293505:0F00010203700D332E2F192971152B52838453631F20,8D33=1A2632020E:0F000102" +
            "03700D332E2F1929711552838453261F201E2322,8D03=2E3A0A1622:0F0001020370332C2E2F1575261" +
            "F,2971476A458352380C=111D293505:0F0001020370332E2F0D19297115637566302B2C3979,8D08=00" +
            "0C182430:0F000102037039297175261F1D21,454F2E1563410F=17232F3B0B:0F0001020370390D3319" +
            "297115632E2C752620212322,8D07=3606121E2A:0F0001020370390D332C1929712E157563548384534" +
            "C,20248A38=16222E3A0A:0F0001020370390D1952838453542971631F0C,152036=14202C3808:0F000" +
            "10203703915632719792322,80262045297158750F=111D293505:0F00010203528384157033,7529712" +
            "06B452F2B262E05=3404101C28:0F00010206030D7129302F79802D7C7C2B5C4744,11701D2052843833" +
            "=111D293505:0F00010206181139702E1F686F6A792D2C2E304E15337566491F23221D21,52296B0D800" +
            "D=15212D3909:0F000102070D70332C2E19528384297115637526201E2322,8D05=2C38081420:0F0001" +
            "021A175D2C19152E302F7183846379,8A20704F7545410A=131F2B3707:0F001A651707,565A58202E1F" +
            "476320=121E36062A:0F11707B7C5271291E20,2E1F39=111D293505:0F11700001522E71291F20,2B07" +
            "=131F2B0737:0F11700001397129,2E2002=111D293505:0F11707129,2E1F2002=131F37072B:0F1152" +
            "702E2F71291F20,000103=131F37072B:0F1152702E2F71291F20,7A3A=111D293505:0F117B7C2C2E71" +
            "291F20,520300=111D350529:0F110001702E2F71291F20,0621=101C280434:0F11000170717B,522E1" +
            "F0A=06121E2A36:0F110001708471292E1F20,03388051561C=121E2A3606:0F1100017B7C702E7129,5" +
            "22B22=2D39091521:0F110039702C2E522F1574487B7C2D4E804B,098A204538612B=05111D2935:0F11" +
            "18795B65170002195D,52382E8A201E=2531010D19:0F111829711500010370390D332E750C201F,4552" +
            "832F382B8004=2A3606121E:0F1118175C000301027039450D29332C2E2F15631F,8A582020=31010D19" +
            "25:0F1118032A0D545283841A802D2C2E2B71296366774744201F26232221,010900150C06=2C3808142" +
            "0:0F11180300706A2E1549466319,292F26806B382B20754506=2E3A0A1622:0F1118528384530001035" +
            "C53702971152B332C2E63201F1E23222621,6B75452D4F802E=111D293505:0F1118060300017B7C792E" +
            "39767566261F20,7129805136=232F3B0B17:0F111800171A454F514E3A3871157765443D23221E26272" +
            "0,80612E1F1C=212D390915:0F11180003706A4F0D332C2E1929711571335363751F20262322,5247464" +
            "16128=3B0B17232F:0F111800037039450D2971332C632026,1F2E2B38528327=3B0B17232F:0F111800" +
            "06032A0D700D332E011954838471152C202322,58477D630C=0814202C38:0F1118000106287129705B0" +
            "32C2E302F802D4E2B201F,528458384108=380814202C:0F11180001027039302971542F7526201E,634" +
            "72E151F583A=1E2A360612:0F1118000102030D70332C2E192971158384535426201E2322,471F1B=1F2" +
            "B370713:0F1118000102030D70332C2E195283845329711563261F0C20,4745752522=3505111D29:0F1" +
            "118000102030D70332E2C192971153953631F0C262720,5284612528=390915212D:0F11180001020370" +
            "0D332C2E192971152F4B49471F270C2322,52562B2029=390915212D:0F111800010203391929710D155" +
            "2838453,2075708A456309410F=0A16222E3A:0F111800010206032A0D09717029092D302F1575761320" +
            ",521F47251D=1F2B370713:0F1118000102111A1703154F2C2E382D2F807566,7163708A1F207D2A=051" +
            "11D2935:0F111800017C5C2C2E7129,527015382021=2B3707131F:0F11185C0370332D1523225283846" +
            "36626271E,2F292C2E1F00010601=2430000C18:0F11185C0001092A0D7014692983847B7C2C2E302F80" +
            "2D2B,06454F208A2E=0D19253101:0F11181200171A7919547638,5215201D09=3A0A16222E:0F1A1716" +
            "007015713F261F2720,5263587D2B470304=111D293505:0F1A0070153871291F20,7A7629=010D19253" +
            "1:0F181179005B712980152D4E2A0D533358,5270208A11=0814202C38:0F181138171A7975665B52845" +
            "415,47701F8A2013=121E2A3606:0F181117795B5C007054292A0D690403332D2C2E66632B3D,8A454F3" +
            "822=121E2A3606:0F1811705200012E71291F20,382A=16222E0A3A:0F1811705200012E71291F20,062" +
            "B27=14202C0838:0F18117052000171291E20,2E1F27=16222E0A3A:0F18117000012E71291F20,527A0" +
            "6=111D290535:0F1811700001062E2F1F20,712912=14202C3808:0F181100062839707952542C2E302F" +
            "03565A7566441F1E,0D29802B2029=1824300C00:0F181100012C2E7129,522025=121E2A0636:0F1811" +
            "0001261F20,03522E=0915212D39:0F18110001702C2E7129,6F454F098A2025=030F1B2733:0F181100" +
            "01702C2E71291F0D2B152F2127,5283162014=16222E3A0A:0F18110001707B7C0D7129,52565A152B20" +
            "34=17232F3B0B:0F1811000104037115454F7677657B7C392023222726210C,52092E1F27=3707131F2B" +
            ":0F181100010603797B7C802D302F2B6743441F202322,2952477D2528=14202C0838:0F181100017B7C" +
            "2E71291F20,036F33=0D19253101:0F18110001027939706954528384685D15565A75201E1D26,29032E" +
            "11=182430000C:0F1811000102062A0D2C2D804B2B672E2F7129,70471F8A2030=17232F3B0B:0F5C707" +
            "971292C2E0E032A0D6A79804B2D8C2B3348634C,52110915462031=15212D3909:0F5C5B0001032A0D70" +
            "52842C2E71291F20,1118517D462B=0F1B273303:0F5C111800015B712952841F20,756A251A=2733030" +
            "F1B:1545332C2E2F84836375662620,0F0003700D71292B1C=0E1A320226:1516291211020056,063820" +
            "07=000C182430:1551000403706A454F3A3D771F262322271E1D21,382B41522016=17232F3B0B:15004" +
            "43626271F1E,29710F47380D19520337=182430000C:150001021745512E443D65262322,2B63387C18=" +
            "192531010D:151A83842627202322,580F7003632E1F297C26=0E1A263202:15391A302F838454756626" +
            "27201E,0F702E4629004708=3606121E2A:5B000102073911522C302F3A678C363F33490D482425200C1" +
            "E2322,0F15382E1F6116=1E2A360612:5B71297000010611182A0D39792C2E332D4E712980152C1F2026" +
            "21,52454F3804=2C38081420:5B11180001020328700D332C2E195283847115632F751F2720,290F4766" +
            "30=0C18243000:201E27262322,8902=3404101C28:2A0D11180F52848353037039156358332C2E,3820" +
            "002628=010D192531:4089,030F565A61206B27=1824300C00:4089,8836=1C28340410:0370833F0F6A" +
            "5215,010D582E1F202C2F582938=112935051D:03700F,79192C2E2D715275262322271F201D217936=1" +
            "12935051D:0370110F45510D3371290941614C522623222720,8D3B=152D390921:03047039171A53385" +
            "2443D363F,8D11=0F1B273303:030402111A16175B4F3A2B153E0079015D5452848369026A51,7006200" +
            "F05=0F1B270333:03041A174533302F56795B3E808339528454,700F292026=121E2A3606:037B7C2E2F" +
            "261F20,0F14=1E2A360612:030270170F45513A2C7129528384702A0D532D2C24252623222720,155A38" +
            "2E1F2F=1B2733030F:03027011170D332D2C2E2F716152838454,010F201F2C=121E2A3606:030270394" +
            "50D332C2F2D2971528384636626202322,581535=212D390915:03020E0F18110D332C2E2D2F4971293E" +
            "615244756653,8A202531=1B2733030F:030102703945802D2C512B7129092322270C7566,112E528325" +
            "=2D39091521:030102062C2E543E3D636679,380D19462971001F=293505111D:03111A171538193E3F," +
            "0F632C2E70454F200C19=17232F3B0B:031A2B7915656A,0F177001204529710D632E2F02=32020E1A26" +
            ":033945302F838475262720,297071000F2E1F3810=17232F3B0B:0339332C2E1575201E26,0F520D631" +
            "F29712A72473826=390915212D:0339332C2E302B66201D1F27,0D2971010015520F6B0E=15212D3909:" +
            "03392D2E332F211D201F1E27,0F7015380029710D195824=16223A0A2E:036F791E20,522E1F31=1D293" +
            "50511:5283845B79037B7C802D2C2E4E302F2B38493D4463664C1F2021,0F0D712917=15212D3909:528" +
            "3845303702971150D2F,388A6A6D0F2012=111D293505:528384530370331929272E2B2F631F1D20,0F1" +
            "56B380E=0D19253101:528384530339454F0D297115332E2F637520,0F00705802=2A3606121E:528384" +
            "530339332E152C2F58631F20,380D000F2900=283404101C:528384530003010215392C20,1112180F29" +
            "560D2E1F754511=15212D3909:5283845300031929150D332C2E63,0F217045208A717521=3505111D29" +
            ":5283845300010670528384802D2C2E4E155B201F1E232221,380F71296A0E=17232F3B0B:5283845354" +
            "037029711575262720,631F58000F2E38010D=111D293505:528384000103451915332C2E631F2720,29" +
            "716A0D0F7019=1D29350511:5283840001032E1570637566302F391F,0F4729712030=16222E3A0A:528" +
            "3845479036A2627201E,0F380D70297115012F1A=1F2B370713:528384542E03700F1118705469565A75" +
            "66631F1E2021,297138000C31=121E2A3606:52838454443D65002C2E15495D1F,0F417D712B38630F=0" +
            "D19253101:5283845444360F11756415,2C2F29016B472E2B20381D=212D390915:52838454536300010" +
            "3332E15,0F1F197029710D757D2032=121E2A3606:528384546315332C2E2F26201F2322,0F0D4500297" +
            "1756B17=192531010D:52838454754C2971150301022E,0F63206A0938268A4117=1B2733030F:528483" +
            "53000103297115332E2F19,0F8A514F6A6620754526=1824300C00:528403395B2F1E20,0F012D=0B172" +
            "32F3B:5254700001020612692D4E584647336375662E1F1E,71290D262037=131F2B3707:525400045B1" +
            "7791A565D754C7866,2E1F207C34=0F2733031B:483F89,8838=232F3B0B17:767779392623222789,15" +
            "2B1F1D200E=0A16222E3A:767789,528300292025=14202C3808:7665261F20,0F291A=222E3A0A16:76" +
            "65262322271F201E21,0F0029807124=1824000C30:7889,292E1F24=101C283404:8D,8832=1D293505" +
            "11:63767789,522E0006206B31=131F2B3707:7B7C343589,0F7038=2632020E1A:7B7C343589,520F20" +
            "=0E1A260232:7B34,8812=1C28340410:02703918110F7919155283756626232227201E,012C2E1F0C29" +
            "=121E2A3606:020F11161A17454F2C2E2D302F2B38434C,2070016328=1824300C00:02060418110D332" +
            "C2E415B637566262322271F20,520F23=142038082C:07504089,0F010C=15212D3909:07262723221F4" +
            "0,0F7129523B=2430000C18:0717363F1A2C4F3A67433D8B,71290F0103471A=2531010D19:070403111" +
            "8528384542D2E4E49201F1E1D2127,292B000C3B=283404101C:073F7765644889,012014=111D293505" +
            ":074048261F202322,0F71454F1500018008=111D293505:07404826271F1E2089,882C=0D19253101:0" +
            "7565A5283845463756677261F20,010F15296120=2F3B0B1723:07487677393F89,0F2952151F1D30=11" +
            "1D293505:074889,06520F3808=17232F3B0B:074889,883B=131F2B3707:074889,8832=15212D3909:" +
            "07762623221F1E20,000F1552296B2F2A=0D19253101:0776776A742623221F200C211D1E,11180F2F52" +
            "06802B0B=04101C2834:0776776564,000F29382011=101C283404:0706397B7C794C636A48,520F7129" +
            "472026=14202C3808:077C343589,880A=380814202C:076A79040363660F5D363F,52292E1F20382F15" +
            "560123=16223A0A2E:076A696819,0F2918=222E3A0A16:076A171552847983546578,712970010F2D=1" +
            "82430000C:076A48,45752F29384C0F204F612B30=131F2B3707:076A7626271F1E20,0D0F29382F2E0E" +
            "=0814202C38:07343589,065238=1C28340410:070039201F0C2789,06030F292F23=101C280434:0765" +
            "64,0F292002=0D19253101:073918111A17332C2E71292322271F1E20481D45548384,38002F702A=182" +
            "4300C00:7C343589,8801=172F3B0B23:6A79363F65,0F292B7118=1B2733030F:6A170F19,5845754C2" +
            "01F4F382430=1B2733030F:6A170F1963766F,5452201F32=0C18243000:6A0339332C20528384531563" +
            ",29713801000F0C47806B3B=2A3606121E:77766564000789,0F52201E8A01=202C380814:1F20272600" +
            "76232289,0F29528339=0F1B330327:3435,8809=0F1B273303:34357B7C,8818=121E2A3606:34357B7" +
            "C7789,0F291D=232F3B0B17:34357B7C89,0F2021=33030F1B27:34357B7C89,030F27=390915212D:34" +
            "357B7C89,712917=1D29350511:3435073989,8802=2C38081420:34357C89,0111180F292006=30000C" +
            "1824:34357C89,71291A=14202C3808:34357C89,8A2036=182430000C:3435000789,8835=232F3B0B1" +
            "7:34350089,0F2025=3707131F2B:34353989,0F2037=0D25310119:343589,0F52202D=0F1B273303:3" +
            "43589,0F7152290D=131F2B3707:343589,8830=121E2A3606:343589,881C=16222E3A0A:343589,881" +
            "9=131F2B3707:343589,880F=15212D3909:343589,8832=14202C3808:343589,8813=0D19253101:34" +
            "3589,8811=17232F3B0B:343589,881E=142C380820:017018110F1A2E15495247838463462322271F,8" +
            "D03=0F1B270333:0103040818111A155284262322271E20217A79708330,38472E631B=14202C3808:01" +
            "0670170F0E3A294152838454262322271F201E,2E1815442C=0F1B273303:01067071292C2E1F20,1103" +
            "150F520A=17232F0B3B:010670181126271F202165,293816=182430000C:0106111839513A2C2E2D2F8" +
            "C804B4723221F63,7152292037=0F2733031B:010203040618110F3315292A271D200C6339171A712C2E" +
            "30491E21,7A21=0E1A260232:010206040318110F2E292A27200C70072C302F541F392B49,381512=1A2" +
            "632020E:010206110F452C2E7129095B5226232227201F0C,58804B036B2B381C=142C380820:0102391" +
            "8112E2D493E52756624262322271F20,8D12=121E2A3606:008354,06462F2E1F27=030F1B2733:00797" +
            "084831754,0F2E472D4E1F06=0D19250131:0079701811072C2E01060F33152627200C7A1A302F457663" +
            "1F2B,8052382900=172F3B0B23:00790F072C2E0103047018111A262322271E7A302F5448637545,2938" +
            "15561E=101C340428:007952151E20,0F2E1F33=0F1B273303:007984831A160F1719,632E20471D6B01" +
            "=152D390921:0079110F0304062A528423222627207A19701A2C2E2F5D83,294513=0F1B273303:00791" +
            "81A165B332F2B262322271E2021030469702D4E49712930845D,454F05=152139092D:0079192E2F0304" +
            "17332D1552847A5D,4E201F=162E3A0A22:003826232277,632E20523A=0D19310125:0038262389,521" +
            "513=1C28340410:00384089,0F202E157C07=04101C2834:00384089,152967631F=101C283404:00384" +
            "740,0F2037=1C28340410:00387765504089,0F157C04=131F37072B:00385476,521F13=16222E3A0A:" +
            "003854767789,2E1F522010=131F2B3707:003854637519,205D1D1F52151E210F=121E2A3606:003889" +
            ",52201F1D4733=121E2A3606:003889,881F=212D390915:001D23221E2789,52290F2E1F202B=07131F" +
            "2B37:002C7080305C784C62,2E1F472001=283404101C:004D64547589,0F292E=131F2B3707:005040," +
            "522E1F0F2C2004=3404101C28:005089,032C2E1F33=182430000C:005089,8815=192531010D:00261F" +
            "23221E201D2189,8D12=131F2B3707:00261F2322271E200C89,8D1E=121E2A3606:0026271E20,2F2E1" +
            "F33=16222E3A0A:002627241F1E20232289,8D33=14202C3808:002627651E2027232289,881B=182430" +
            "000C:00262789,292C2E1F2B2F2A=07131F2B37:00262322271F1E203F8B65,52290F038002=15212D39" +
            "09:001779332D2322271E2007760304,38290F1C=1F2B370713:00173883546365756619,466115201F7" +
            "01D47522434=0D25310119:00170F79191A6540,712909387C2015=0E1A263202:00170F332C2E2D2F80" +
            "2952443F26232227201F,15637C383A=132B37071F:00170F7665776489,8D2A=390915212D:00177689" +
            ",0F52804F2507=2E3A0A1622:00177179546A76,0F52443D1F2D=0915212D39:0070,0F292C2E791F13=" +
            "131F2B3707:007083624C,0F38202E7D4F45471F7107=380814202C:00704F0D332C2E2D15363F261F20" +
            "274C,0F2906036F4703=3404101C28:00702C2E164C157126271F1E202425363F,29386A032B0F=0F1B2" +
            "73303:00700F1715262720,472E386309=15212D0939:007022230726,2E17712952302F15=15212D390" +
            "9:00704889,8834=1C28340410:0070784889,0345201F21=2D39091521:007007482089,2E1F58470B=" +
            "0D19253101:0070071A010618110F5B52846775,6326202E=16222E3A0A:00701A17794C0F302F715475" +
            ",2E454F8A20243A=0F1B330327:007018111A1617192E15382627201F656477,4F090A=0F1B273303:00" +
            "2E2F18110F5B3315292A26271F20210C7A70710102393E19,035A37=14202C3808:002E4344793F26271" +
            "F20,03702C2F292B381A31=0E1A263202:00161A5D454F153826201E27,7D0D2904=152139092D:00040" +
            "37039180F332D152952262322271F0C533A83,4117804735=1F2B370713:0004037B7C0F794947667546" +
            "67,80293869208A1E=162E3A0A22:00040301067018111A0F332C15292A261E200C7A791970712F5D528" +
            "38454,5617454F06=3404101C28:000403110F527079156523221E2027,0129802E1F6B1D=1830000C24" +
            ":0004031A170F11332C2E302F1571292A657677451949,70201D5218=102834041C:0004031811171A5B" +
            "332C2E155D52,0D29204504=17233B0B2F:00040318110F1519262322271E2021,52831F3825=3B0B172" +
            "32F:00046A7966444C7765,010C202F38520F70292E31=14202C3808:003F261F202789,8836=131F2B3" +
            "707:003F657789,7152290F032B3A=2632020E1A:003F651F0C2027232289,0F292B=16222E3A0A:003F" +
            "89,8836=212D390915:000F76,032E1F522C292B22=2B3707131F:000F7765,2E1F7C4607=0F1B273303" +
            ":000F01111A1615292A2627200C2C670279538384543E49,634512=0F1B273303:000F1320,638038293" +
            "6=0F2733031B:000F1323222627,2E3829031535=0D25310119:00676589,0F200F=0C18243000:00401" +
            "D232289,71290F47202B=101C283404:0040395089,8803=30000C1824:004023222089,0F291118470D" +
            "=0A16222E3A:004089,0F5211=1A2632020E:004089,0F0147200B=3A0A16222E:00037039454F0D3329" +
            "71152C4C48,090F476341382E0A=111D293505:00037039041A26271F1E202322,0F2F2C335129452E0D" +
            "3A3B=222E3A0A16:000370396A450D332F4B154C,0F208A7D41381F2E14=0F1B273303:00030401061A1" +
            "6170F332E71292627200C02696A45514F0D2C2D4E497A,2B0B=0F1B273303:000304111A33152D2E302F" +
            "71292A5284530770022B,0F6345203B=0F1B330327:00030418111617332E2D2F292A52845407020D302" +
            "B,090F452001=0F1B273303:000304080618110F1A2E2D0D3371292A2C302F7566010239454E802B,632" +
            "039=2430000C18:00036A7415384878,45751F20240F522E834F2E=182430000C:000301394F2E154763" +
            "751F27,0F707A802629710D192035=14202C3808:0003391983845475,2E1F0F6A702971722A0D04=0F1" +
            "B270333:00483F,6338200F2A=3B0B17232F:00481F2023221E27262189,0F292C2E1B=122A36061E:00" +
            "76645089,8819=202C380814:0076777566262322271F201E,0F111852290D=101C283404:00763989,0" +
            "F2036=1E2A360612:00788B89,0671292E25=010D192531:00784C00793989,0F29702E1F208A21=3101" +
            "0D1925:0006261F1E201D212322,0F2938111801=2A3606121E:00060403702C2E4C154947443D651F,0" +
            "D2920=101C283404:0006522E261F20,0F712939=2632020E1A:00060724232227261F2025,520F15792" +
            "9382F22=31010D1925:0006547677,0F5229151F201B=0E1A320226:00061A161718110F292A0C26271F" +
            "212A79700102212F49,470D=0814202C38:002876396577261F20,5283290F37=212D390915:00283979" +
            "76771E232227,0F522E47442027=121E2A3606:006389,8822=101C280434:007B7C3989,881E=183000" +
            "0C24:007B343589,8805=2E3A0A1622:00021719792B155D5466774962,010611180F292030=14202C38" +
            "08:00020370454F0D3933192C2E2D156375261F202322,0F7123=0E1A260232:0002070818111A16175B" +
            "153E445D5452848365647576,2038454F15=182430000C:0007385476771548,52061F2024=2D3909152" +
            "1:0007504089,0F29157030=15212D3909:0007504089,060F71702F2918=15212D3909:0007504089,8" +
            "80B=17232F0B3B:000770171989,0F2E20382F=0B17232F3B:00077089,522E1F8A202C=07131F2B37:0" +
            "00704036939487C4466,0F7011293821=1824000C30:000715547776,521F18=0E2632021A:000703040" +
            "1021811171A0F2E2322271F1E706749528483,202F293800=0F1B330327:00077663,0F297138202C=0B" +
            "17232F3B:000776776548,0F1118152E1F2017=121E2A3606:00077665776489,52830F208A14=1A2632" +
            "020E:00077B7C4834353989,2952203B=2632020E1A:00076A386563,0F7D8A2066454F52754C15=1E2A" +
            "360612:00076A0F3874485040,06707C2509=3606121E2A:00076A74504089,5229702C7D15=14202C38" +
            "08:00076A74173926271F1E20,0F7029522B09=000C182430:00076A54196348767765,7920297115528" +
            "A0D382B16=101C283404:000734357B7C3989,0F528329200C=06121E2A36:0007343589,290F7104=2E" +
            "3A0A1622:0007343589,0F292F702012=182430000C:0007343589,0F71296B708003=15212D3909:000" +
            "7343589,7129706300=0D19310125:0007010618111A332D302F15262322271E530270164C,560F71292" +
            "4=0E1A263202:000701020618111A175284835407230C7027,262038292C=111D293505:0007711F2048" +
            "40,010F29153814=17232F3B0B:00076527262322,1552835A201D0F382D=0D19253101:0007363F8B39" +
            "89,09292C208A0F28=030F1B2733:000739483F66,0F208A2B0A=04101C2834:0007397B7C343589,010" +
            "6522008=020E1A2632:0007396A48343589,0F203A=283404101C:00073934357B7C89,0F5223=350511" +
            "1D29:000739343589,032010=0A16222E3A:000739343589,520F2F=111D293505:000739343589,8A20" +
            "0A=15212D0939:00077A7089,8817=17232F3B0B:000789,8D3B=172F3B0B23:000789,8815=1B273303" +
            "0F:007C343589,881B=212D390915:007C343589,8812=15212D3909:006A79190F6F2627,6B46204538" +
            "290B=380814202C:006A38075040,0F630141202B454F2D=121E2A3606:006A5040077448,702B2C0F2F" +
            "292E=0B17232F3B:006A583F232227261F20,0F291547031C=232F3B0B17:006A6F391974,0F2E614447" +
            "702C292F71201F38521F=31010D1925:0034353989,522E1F2B=0D19253101:00343589,060F5200=2A3" +
            "606121E:00343589,7129565A01=131F2B3707:00343589,883B=111D350529:00343589,8800=152D39" +
            "0921:000150402627,0F292F2B1E=2733030F1B:00010F17505840,565A80385283846315=101C283404" +
            ":000103020611187B7C2D4E616439201E0C26,522E474429=101C283404:0001030239450D297115332C" +
            "2E4C,0F542070528438632C=101C283404:000103392E54837548,19700F58157A20381F=1830000C24:" +
            "00010670175B71292A152322271E,03637C2B380F=0E1A263202:0001067052842E71291F20,030F3847" +
            "7533=131F2B3707:0001067011185B0D332C2E2D712909262322271F200C,0F5263250C=17232F0B3B:0" +
            "00106040318111A170F33292A26276A201D0C7A71077C1F1E74694F,520A=0D19253101:000106040323" +
            "2226380F767754,568020152D=111D293505:000106025B7571295B04032D302F382B2A0D801E20,2E1F" +
            "0F0F0C=0D19253101:00010607155B5C26271E2021165D83,38470F2920=16222E3A0A:0001060730181" +
            "10F3329271E0C7A0D75,3826201508=0F1B273303:00010618111A16332C2E2F2D27200C07483A450D,1" +
            "552843825=0E1A263202:000102261E2027,03476F700F2971382E39=15212D3909:0001027007834878" +
            ",2E388A201D17=131F2B3707:00010203450D3329152C2E2F5375,0F638A6A1D8A382D=0E1A263202:00" +
            "0102030D70332C2E29712F534426201F1E,0F38152F=121E2A3606:0001020370450D332C2E2D152971," +
            "0F52838A201D1B=1D29350511:0001020370528384631575712D2E4E3E581F1E1D,292C2B452620803A=" +
            "222E3A0A16:0001020370392F2971152B54754C,458A1F0F20462C=14202C3808:0001020370392F8071" +
            "2B546675201E26,1F58472E152F=16222E3A0A:000102037039714515750D33,201D381F092E0F1103=3" +
            "2020E1A26:000102030F7039453319152E2D2F63751F0C1E20,71290D38472C=16222E3A0A:000102035" +
            "270392E2D5863,0F381D2B2921201511=131F2B3707:0001020352666A,0F7020262938172F3A=243000" +
            "0C18:00010203332C2E2F1558631F,0F1920707A2971264627=05111D2935:0001020311180F702E1F79" +
            "52838468332D6749443E46630C1E1D21,292B2035=1C28340410:000102031118396375664819,1D4138" +
            "702080291F=232F3B0B17:000102033945332C6375201D21,0F1929710D702D=101C283404:000102033" +
            "90D3329152C2B751E20,2E1F54475352458316=111D293505:0001020339161745514F2C190F1A16152E" +
            "2D2F304979,8D13=17232F3B0B:00010203396A79637566201D211E,29387D71707A30=101C283404:00" +
            "0102033911170D3319152E2F0947442627201F,8D25=3505111D29:000102031811392E2D19528384543" +
            "E4463751F20,152F1A290F0D=0E1A263202:0001020626232227201E,0F2E03801F0F=101C283404:000" +
            "1020617385483,030F47202B6B1B=2733030F1B:000102060F17705283797823221E2027,2E712910=12" +
            "1E2A3606:000102062A397129797B7C2E1F2425,162F5D20262B=182430000C:0001020603691817452C" +
            "2E2D498344,412B6A09633808=3A0A16222E:0001020603700F7B7C2E1F692D48302F565A586366240C2" +
            "1,2B151A292039=17232F3B0B:000102060717706A33392D2E4E674447482322271E210C,71292B4F202" +
            "3=33030F1B27:0001020607036A5D397C7C2163664744,0F4E25208A08=04101C2834:00010206077526" +
            "1F20,71290F70150C=101C283404:00010206111803302F565A802D4E2B881F261E0C,0D0F521B=16222" +
            "E3A0A:00010206090D5B7952838454685D7B7C443D77656366201F1E,030F47454F24=010D192531:000" +
            "102071283542627201D210C4C78,29580F2E6352032E1F01=32020E1A26:00010275261E0C2322,63037" +
            "06F0F292E1F19=0E2632021A:000102081A158483262322270C1E,700F292E1B=101C283404:00011A16" +
            "15262322271F1E200C214C,472B0F1124=3707131F2B:00013974150726271F1E200C,0F06520D297170" +
            "382B4507=17233B0B2F:000118111A16175B154C26271E200C232279302F5D528384547543,0F297C7A0" +
            "3=17232F3B0B:000118111A332C2E2D1571292A2627200C7A1979,387C02=172F3B0B23:000118111A33" +
            "2C2E2D1571292A23222627200C7A791970302F5D5283845456,387C454F1F=0E1A263202:00010818111" +
            "71A160F1571292A26271E20396476452B0D,632E523813=15212D3909:00211D1E232289,8D16=0E2632" +
            "021A:006526232227201F,8926=05111D2935:00657689,6B0F5225=16223A0A2E:00654C89,8D03=2A3" +
            "606121E:006589,2970472008=15212D3909:001A170F5B332E2D7129261E203E5D,1503528306=15213" +
            "9092D:001A170F1379232227761926,71293833=1C28340410:001A1715838444363F261F1E200C2322," +
            "0F476B52036338=14202C3808:001A2B5448701938754C,152E20242510=0D19253101:0039504089,8D" +
            "39=283404101C:003926271E20747677642322480C06,2E1F38=0F1B273303:0039262322271E201D210" +
            "C0748766465776A,150F382939=202C380814:0039332C2E2D2F152B4644261F1E,0F7019382971637A3" +
            "1=192531010D:0039787989,1F2E2010=101C283404:0039787089,2E1F8A034F206B29=05111D2935:0" +
            "0398B7989,0F200C=131F2B3707:0039077426271F1E20,0F29713852832B632D=14202C3808:0039076" +
            "A7426271F2048,0F79197029717A382C=0E1A263202:00397C343548,8929=3B0B17232F:003934357B7" +
            "C89,0F2028=16222E0A3A:0039343589,8D34=16222E3A0A:0039343589,880B=111D293505:00393435" +
            "89,8805=17233B0B2F:0039343589,882E=101C283404:0039343589,8806=17233B0B2F:00390103040" +
            "618111A17332C2E262322271E157A7071302F45631F2075,807C2B=0915212D39:00396577647969271E" +
            "2322,52012E1F2620612D=16222E3A0A:00391A6A15384C4943363F7448,0F0379472B6319=192531010" +
            "D:00394C786F89,0F2E442035=182430000C:003989,882A=121E2A3606:003989,8816=13191F252B31" +
            "3701070D:003989,8801=0D19310125:003989,880D=0F1B273303:0018112C2E01040607332D292A092" +
            "70C2322696870302F47023945,382052801C=101C340428:00190F153917701A48,472E1F200334=1F2B" +
            "370713:00195475667689,5229152E2019=222E3A0A16:004C504089,0F5215470A=3A0A16222E:005C7" +
            "02C2F802B154C78,5A562E1F208A45466319=102834041C:0089,090F1538=131F2B3707:71297C79000" +
            "1062A710F802D,5215705D2F=0E1A263202:7100030170391959152E2D2F2B39,0F201F4F75668A3824=" +
            "030F1B2733:5483846376656419786A,298030201A=2430000C18:5452838479195D00012A0D7B7C2C2E" +
            "3348156366242526201E,0F71292D=07131F2B37:54528384700001020339482D301571565A363F63756" +
            "6,06292B201F8A29=030F1B2733:54528384036F796A153E65,7129631D=2733030F1B:5452848303152" +
            "F802C2D,2E1F208A7A700F29710C7D22=33030F1B27:118384155B20272E1F21,0F03380E=0E1A263202" +
            ":1179302F842627201E,0071292E1F0E=06121E2A36:11177B7C52842C2E5B1F20,060071292F0F0E=10" +
            "1C283404:110F70528475660D7129,012E1F20262A=101C283404:110F03706A795215636626271E,0C0" +
            "12F38062C292B07=020E1A2632:110F0001702C2E7129201F,52060C=0E1A263202:110F00017052792E" +
            "1F1E,71290D2B2020=293505111D:110F1A6A702C2E1952838453712F6375,45201500011D=101C34042" +
            "8:11037B7C2E2F7129,0F52200B=0E1A263202:11000170792C2E7129,0F52201F01=111D350529:1100" +
            "01527B7C2E75,0F2009=04101C2834:1100010206702D804E2B2620,0F52540D00=131F2B3707:110001" +
            "392E1F20,0F712932=17232F3B0B:11715452838454292C2E302D4E092A0D50407970443D,5680410023" +
            "=2B3707131F:111879690001020370396A2E2D528384543E637566,0F380D580F292000=222E3A0A16:1" +
            "11879076A1A171523221E27207924,5229700F1D012E292B0C2F0B=06121E2A36:111817000106702C2E" +
            "71292A0D33802D302F4E2B44,0F52252029=07131F2B37:11180F000704030D7C684580302F153867534" +
            "775,70204119=2430000C18:11180F00012A0D70795D7B7C39332D2C2E4E4863664C,064F478A2037=1E" +
            "2A360612:11180F000152548471702C2E2D4E303348492A156144474C63,8A201F38450618=202C38081" +
            "4:11180F000128032A0D7129302C2E302F2D802B09411F1E20,5284543824=2F3B0B1723:11180F00010" +
            "20370391952845329712B632E7B7C792D2C8020,385D151E=293505111D:11180F0001020339700D2971" +
            "6375662E1F2620,3815568016=16222E3A0A:11180F000102587B7C5283847971302F804B2B497675,09" +
            "612E1F201E=232F3B0B17:11180F00010E715229702E79692C2E2D2B15093954444C6666,2F565A80613" +
            "2=131F2B3707:11180F71297052838454792A0D33802D153853201F1E212627,012F56476628=3707131" +
            "F2B:11180F71297000010604032A0D793969302F33802D636675,201F52565A1E18=1D29350511:11180" +
            "F5C000102030D332C2E195329711563261F202322,52843A=202C380814:11180370392A0D3329712C2F" +
            "156375795B5D,450C8A00382E1F20010C=3A0A16222E:11185283847975661271393D692D15565A201E2" +
            "62322,292F060D0C02=30000C1824:111852838470795B302F404533802D152B39201E23221D212726,0" +
            "F2E1F010D2923=2D39091521:111852838453546319297115030D332B2C,060F8A2E38201F38=0D19253" +
            "101:111800020D041A796933483E5347446563751F1D212026,010F09150C17=2430000C18:111800071" +
            "7161A2C2E3371292B56433D6375363F,0F010347208A09=020E1A2632:111800012A0D2C705271292E20" +
            "1F,1538617904=30000C1824:11180001032A0D70795B2C2E302F802D4E152B33714161201F26,520958" +
            "470A=000C182430:11180001020439332C2E302F2B5844477515634C1F2721,0F520D19267A297170203" +
            "7=232F3B0B17:111800010206037939695483845D2D2E4E446375661F262120,0F52290D7123=31010D1" +
            "925:111800010206071979697C67474475664C,0F16298A2014=182430000C:11187129705B790001060" +
            "32A0D397B6F7C802D2C2B61756627261E0C1D21,0F2E15414732=192531010D:11187154528384297939" +
            "7B7C69152B2A0D3348295324251F1D1E26,6B00702F800C201E=1F2B370713:5D0007363F232227261E2" +
            "1,037C0F471F202E=0E1A263202:6526232227201F,880E=111D293505:653989,8806=131F2B3707:36" +
            "3F6526232227201E89,8832=1A2632020E:1A454F548384,881D=121E2A3606:1A38712975,0F201A=0E" +
            "1A263202:1A162623227954,0001710F290C=0F1B273303:1A16170F13152654,3852204F32=0F1B2733" +
            "03:1A5D453A332C2E2F4B25262322271F201E1D21,000F704723=2F3B0B1723:3950177089,522E1F0F2" +
            "01A=1D29350511:39701117302F713819297566,004551152C2E201D1F34=121E2A3606:393589,881A=" +
            "15212D3909:393589,882C=182430000C:393589,8825=101C283404:393589,881C=2531010D19:3940" +
            "89,71294709636F7C440D=0D19253101:3948007889,8D38=2430000C18:394889,8811=111D293505:3" +
            "94889,882A=0E1A263202:3907,8807=0D19253101:39343589,8831=101C283404:393489,8801=222E" +
            "3A0A16:390050404C89,0F528329692018=131F2B3707:39006A26201F,0F520D38580629712B09=3808" +
            "14202C:390001022C2E302F1575804B2D261F20,0D0F0319707D5229717A15=17232F3B0B:3989,8D11=" +
            "0A16222E3A:181179838454637566,0F5229012007=111D293505:18117915384C,52200E=0C18243000" +
            ":1811795B032C2E302F802D4163754C27261E1D2120,010D0F29521F29=16222E0A3A:1811795B5466,0" +
            "1202F=192531010D:181179000607040D03302F5283844F3A45512B1533664C47,090F702E208A2B=0B1" +
            "7232F3B:18117900012C2E5B1F20,0F710D52291A=122A36061E:181179190E332C2E2D5263756626232" +
            "2271F20,8D02=0F1B273303:181117332C2E1526232227201F1E3E,38030F522922=142038082C:18117" +
            "0792C2F7129,52201F=121E36062A:18117001061579,71292023=121E2A3606:18117000012C2E7129," +
            "522024=3505111D29:18110F390001020370390D3329711563752E1F0C201D,38525D1A=101C283404:1" +
            "8110F197983842E230C271F1E7A70525463,2620291503=111D293505:1811002E1F8384,0F2022=1824" +
            "000C30:181100012C2E2F1F,0F3821=142038082C:181100012C2E2F1F20,0F5229=14202C3808:18110" +
            "0015B3875,2E2034=15212D3909:181100012A0D2C2E2F2B2D302F4E447129841F,0F09416138200F=08" +
            "14202C38:181100012A0D52842953411E20,2E1F0F47152F=131F2B3707:18110001032A0D845B712930" +
            "2F791533536678,0F208A1F1D33=17232F3B0B:18115452840001712970802D2C2E302F2B2A0D78791F," +
            "0F204758610E=0F1B273303:18111A16175B3315262322271F1E201D215D838454433E363F754551,000" +
            "30F290D=0C18243000:18115C0001702A2C2E2F5283847129795B6375802D154C,1F208A2407=15212D3" +
            "909:88,262052830D=17232F3B0B:88,8D17=102834041C:88,8D0B=15212D0939:88,8D24=121E2A063" +
            "6:88,8D09=17232F0B3B:88,8D13=111D293505:1979,3F2F2E45207D37=112935051D:1966583F6589," +
            "8831=16222E3A0A:4C4089,880C=0C18243000:4C78,297172380D2A2E0F47484112=16222E3A0A:5C0F" +
            "1811790070528471291F20,2F0380512514=1C28340410:5C0001020652835B0E03804B2D4E2B7520242" +
            "10C06,292E565A36=1A2632020E:5C11180001027170520D298483292B15200C,03802E386333=15212D" +
            "3909:89,6B34=111D293505:89,8D";
    /**
     * 时辰宜忌数据
     */
    private static final String TIME_YI_JI = "0D28=,2C2E2128=,2C2E0110=,2C2E0C1F=,2C2E7A701B1C=,0" +
            "1022308=,01026D003026=,000106037A702D02=,000106037A702802=,000106037A703131=,0001060" +
            "37A70341B=,000106087A701F0E=,000106087A702E15=,000106087A702C2E0E39=,000106087A702C2" +
            "E0D2B=,881727=,88032D=,88352F=,882B2F=,882125=,882A22=,880C1E=,880220=,88161A=,88201" +
            "8=,883422=,880113=,880B11=,883315=,882915=,881F17=,88150D=,88122E=,88302A=,88262A=,8" +
            "83A28=,880826=,881C2C=,881905=,882303=,880F09=,88050B=,883701=,882D01=,88060C=,88241" +
            "0=,881A12=,882E0E=,88380E=,881010=,883630=,881834=,880E38=,882232=,882C30=,88043A=,8" +
            "81E0A=,880006=,883208=,880A04=,881400=,882808=,883137=,883B35=,882737=,881D39=,88133" +
            "B=,880933=,88251D=,882F1B=,881B1F=,88111D=,880719=,88391B=,88212D=,7A702C0B15=,7A705" +
            "51515=,7A70552D00=,7A7D2C2E1334=382C,000106083528=382C,7A70000106080504=382C7A6C5570" +
            "0F197120,00010608223A=380006082C,01026D0D2C=380006082C,01027A70551D30=380006082C0F71" +
            "295283,01027A703636=380006082C0F71295283,0102416D1226=380006082C7A706C550F297120,010" +
            "2251C=380006082C7A6C55700F197120,01026D2300=3800010608,2C2E0324=3800010608,7A702C2E0" +
            "82E=3800010608,7A70552C2E3B34=38000106082C,2F8026330C=38000106082C,2F80267A701622=38" +
            "000106082C7A70556C0F197120,1904=38000106082C7A6C55700F197120,1514=38000106087A70556C" +
            "0F197120,2C2E3138=38000106087A70556C0F197120,2C2E0B10=38000106087A6C55700F197120,2C2" +
            "E2B28=387A6C55700F197120,000106082C2E2E16=38082C,000106037A700E3A=38082C,000106037A7" +
            "03708=38082C6C550F197120,000106037A701B20=38082C6C550F197120,000106037A70111C=38082C" +
            "6C550F197120,000106037A703A2D=2C38,000106082733=2C38,000106081015=2C38020F71295283,0" +
            "00106083817=2C2920,7A700F03=2C2920,616D1839=2C292070556C100F,00010608161B=2C2920020F" +
            "7100010608,302B=2C2920556C0F1971,7A701E07=2C2920010F,1B1B=2C2920010670100F00,352B=2C" +
            "292000010206100F70,082B=2C292000010206100F707A,0C21=2C292000010870556C100F7A,0617=2C" +
            "29206C0F1971,7A70552807=2C29207A70556C0F197100010206,122F=2C29207A706C55100F1971,101" +
            "7=2C29207A706C55100F1971,2731=2C20,616D0436=2C2070550F,7A7D01022E12=2C200F71295283,0" +
            "1021831=2C20556C0F1971,7A702912=2C20100F52,01026D1D33=2C807138152952,000106080E31=2C" +
            "80713815295270556C100F,000106083201=2C80713815295270556C100F7A,000106080327=2C807138" +
            "15295202100F,000106037A702B2B=2C80713815295202100F,000106037A702801=2C80713815295202" +
            "100F,000106083639=2C80713815295202100F7A7055,00010608341D=2C807138152952556C100F,000" +
            "106037A701B23=2C807138152952010F6C55,7A70302D=2C8071381529520102100F7A7055,2231=2C80" +
            "71381529520102100F7A6C55,1F13=2C80713815295200010206100F20,7A70313B=2C8071381529526C" +
            "550F,000106037A701A15=2C8071381529527A70550F,000106080219=2C8071381529527A70556C0F19" +
            ",000106082E0D=2C80713815295208556C100F,000106037A70161F=2C80711529525670556C100F,000" +
            "106083813=2C80711529525670556C100F,000106082D05=2C807115295256020F7A706C55,2237=2C80" +
            "711529525602100F,000106081F0D=2C80711529525602100F55,000106037A702627=2C807115295256" +
            "0102100F7A706C,2C33=2C8071152952560102100F7A706C,0939=2C80711529525601100F7A7055,416" +
            "D021F=2C80711529525600010206100F70,0E37=2C80711529525600010870556C10,2129=2C80711529" +
            "52566C550F,7A702519=2C8071152952566C550F19,7A702417=2C8071152952566C55100F19,0001060" +
            "37A70043B=2C8071152952566C55100F19,000106037A700C1B=2C8071152952566C55100F19,7A703B3" +
            "1=2C8071152952566C100F19,7A705500010603172D=2C8071152952567A70550F,416D3A2F=2C807115" +
            "2952567A70556C100F,1901=2C8071152952567A706C55100F19,1119=2C8071152952567A6C55700F19" +
            ",1C2B=2C80711529525608556C100F,000106037A701403=2C80711529525608556C100F,000106037A7" +
            "0071D=2C80711529525608100F55,000106037A701908=292C20,7A7D01026D2E0F=292C200102100F7A" +
            "7055,032C=292C20000608,0102071C=292C206C550F1971,000106037A700E33=292C207A70556C0001" +
            "08,0503=2920550F,7A702C2E0721=2920556C100F,7A702C1225=2920000108556C100F,7A702C2E1F1" +
            "1=2900010870556C100F7A,032C201A11=297A70556C100F,032C200E35=297A70556C100F,032C20000" +
            "A=70556C0F197120,7A7D3A29=70556C100F2C20,000106081C25=70556C100F2C20,000106082805=70" +
            "556C100F2C20,000106082F20=70556C100F2C20,00010608150C=70556C100F29522002,7A7D0001060" +
            "33314=70556C100F,00010608032C20122A=70556C08,7A7D000106032415=70100F2C715220,0001060" +
            "81A0D=4B0F2C20,000106037A701902=4B0F2C20,000106080E3B=4B0F20,7A702C000106032E17=0F2C" +
            "09382920,7A7000010603363B=0F2C093829206C55,000106037A70082C=0F29528320,7A2C71707D010" +
            "26D0718=0F712952832C20,7A7D01021C26=0F712952832C20,7A7D01026D3918=0F712952832C203800" +
            "0608,01027A70552126=0F712952832C2010,01021330=0F712952832C207A7055,01021118=0F712952" +
            "832C207A7055,01023524=0F715220,7A70552C2E3419=20556C0F1971,7A702C2E1D31=200001020610" +
            "0F,7A702C1E05=0270290F2C207A,00010608212C=0270550F,00010608032C200C23=0270550F,00010" +
            "608032C203706=0270550F20,000106082C2E2520=0270550F20,7A7D000106032E13=0270550F202C80" +
            "7115295256,000106081620=020F29528320,000106087A2C71707D0112=020F2952832055,7A2C71707" +
            "D000106030F08=020F20,7A7055000106032A23=020F712952832C20,2521=020F712952832C20,00010" +
            "6082F21=020F712952832C20,000106080003=020F712952832C20,7A700432=020F712952832C203800" +
            "0106086C,7A701E03=020F712952832C2070556C10,000106081623=020F712952832C2001,2236=020F" +
            "712952832C2001,000B=020F712952832C2001,7A70552C36=020F712952832C20013800,416D341E=02" +
            "0F712952832C20017055,7A7D0E32=020F712952832C200110,7A7D0329=020F712952832C2001107A70" +
            "6C55,262D=020F712952832C20017A7055,1229=020F712952832C2000010608,122D=020F712952832C" +
            "2000010608,1011=020F712952832C2000010608,0A0B=020F712952832C2000010608,1F0F=020F7129" +
            "52832C2000010870556C,1A0E=020F712952832C206C55,7A703312=020F712952832C2010,000106037" +
            "A70172A=020F712952832C2010,7A7055000106033B3B=020F712952832C2010,416D000106037A700B1" +
            "2=020F712952832C20106C55,000106037A700615=020F712952832C207A7055,3203=020F712952832C" +
            "207A7055,201B=020F712952832C207A706C5510,2023=020F712952832C207A6C7055,2A1B=020F7129" +
            "528320,000106087A702C2629=020F7129528320,7A702C2E3709=020F7129528320,7A702C000106083" +
            "A24=020F7129528320,7A70552C2E341A=020F712952832038000106087A70,2C2E1C2D=020F71295283" +
            "2001,7A702C2E0611=020F712952832001,7A702C2E021A=020F712952832001,7A7D2C2E3815=020F71" +
            "295283200100,7A702C2E3024=020F71295283200110,616D2C2E093B=020F71295283206C55,7A702C2" +
            "E000106030505=020F71295283206C55,7A702C030C1A=020F71295283207A706C55,000106082C2E370" +
            "5=020F712952837A706C55,032C201F0C=02550F20,000106037A700508=02550F20,000106037A70302" +
            "9=02550F20,000106087A702C2E3027=02550F202C807115295256,000106037A703526=02100F2C2952" +
            "8320,000106037A70150E=02100F2C29528320,00010608380F=02100F2C29528320,000106083527=02" +
            "100F2C29528320,7A70000106031C27=02100F2C2955528320,000106081227=02100F2C29555283207A" +
            "706C,00010608060F=02100F2C29555283207A706C,000106081D34=02100F7020,7A7D000106030F02=" +
            "02100F7055528315,2F8026000106083920=02100F7055528315,2F802600010608212A=02100F705552" +
            "8315,000106082A20=02100F7055528315,000106083A26=02100F7055528315,000106080439=02100F" +
            "7055528315,000106080008=02100F7055528315,000106081B21=02100F7055528315,00010608071B=" +
            "02100F7055528315,000106080D24=02100F7055528315,000106082C2E2C32=02100F7055528315,000" +
            "106082C2E2B2C=02100F7055528315,00010608032C201402=02100F7055528315,00010608032C20391" +
            "C=02100F7055528315,7A7D000106031F10=02100F705552831538,2F8026000106082D06=02100F7055" +
            "5283157A,2F802600010608290D=02100F20,7A702C000106032416=02100F20,616D000106037A702C3" +
            "4=02100F20292C,7A70000106031C2A=02100F528315,7A7055000106032234=02100F528315,7A70550" +
            "00106032A21=02100F55528315,000106037A703313=02100F55528315,000106037A700509=02100F55" +
            "528315,000106037A702D03=02100F55528315,000106037A700613=02100F55528315,000106037A702" +
            "235=02100F55528315,000106037A70391D=02100F55528315,000106037A70100F=02100F55528315,0" +
            "00106087A702C111B=02100F55528315,000106087A702C2E2916=02100F55528315,7A2C71707D00010" +
            "6030430=02100F55528315,7A2C71707D000106033B32=02100F55528315,7A2C71707D000106081903=" +
            "02100F55528315,7A702C2E000106033A27=02100F55528315,7A702C000106030931=02100F55528315" +
            ",7A702C000106030C1C=02100F55528315,7A70000106032735=02100F555283152C8071,000106037A7" +
            "00B13=02100F555283152C807138,000106037A701517=02100F555283152C807138,000106037A70291" +
            "7=02100F555283156C,000106037A703136=550F522010,7A2C71707D01022A1E=550F715220,7A702C2" +
            "E1333=550F715220,7A702C2E000106081405=556C,000106087A702C2E0433=556C,7A70000106083B3" +
            "8=556C0F197120,7A702C2E1E01=556C0F19712001,7A702C2E190B=556C000108,7A70230B=556C0001" +
            "08,7A702C2E1A0F=556C0001082C807115295256,7A701830=556C0008,7A2C71707D01023814=556C10" +
            "0F295220,7A2C71707D03082F=556C100F295220,7A702C0C1D=556C100F295220,7A702C2E000106030" +
            "21D=556C100F295220,7A70000106031121=556C100F2952202C,7A701835=556C100F2952202C807138" +
            "15,000106037A703B30=556C100F29522002,000106037A70290C=556C100F29522002,7A70000106030" +
            "930=556C100F2952200238,000106037A702B27=556C100F2952200102,7A702C2E3812=556C08,00010" +
            "6037A701012=556C08,000106037A701621=556C08,7A702C2E000106033209=556C08,7A702C2E00010" +
            "6032021=556C082C807138152952,000106037A700009=556C082C807138152952,000106037A702A1D=" +
            "807138152952000170100F,032C200A05=807138152952000170100F,032C20273B=8071381529527A70" +
            "6C550F,032C203423=80711529525600010870556C100F,032C201511=80711529525600010870556C10" +
            "0F,032C20183B=80711529525600010870556C100F,032C203311=010F2C80093829206C55,7A702B29=" +
            "010F2C80093829206C55,7A70616D3A25=010F2C09382920,7A70550825=010F2C093829207A6C5570,2" +
            "01E=010F09382920,7A702C2E352E=010670100F2C71522000,1C28=010670100F7152207A6C55,2C2E2" +
            "E11=0106100F7152,7A70032C203205=0106100F71526C,7A70032C202A19=0102290F20,7A702C2E2A1" +
            "F=010270290F2C207A6C55,2413=010270290F2C207A6C55,0437=010270290F2C207A6C55,0935=0102" +
            "70550F,032C201B18=010270550F20,2B24=010270550F20,2F80261906=010270550F20,2C2E2732=01" +
            "0270550F20,2C2E071A=010270550F20,2C2E3700=010270550F20,7A7D1724=010270550F203800,2F8" +
            "0263921=010270550F202C29,416D290F=010270550F202C807138152952,1619=010270550F202C8071" +
            "381529527A,3207=010270550F202C80711529525600,0829=010270550F2000,060D=010270550F2000" +
            ",0001=010270550F2000,2736=010270550F207A,1B1E=010270550F207A,2C2E140B=010270550F207A" +
            "6C,0114=010270550F7A6C,032C202C3B=010270550F7A6C,032C20201F=0102550F20,7A702C1A13=01" +
            "02550F20,7A702C3637=0102550F20,7A702C280B=0102550F20,7A702C223B=0102550F20,7A702C032" +
            "D04=0102100F2C29528320,7A701409=0102100F2C29528320,7A70552307=0102100F2C2952832000,0" +
            "005=0102100F295283,032C207A700A00=0102100F2955528320,7A2C71707D082D=0102100F29555283" +
            "20,7A702C2E2809=0102100F295552832000,7A702C2E2B2D=0102100F7055528315,021E=0102100F70" +
            "55528315,0C20=0102100F7055528315,2F80263420=0102100F7055528315,2F80261510=0102100F70" +
            "55528315,2F80262E10=0102100F7055528315,2F80262806=0102100F7055528315,2F80263134=0102" +
            "100F7055528315,2F80261D38=0102100F7055528315,2F8026251A=0102100F7055528315,2F80263A2" +
            "A=0102100F7055528315,2F80267A7D1120=0102100F7055528315,2F80267A7D0824=0102100F705552" +
            "8315,2C2E1E00=0102100F7055528315,2C2E7A2F1D=0102100F7055528315,032C200A06=0102100F70" +
            "55528315,7A7D2C2E1C2E=0102100F70555283153800,2F80261832=0102100F70555283153800,2C2E2" +
            "80A=0102100F70555283153800,2C2E320A=0102100F705552831538007A,2738=0102100F7055528315" +
            "38007A6C,2F80260720=0102100F705552831538007A6C,2F8026032B=0102100F70555283152C292000" +
            ",1907=0102100F70555283152C292000,3703=0102100F70555283152C292000,2739=0102100F705552" +
            "83152C29207A,251B=0102100F70555283152C29207A,2B25=0102100F70555283152C29207A6C,1331=" +
            "0102100F70555283152C207A,0D29=0102100F70555283152C80717A,1B1D=0102100F70555283158071" +
            ",032C200D2D=0102100F705552831500,1725=0102100F705552831500,352D=0102100F705552831500" +
            ",0C19=0102100F705552831500,150F=0102100F705552831500,3025=0102100F705552831500,0F07=" +
            "0102100F705552831500,1E09=0102100F705552831500,251F=0102100F705552831500,010C=010210" +
            "0F705552831500,2F80261A10=0102100F705552831500,2F80261016=0102100F705552831500,2F802" +
            "60934=0102100F705552831500,2F80262910=0102100F705552831500,2F80267A7D1A14=0102100F70" +
            "5552831500,2C2E2304=0102100F705552831500,7A7D3421=0102100F7055528315002C2920,212F=01" +
            "02100F7055528315002C807138,111F=0102100F7055528315002C807138,3135=0102100F7055528315" +
            "008071,032C200828=0102100F7055528315007A6C,2022=0102100F70555283156C,7A7D140A=010210" +
            "0F70555283156C,7A7D2C2E2127=0102100F70555283157A,1618=0102100F70555283157A,0B0F=0102" +
            "100F70555283157A,1836=0102100F70555283157A,172E=0102100F70555283157A,2F8026352A=0102" +
            "100F70555283157A,2F80262B2E=0102100F70555283157A,2F8026082A=0102100F70555283157A,2F8" +
            "0262306=0102100F70555283157A,2F80263702=0102100F70555283157A,2F80262C38=0102100F7055" +
            "5283157A,2F80261E06=0102100F70555283157A,2F80261B1A=0102100F70555283157A,2F8026032A=" +
            "0102100F70555283157A,2C2E1F14=0102100F70555283157A,2C2E3810=0102100F70555283157A,2C2" +
            "E262C=0102100F70555283157A29,032C20201A=0102100F70555283157A00,2F80260A02=0102100F70" +
            "555283157A00,2F80261838=0102100F70555283157A6C,2F80260E34=0102100F70555283157A6C,2F8" +
            "0260438=0102100F70555283157A6C,2C2E2F1A=0102100F70555283157A6C,2C2E2305=0102100F5283" +
            "15,7A70553525=0102100F5283152C8071,7A70550723=0102100F528315807138,7A7055032C200D2A=" +
            "0102100F55528315,2F80267A2C71707D3316=0102100F55528315,2F80267A2C71707D1224=0102100F" +
            "55528315,2F80267A2C71707D212E=0102100F55528315,2F80267A700616=0102100F55528315,2F802" +
            "67A70380C=0102100F55528315,2F80267A700434=0102100F55528315,2F80267A702A18=0102100F55" +
            "528315,7A2C71707D2628=0102100F55528315,7A2C71707D100C=0102100F55528315,7A2C71707D2F8" +
            "0261729=0102100F55528315,7A701F15=0102100F55528315,7A70240E=0102100F55528315,7A70363" +
            "2=0102100F55528315,7A701339=0102100F55528315,7A700115=0102100F55528315,7A702C2C37=01" +
            "02100F55528315,7A702C320B=0102100F55528315,7A702C3206=0102100F55528315,7A702C2E2238=" +
            "0102100F55528315,616D2F80267A2C71707D3816=0102100F555283153800,2F80267A701406=010210" +
            "0F555283153800,2F80267A700111=0102100F555283152C8071,7A700501=0102100F555283152C8071" +
            ",7A70370B=0102100F555283152C807138,7A703B37=0102100F555283152C80713800,7A701C2F=0102" +
            "100F555283152920,7A702C240F=0102100F555283152920,7A702C0A03=0102100F555283152920,7A7" +
            "02C0221=0102100F55528315292000,7A702C2E3317=0102100F55528315292000,7A702C2E3634=0102" +
            "100F5552831500,2F80267A2C71707D3028=0102100F5552831500,7A2C71707D111A=0102100F555283" +
            "1500,7A2C71707D071E=0102100F5552831500,7A2C71707D2913=0102100F5552831500,7A702F19=01" +
            "02100F5552831500,7A702301=0102100F5552831500,7A702C3919=0102100F5552831500,7A702C3B3" +
            "3=0102100F5552831500,7A702C2E0223=0102100F5552831500,7A702C03032F=0102100F5552831500" +
            "6C,7A702C2E262E=0102100F555283156C,2F80267A70032E=0102100F555283156C,7A2C71707D0F0B=" +
            "0102100F555283156C,7A701D3B=0102100F555283156C,7A702C2E030116=01100F1571292C20,2F802" +
            "67A703200=01100F1571292C20,7A7055370A=01100F1571292C2000,7A701B22=01100F1571292C2000" +
            ",7A701E04=01100F1571292C2000,416D1336=01100F1571292C20007A70556C,391A=01100F1571292C" +
            "20007A6C7055,1C24=01100F1571292C207A7055,2F80260D2E=01100F15712920,7A702C2E2D0A=0110" +
            "0F15712920,7A702C2E2800=01100F15712920027A7055,2C2E251E=01100F157129207A70556C,2C2E1" +
            "228=01100F157129207A70556C,416D2C2E050A=01100F5220,7A70550000=01100F5220,616D2624=01" +
            "100F5220,616D2F80267A702804=01100F5220006C,7A70550F06=01100F52207A70556C,2C2E2F1E=01" +
            "100F52207A70556C,2C2E1014=01100F527A70556C,032C20161E=01100F712920,7A702C2E0A0A=0110" +
            "0F71522C2920,616D161C=0070100F292C20,01020F04=0006100F7020,7A7D01026D183A=0006100F70" +
            "20,616D0102201C=0006100F20,7A2C71707D01026D1D37=000170100F292C20,2F18=000170100F292C" +
            "802038,161D=00014B0F,032C201338=00014B0F2C2002,2F80261728=00014B0F20,2C2E0F0A=00014B" +
            "0F20,7A2C71707D1833=00014B0F20,7A702C1407=00014B0F20,7A702C1401=0001060838,2C2E1123=" +
            "0001060838,416D032C202019=000106082C38,2C31=000106082C38,391F=000106082C38,2523=0001" +
            "06082C38,7A70416D1C29=000106082C38020F71295283,3811=000106082C38020F71295283,7A70093" +
            "7=000106082C386C550F197120,7A700117=00010252100F29202C7A706C55,1337=00010206700F202C" +
            "807138152952,3A2E=00010206100F7020,616D0610=00010206100F20,7A2C71707D0328=0001020610" +
            "0F20,7A700F01=00010206100F20,7A702C3310=00010206100F20,7A702C2E3139=0001100F298020,7" +
            "A702C2625=00010870556C100F2C20,1909=00010870556C100F2C20,391E=00010870556C100F2C20,2" +
            "124=00010870556C100F2C20,2F80267A7D0F00=00010870556C100F2C2038,2D09=00010870556C100F" +
            "2C2002,0500=00010870556C100F2C207A,2C39=00010870556C100F2C207A,2518=00010870556C100F" +
            "2C207A,0B0C=00010870556C100F2C207A,2F80262911=00010870556C100F7A,032C200007=00010855" +
            "6C100F2C2029,7A700A07=000108556C100F2C2029,7A701332=000108556C100F20,2C2E7A70100D=00" +
            "0108556C100F20,7A702C2E2239=000108556C100F20,7A702C2E0A01=000108556C100F20,7A702C2E3" +
            "80D=0001086C100F2C20,7A70551D36=0001086C100F2C20,7A70552F1F=000108100F70552920,010D=" +
            "000108100F70552920,616D0507=000108100F705529202C80713815,0B0D=000108100F705529202C80" +
            "71157A,3133=000108100F7055292002,2309=000108100F7055292002,416D0002=000108100F705529" +
            "207A,2F80263202=000108100F705529207A,2F80263638=000108100F705529207A,2C2E2A1A=000108" +
            "100F705529207A38,2F80262414=000108100F705529207A6C,2C2E2E14=000108100F552920,7A2C717" +
            "07D1404=000108100F552920,7A2C71707D0B17=000108100F552920,7A70330D=000108100F552920,7" +
            "A702C172F=000108100F552920,7A702C2E3707=000108100F5529206C,616D7A702C2E302E=6C55700F" +
            "197120,2C2E7A7D0C22=6C55700F197120,7A7D01026D1E02=6C550F297120,000106037A703923=6C55" +
            "0F297120,7A702C2E03230A=6C550F1920,7A2C71707D240C=6C550F19200210,7A2C71707D000106031" +
            "A16=6C550F197120,000106037A701513=6C550F197120,7A703A2B=6C550F197120,7A701837=6C550F" +
            "197120,7A702F23=6C550F197120,7A702F22=6C550F197120,7A702D07=6C550F197120,7A702C2E392" +
            "2=6C550F197120,7A700102093A=6C550F197120,7A70000106031B19=6C550F197120,616D7A70071F=" +
            "6C550F197120,616D7A702C2E212B=6C550F197120,616D7A702C2E000106032734=6C550F197120292C" +
            ",000106037A700325=6C550F1971200001020610,7A702C122B=6C550F19712008,000106037A702411=" +
            "6C100F2952,7A7055032C20010E=100F2C29528320,01023704=100F2C29528320,0102363A=100F292C" +
            "206C55,000106037A702B26=100F2920,7A2C71707D01026D302C=100F7055528315,01021E08=100F70" +
            "55528315,01022730=100F7055528315,01021512=100F7055528315,010200352C=100F7055528315,7" +
            "A7D01026D2F1C=100F7055528315,7A7D01026D0222=100F70555283153800,01026D2412=100F705552" +
            "83157A,01022230=100F70555283157A,0102060E=100F70555283157A6C,01022C3A=100F7055528315" +
            "7A6C,01026D1F12=100F1571292C20,01026D3B36=100F1571292C20,01026D1516=100F1571292C20,0" +
            "00106037A702302=100F1571292C20,000106037A701D32=100F1571292C20,000106082F8026330E=10" +
            "0F1571292C20,000106086D2A1C=100F1571292C20,7A7001026D313A=100F1571292C20,7A700001060" +
            "3341C=100F1571292C20,416D7A70000106032B2A=100F1571292C2002,000106037A700326=100F1571" +
            "292C20556C,000106037A70273A=100F1571292C2000,01026D0722=100F1571292C2000,01026D2E0C=" +
            "100F1571292C206C55,000106037A701408=100F1571292C207A706C55,01022020=100F1571292C207A" +
            "706C55,000106081726=100F1571292C207A6C7055,0102290E=100F1571292C207A6C7055,000106080" +
            "932=100F1571292C207A6C7055,000106080D26=100F52,00010608032C20100E=100F5283153800,010" +
            "27A70550B16=100F5220,2F8026000106081122=100F5220,6D010200133A=100F5220,01026D1F16=10" +
            "0F5220,000106037A703132=100F5220,000106083B3A=100F5220,000106082522=100F5220,0001060" +
            "8190A=100F5220,000106082C2E021C=100F5220,7A70000106030936=100F52202C,01026D3A2C=100F" +
            "52206C55,01027A701A0C=100F52206C55,000106037A700E30=100F52206C55,000106037A700A08=10" +
            "0F52207A706C55,000106083204=100F52207A6C5570,01026D0B0E=100F55528315,01027A2C71707D0" +
            "004=100F55528315,7A2C71707D01026D1D3A=100F55528315,7A2C71707D01026D3418=100F55528315" +
            "00,7A2C71707D0102201D=100F712920,7A702C2E00010608030E36=100F71522C2920,01023635=100F" +
            "715229,00010608032C20021B=7A70550F2C715220,1900=7A70550F715220,2C2E0A09=7A70556C,000" +
            "10608172C=7A70556C,00010608032C200B14=7A70556C,00010608032C202914=7A70556C0F197120,2" +
            "C2E0938=7A70556C0F197120,000106082C2E111E=7A70556C000108,0502=7A70556C000108,2F80260" +
            "D2F=7A70556C0001082C807138152952,2D0B=7A70556C0001082C807138152952,3633=7A70556C0001" +
            "082C807115295256,0C18=7A70556C0008,01020218=7A70556C0008,0102302F=7A70556C100F295220" +
            ",000106082C35=7A70556C100F295220,000106081E0B=7A70556C100F2952202C807115,3130=7A7055" +
            "6C100F29522002,000106080506=7A70556C100F29522001,2C2E330F=7A70556C100F29522001022C80" +
            "71,010F=7A70556C100F295220010200,0435=7A70556C100F295280713815,032C200614=7A70556C10" +
            "0F295201,032C20122C=7A70556C100F29520102,032C203B39=7A706C550F297120,0F05=7A706C550F" +
            "297102,032C200D25=7A706C550F19712001,616D2233=7A706C550F19712000010608,2626=7A6C7055" +
            "0F197120,01021A17=7A6C70550F197120,00010608262F=7A6C70550F1971202C29,000106083529=7A" +
            "6C70550F19712002,616D000106082D08=7A6C70550F197120103800,0102341F=7A6C55700F197120,2" +
            "C2E172B=082C38,7A7055000106030D27=082C38,7A70000106030827=08556C100F2C20,000106037A7" +
            "02803=08556C100F2C20,000106037A701013=08556C100F2C20,7A7000010603262B=08556C100F2C20" +
            ",7A7000010603240D=08556C100F2C20,7A70000106033631=08556C100F2C20,7A70000106030431=08" +
            "556C100F20,7A702C2E000106031D35=08100F552920,000106037A701335=08100F552920,000106037" +
            "A700612=08100F55292038,000106037A70";
    /**
     * 神煞
     */
    private static final String[] SHEN_SHA = {
            "无", "天恩", "母仓", "时阳", "生气", "益后", "青龙", "灾煞", "天火", "四忌", "八龙", "复日",
            "续世", "明堂", "月煞", "月虚", "血支", "天贼", "五虚", "土符", "归忌", "血忌", "月德", "月恩",
            "四相", "王日", "天仓", "不将", "要安", "五合", "鸣吠对", "月建", "小时", "土府", "往亡", "天刑",
            "天德", "官日", "吉期", "玉宇", "大时", "大败", "咸池", "朱雀", "守日", "天巫", "福德", "六仪",
            "金堂", "金匮", "厌对", "招摇", "九空", "九坎", "九焦", "相日", "宝光", "天罡", "死神", "月刑",
            "月害", "游祸", "重日", "时德", "民日", "三合", "临日", "天马", "时阴", "鸣吠", "死气", "地囊",
            "白虎", "月德合", "敬安", "玉堂", "普护", "解神", "小耗", "天德合", "月空", "驿马", "天后",
            "除神", "月破", "大耗", "五离", "天牢", "阴德", "福生", "天吏", "致死", "元武", "阳德", "天喜",
            "天医", "司命", "月厌", "地火", "四击", "大煞", "大会", "天愿", "六合", "五富", "圣心", "河魁",
            "劫煞", "四穷", "勾陈", "触水龙", "八风", "天赦", "五墓", "八专", "阴错", "四耗", "阳错", "四废",
            "三阴", "小会", "阴道冲阳", "单阴", "孤辰", "阴位", "行狠", "了戾", "绝阴", "纯阳", "七鸟",
            "岁薄", "阴阳交破", "阴阳俱错", "阴阳击冲", "逐阵", "阳错阴冲", "七符", "天狗", "九虎", "成日",
            "天符", "孤阳", "绝阳", "纯阴", "六蛇", "阴神", "解除", "阳破阴冲"
    };
    /**
     * 每日神煞数据
     */
    private static final String DAY_SHEN_SHA = "100=010203040506,0708090A0B101=010C0D,0E0F1011121" +
            "31415102=16011718191A1B1C1D1E,1F20212223103=24011825261B271D1E,28292A2B104=012C2D2E2" +
            "F3031,3233343536105=3738,393A3B3C3D123E106=3F404142434445,464748107=494A4B4C4D,4E108" +
            "=4F5051524C4D5345,54555657109=58595345,5A5B12565C10A=5D415E5F60,616263640B6510B=0266" +
            "676869,6A6B6C0A3E6D10C=1602171803041B05061E,07086E10D=24181B0C0D,0E0F1011126F1314151" +
            "0E=70191A1C1D,1F2021222310F=0125261B271D,28292A2B110=012C2D2E2F3031,3233343536111=49" +
            "013738,393A3B3C3D123E112=4F50013F404142434445,4648113=014A4B,4E6E114=51524C4D5345,54" +
            "550B5657115=0158595345,5A5B12565C116=1601185D415E5F60,61626364117=24021867681B69,6A6" +
            "B3E6D118=0203040506,0708119=1B0C0D,0E0F10111213141511A=191A1B1C1D1E,1F2021222311B=49" +
            "25261B271D1E,28292A11C=4F502C2D2E2F3031,323334353611D=3738,393A3B3C3D123E11E=3F40414" +
            "2434445,460B4811F=4A4B,4E71120=16171851524C4D5345,545556121=241858595345,5A5B12565C1" +
            "22=5D415E5F60,61626364123=0267681B69,6A6B3E6D124=0203041B05061E,070847125=491B0C0D,0" +
            "E0F101112131415126=4F50191A1C1D1E,1F20212223127=2526271D1E,28292A2B128=2C2D2E2F3031," +
            "32333435360B129=3738,393A3B3C3D123E12A=1617183F404142434445,464812B=24184A4B,4E7212C" +
            "=51524C4D53,5455565712D=0158595345,5A5B12565C12E=015D415E5F60,616263647312F=49010267" +
            "681B69,6A6B3E6D130=4F500102030405061E,070874131=010C0D,0E0F101112131415726E132=191A1" +
            "C1D1E,1F2021220B722375133=2526271D1E,28292A2B134=1617182C2D2E2F3031,3233343536135=24" +
            "183738,393A3B3C3D126F3E136=3F4041424344,4648137=4A4B,4E72138=51524C4D5345,5455765672" +
            "57139=4958595345,5A5B7612565C7713A=4F505D415E5F60,6162636413B=02676869,6A6B3E6D200=1" +
            "601025D60,393B28292A11090A201=0103041A1B4A,123435360B6D202=011819681B4C1D061E,3D1014" +
            "203=011718252F591D0D1E,1F20213233204=012C26,3C23205=493751522D2E69,121364223E2B206=5" +
            "03F4005311E,6A3A5A5B207=5841440C38,4615208=431C4D45,6B4E5648209=27534B45,54550708616" +
            "2125620A=16666730,0E0F635720B=0241425E5F1B,6C0A0B3E5C20C=02185D1B601E,393B28292A116E" +
            "20D=171803041B4A,126F3435366D20E=7019684C1D06,3D101420F=4901252F591D0D,1F20213233782" +
            "10=50012C26,3C23211=013751522D2E69,121364223E2B212=013F40053145,6A3A5A5B213=01584144" +
            "0C38,46156E214=16431C4D5345,6B4E5648215=27534B45,545507086162120B5648216=18671B30,0E" +
            "0F6357217=02171841425E5F1B,3E5C218=025D60,393B28292A11219=4903041A1B4A,123435366D21A" +
            "=5019681B4C1D061E,3D101421B=252F591D0D45,1F2021323321C=2C26,3C2321D=3751522D2E69,121" +
            "364223E2B21E=163F40053145,6A3A5A5B21F=5841440C38,467147150B220=18431C4D5345,6B4E5648" +
            "221=171827534B45,5455070861621256222=6730,0E0F6357223=490241425E5F1B,3E5C224=50025D1" +
            "B601E,393B28292A11225=03041A4A,123435366D226=19684C1D061E,3D1014227=252F591D0D1E,1F2" +
            "0213233228=162C26,3C23229=3751522D2E69,121364220B3E2B22A=183F40053145,6A3A5A5B22B=17" +
            "185841440C38,46157222C=431C4D53,6B4E564822D=490127534B45,54550708616212567922E=50016" +
            "71B30,0E0F635722F=010241425E5F,3E5C230=01025D601E,393B28292A1174231=0103041A4A,12343" +
            "53647726E6D232=1619684C1D061E,3D1014233=252F591D0D1E,1F202132330B75234=182C26,3C2323" +
            "5=17183751522D2E69,126F1364223E2B236=3F400531,6A3A5A5B237=495841440C38,461572238=504" +
            "31C4D5345,6B4E76567248239=27534B45,5455070861627612567323A=6730,0E0F635723B=0241425E" +
            "5F,3E5C300=0102415E5F1A1B69,090A471457301=011B05,6A125C302=5001185D19515203042F0C1D6" +
            "01E,323315303=4F490118251C1D1E,3C5A5B106D304=012C2706,1F20213B710B787A305=5837266830" +
            "0D,6B123E306=173F402D2E45,07086423307=00,393A0E2B308=24164142444A533145,61624622567B" +
            "309=674C533845,28292A4E12135630A=431B594D,5455633435364830B=021B27,3D116C0A3E30C=500" +
            "218415E5F1A1B691E,146E5730D=4F49181B05,6A126F5C30E=705D19515203042F0C1D60,3233150B30" +
            "F=01251C1D,3C5A5B106D310=01172C2706,1F20213B7C311=0158372668300D,6B123E312=2416013F4" +
            "02D2E45,0708476423313=01,393A0E0F6E2B314=4142444A533145,61624622567D315=66671B4C5338" +
            "45,28292A4E121356316=5018431B594D,54556334353648317=4F4902181B4B,3D113E318=02415E5F1" +
            "A69,140B57319=1B05,6A125C31A=175D19515203042F0C1D601E,32331531B=251C1D1E,3C5A5B106D3" +
            "1C=24162C2706,1F20213B31D=58372668300D,6B123E31E=3F402D2E45,0708642331F=00,393A0E0F2" +
            "B320=50184142444A533145,61624622567E321=4F4918671B4C533845,28292A4E121356322=43594D," +
            "5455633435360B48323=021B4B,3D113E324=0217415E5F1A691E,1457325=05,6A125C326=58165D195" +
            "15203042F0C1D601E,323315327=251C1D1E,3C5A5B106D328=2C2706,1F20213B75329=58372668300D" +
            ",6B123E32A=50183F402D2E45,0708642332B=4F4918,393A0E0F722B32C=4142444A5331,616246220B" +
            "567B32D=01671B4C533845,28292A4E12135632E=011743594D,5455633435364832F=01024B,3D113E3" +
            "30=24160102415E5F1A691E,741457331=0105,6A12726E5C332=5D19515203042F0C1D601E,32331572" +
            "333=251C1D1E,3C5A5B106D334=50182C2706,1F20213B335=4F491858372668300D,6B126F3E336=3F4" +
            "02D2E,0708640B23337=00,393A0E0F722B338=174142444A533145,616246762256727B73339=674C53" +
            "3845,28292A4E7612135633A=241643594D,5455633435364833B=024B,3D113E400=5001431B,5A5B12" +
            "48401=490141425E5F2F4B,32336314402=4F01024A1D1E,396B3C130B57403=01025803044C1D1E,070" +
            "85C404=01183F5D5960,0E0F10127F405=171819,1F20213E6D788075406=162526690645,28292A407=" +
            "242C2D2E050D,6162343536647B408=3767680C5345,6A3A3B3D12155623409=4041441C5345,46562B4" +
            "0A=501B274D31,4E1140B=4951521A1B3038,5455223E40C=4F431B1E,5A5B0981120B6E4840D=41425E" +
            "5F2F4B,3233631440E=02184A1D,396B3C135740F=010217185803044C1D,0708475C410=16013F58596" +
            "0,0E0F1012411=240119,1F20213E6D412=012526690645,28292A413=012C2D2E050D,6162343536646" +
            "E7B414=503767681B0C5345,6A3A3B3D126F155623415=494041441B1C5345,46562B416=4F1B274D31," +
            "4E11710B417=51521A1B3038,54556C81223E418=18431B,5A5B1248419=171841425E5F2F4B,3233631" +
            "441A=16024A1D1E,396B3C135741B=24025844044C1D1E,07085C41C=3F5D5960,0E0F101241D=19,1F2" +
            "0213E6D41E=50702526690645,28292A41F=492C2D2E050D,6162343536647D420=4F663767681B0C534" +
            "5,6A3A3B3D12150B5623421=4041441B1C5345,46562B422=181B274D31,4E11423=171851521A3038,5" +
            "455223E424=16431E,5A5B1248425=2441425E5F2F4B,32336314426=024A1D1E,396B3C1357427=0258" +
            "03044C1D1E,07085C428=503F5D5960,0E0F10126F429=4919,1F20213E6D42A=4F2526690645,28292A" +
            "0B8242B=2C2D2E050D,616234353664727E7342C=183767681B0C53,6A3A3B3D1215562342D=01171840" +
            "41441C5345,4647562B42E=1601274D31,4E1142F=240151521A3038,5455223E430=01431E,5A5B7612" +
            "48431=0141425E5F2F4B,32336314726E432=50024A1D1E,396B3C137257433=49025844044C1D1E,070" +
            "8745C434=4F3F5D5960,0E0F10120B435=19,1F20213E6D75436=1825266906,28292A82437=17182C2D" +
            "2E050D,616234353664727B73438=163767680C5345,6A3A3B3D1215567223439=244041441C5345,465" +
            "62B43A=274D31,4E1143B=51521A3038,545576223E83500=012F4D31,54550708323312501=01586938" +
            ",0E0F3C63502=16010241435E5F051D1E,641448503=01020C1D4B1E,6A28292A353615220B504=01171" +
            "83F03041C,123457505=181927,3D103E5C506=5D25306045,1F20213B616213507=492C2667,6D508=5" +
            "03751522D2E530645,1256509=401B4A530D45,393A5A5B115650A=4142441A1B4C,462350B=681B59,6" +
            "B4E3E2B50C=162F4D311E,5455070832330981126E50D=586938,0E0F3C0B50E=02171841435E5F051D," +
            "64144850F=0102180C1D4B,6A28292A35361522510=013F03041C,123457511=49011927,3D103E5C512" +
            "=50015D25306045,1F20213B616213513=012C26671B,6E6D514=3751522D2E1B530645,126F56515=40" +
            "1B4A530D45,393A5A5B1156516=164142441A1B4C,467123517=6859,6B4E6C810B3E2B518=17182F4D3" +
            "1,54550708323312519=18586938,0E0F3C6351A=0241435E5F051D1E,64144851B=49020C1D4B1E,6A2" +
            "8292A3536152251C=503F03041C,12345751D=1927,3D103E5C51E=705D25306045,1F20213B61621351" +
            "F=2C26671B,6D520=163751522D2E1B530645,1256521=404A530D45,393A5A5B110B56522=171841424" +
            "41A1B,4623523=186859,6B4E3E2B524=2F4D311E,54550708323312525=49586938,0E0F3C63526=500" +
            "241435E5F051D1E,641448527=020C1D4B1E,6A28292A35361522528=3F03041C,126F344757529=1927" +
            ",3D103E5C52A=165D25306045,1F20213B616213658452B=662C2667,0B726D52C=17183751522D2E1B5" +
            "306,125652D=0118404A530D45,393A5A5B115652E=014142441A4C,462352F=49016859,6B4E3E2B530" +
            "=50012F4D311E,545507083233761285531=01586938,0E0F3C63726E532=0241435E5F051D1E,641472" +
            "48533=020C1D4B1E,6A28292A7435361522534=163F03041C,123457535=1927,3D100B3E5C536=16185" +
            "D253060,1F20213B61621378537=182C2667,726D538=3751522D2E530645,125672539=49404A530D45" +
            ",393A5A5B115653A=504142441A4C,46472353B=681B59,6B4E763E2B600=241601304D,3C28292A4E12" +
            "35361423601=01,54553B63342B602=0102681D311E,3D603=010241425E5F4A1D381E,64604=01183F4" +
            "34C,39127148605=4F49181951520304594B,61620B3E73606=50256745,5A5B102257607=172C69,1F2" +
            "0215C608=5D37261B05536045,6B111256609=402D2E1A1B0C5345,6B11125660A=24161B1C06,6A3A0E" +
            "0F1360B=5841442F270D,3233463E60C=304D1E,3C28292A4E0981123536146E2360D=00,54553B63342" +
            "B60E=0218681D31,3D60F=4F4901021841425E5F4A1D38,640B610=50013F434C,391248611=01171951" +
            "520304594B,61623E612=0125671B45,5A5B102257613=012C1B69,1F20216E5C614=24165D37261B055" +
            "36045,6B11126F56615=402D2E1A1B0C5345,070815566D616=1C06,6A3A0E0F1347617=5841442F270D" +
            ",3233466C813E618=18304D,3C28292A4E1235361423619=4F4918,54553B63340B2B61A=5002681D311" +
            "E,3D61B=021741425E5F4A1D381E,6461C=3F434C,39124861D=1951520304594B,61623E61E=2416702" +
            "5671B45,5A5B10225761F=2C1B69,1F20215C620=5D372605536045,6B111256621=402D2E1A0C5345,0" +
            "70815566D622=181B1C06,6A3A0E0F13623=4F49185841442F270D,3233460B3E624=50304D1E,3C2829" +
            "2A4E1235361423625=17,54553B63342B626=02681D311E,3D627=0241425E5F4A1D381E,64628=24163" +
            "F434C,39126F48629=1951520304594B,61623E62A=256745,5A5B1022578662B=2C69,1F2021725C756" +
            "2C=185D37261B055360,6B11125662D=4F490118402D2E1A0C5345,0708150B566D62E=50011C06,6A3A" +
            "0E0F1362F=01175841442F270D,3233463E630=01304D1E,3C28292A4E761235361423631=01,54553B6" +
            "334726E2B87632=241602681D311E,3D72633=0241425E5F4A1D381E,7464634=3F434C,39124748635=" +
            "1951520304594B,61623E6573636=661825671B,5A5B10225786637=4F49182C69,1F20210B725C75638" +
            "=505D372605536045,6B11125672639=17402D2E1A0C5345,070815566D63A=1B1C06,6A3A0E0F1363B=" +
            "5841442F270D,323346763E700=0103404142445906,46701=01020D,4E14702=50015152694D1D1E,54" +
            "553B23703=4901051D1E,5A5B2B1288704=4F0102415E5F0C31,6162636415705=6667681C38,6A6B3E7" +
            "06=4303042745,07080B48707=02304B,0E0F101112708=16171819,1F20135657709=24185825261B53" +
            "45,28292A353622565C70A=025D2C2D2E2F4A60,3233893470B=374C,393A3C3D3E6D70C=503F4041424" +
            "459061E,466E70D=49020D,4E1470E=4F5152694D1D,54553B70F=01051D,5A5B12132B710=0102415E5" +
            "F0C31,61626364150B65711=0167681C38,6A6B3E712=162417184303041B2745,070848713=24010218" +
            "1B304B,0E0F1011126E714=191A1B5345,1F20215657715=5825261B5345,28292A353622565C717=493" +
            "74C,393A3C3D126F473E6D718=4F3F404142445906,46719=020D,4E1471A=515269,1D1E71B=051D1E," +
            "5A5B12132B71C=16021718415E5F0C31,616263641571D=241867681B1C38,6A6B3E71E=4303041B2745" +
            ",07084871F=021B30,0E0F101112720=50191A5345,1F20215657721=495825265345,28292A35362256" +
            "5C722=4F025D2C2D2E2F4A60,32338934723=374C,393A3C3D123E6D724=3F4041424459061E,46098A0" +
            "B725=020D,4E7114726=1617185152694D1D1E,54553B23727=2418051D1E,5A5B12132B728=02415E5F" +
            "0C31,616263641573729=67681B1C38,6A6B3E72A=504303042745,07084872B=4902304B,0E0F101112" +
            "6F7272C=4F70191A1B,1F2021565772D=015825265345,28292A353622565C72E=01025D2C2D2E2F4A60" +
            ",323389340B72F=01374C,393A3C3D6C8A123E6D730=160117183F4041424459061E,46731=240102180" +
            "D,4E14726E732=5152694D1D1E,54553B767223733=051D1E,5A5B7612132B77734=5002415E5F0C31,6" +
            "162636415735=4967681C38,6A6B473E736=4F4303041B27,7448737=02304B,0E0F10111272738=191A" +
            "5345,1F20210B56725775739=5825265345,28292A353622565C73A=160217185D2C2D2E2F4A60,32338" +
            "93473B=2418374C,393A3C3D123E6D800=50013F5D402760,6A3A5A5B22801=490102414430,466D802=" +
            "014D1D061E,6B4E4714803=011D0D1E,54550708616212804=0102671B4A,0E0F6323805=41425E5F4C," +
            "8B2B806=16593145,3928292A113536807=025803041A1B38,1234130B808=181943681B695345,3D105" +
            "648809=1718252F0553534B45,1F20213B32335680A=50022C260C,3C155780B=493751522D2E1C,1264" +
            "3E5C80C=3F5D4027601E,6A3A5A5B226E80D=02414430,466D80E=4D1D06,6B4E1480F=011D0D,545507" +
            "0861621279810=16010266674A,0E0F6323811=0141425E5F1B4C,0B3E2B812=01181B593145,3928292" +
            "A113536813=010217185803041A1B38,1234136E814=501943681B695345,3D105648815=49252F05534" +
            "B45,1F20213B323356816=022C260C,3C1557817=3751522D2E1C,126F643E5C818=3F5D402760,6A3A5" +
            "A5B22819=02414430,466D81A=164D1D061E,6B4E1481B=1D0D1E,545507086162120B6581C=0218671B" +
            "4A,0E0F632381D=171841425E5F1B4C,3E2B81E=501B593145,3928292A11353681F=49025D03041A38," +
            "123413820=194368695345,3D10475648821=252F05534B45,1F20213B323356716=50025D2C2D2E2F4A" +
            "60,32338934822=022C260C,3C1557823=3751522D2E1C,12643E5C824=163F5D4027601E,6A3A5A5B09" +
            "8A22825=02414430,46710B6D826=184D1D061E,6B4E14827=17181D0D1E,54550708616212828=50026" +
            "71B4A,0E0F6323829=4941425E5F4C,3E2B82A=593145,3928292A11353682B=025803041A38,126F341" +
            "37282C=701943681B6953,3D10564882D=01252F05534B45,1F2021613233567882E=1601022C260C,3C" +
            "155782F=013751522D2E1C,6C8A12640B3E5C830=01183F5D4027601E,6A3A5A5B22831=010217184144" +
            "30,46726E6D832=504D1D061E,6B4E761472833=491D0D1E,545507086162761273834=02674A,0E0F63" +
            "23835=41425E5F4C,3E2B836=1B5931,3928292A11743536837=025803041A38,12341372838=1619436" +
            "8695345,3D10567248839=252F05534B45,1F20213B32330B567583A=02182C260C,3C155783B=171837" +
            "51522D2E1C,12643E5C900=013F408C2E4C,0708641457901=010259,393A0E0F5C902=2416015D41424" +
            "41D601E,61624635367B903=0167691D1E,28292A4E126D904=01021B054D06,5455637134220B905=58" +
            "0C0D,3D11153E906=17415E5F1A1B1C45,23907=4F49021B27,6A3B12472B908=501819515203042F305" +
            "33145,323356909=1825533845,3C5A5B105690A=022C43,1F2021487C90B=3726684A4B,6B12133E90C" +
            "=24163F402D2E4C1E,070864146E5790D=0259,393A0E0F5C90E=5D4142441D60,61624635360B7B90F=" +
            "0167691D,28292A4E126D910=0102171B054D06,5455633422911=4F4901581B0C0D,3D11153E912=500" +
            "118415E5F1A1B1C45,23913=0102181B27,6A3B126E2B914=19515203042F30533145,323356915=2553" +
            "3845,3C5A5B1056916=2416022C43,1F202148917=3726684A4B,6B126F133E918=3F402D2E4C,070864" +
            "140B57919=0259,393A0E0F5C91A=175D4142441D601E,61624635367D91B=4F4966671B691D1E,28292" +
            "A4E126D91C=5002181B054D06,545563342291D=18581B0C0D,3D11153E91E=415E5F1A1C45,2391F=02" +
            "27,6A3B122B920=241619515203042F305331,323356921=25533845,3C5A5B1056922=022C43,1F2021" +
            "0B48788D923=3726684A4B,6B12133E924=173F402D2E4C1E,0708098A641457925=4F49022E,393A0E0" +
            "F475C926=50185D4142441D601E,61624635367E927=18671B691D1E,28292A4E126D928=02054D06,54" +
            "55633422929=580C0D,3D11153E92A=2416415E5F1A1C45,2392B=0227,6A3B126F722B92C=701951520" +
            "3042F305331,32330B5692D=0125533845,3C5A5B105692E=0102162C43,1F2021487592F=4F49013726" +
            "684A4B,6B6C8A12133E930=5001183F402D2E4C1E,0708641457931=01021859,393A0E0F726E5C932=5" +
            "D4142441D601E,616246763536727B73933=67691D1E,28292A4E76126D934=241602054D06,54556334" +
            "22935=580C0D,3D11153E936=415E5F1A1B1C,740B23937=0227,6A3B12722B938=1719515203042F305" +
            "33145,32335672939=4F4925533845,3C5A5B105693A=5002182C43,1F20214893B=183726684A4B,6B1" +
            "2133EA00=160170182543261C,28292A48A01=240117182C2D2E274B,61623464147BA02=013F3767683" +
            "01D1E,6A3A3D1257A03=01584041441D1E,465CA04=015D4D60,4E1113A05=4951521A1B4A,54553E6DA" +
            "06=4F501B4C0645,5A5B12A07=41425E5F2F590D,32336322A08=025345,396B3C0B5623A09=02030469" +
            "5345,0708562BA0A=16180531,0E0F10126FA0B=241618190C38,1F20213B3536103EA0C=2543261C1E," +
            "28292A6E48A0D=2C2D2E274B,61623464147BA0E=3F376768301D,6A3A3D124757A0F=4924584041441B" +
            "1D,465CA10=4F50015D1B4D60,4E1113A11=0151521A1B4A,54553E6DA12=011B4C0645,5A5B120BA13=" +
            "0141425E5F2F590D,323363226EA14=1602185345,396B3C5623A15=240217180304695345,0708562BA" +
            "16=0531,0E0F1012A17=190C38,1F20213B3536153EA18=2543261C,28292A4882A19=49503F3767681B" +
            "301D1E,6A3A3D1257A1A=4F503F3767681B301D1E,6A3A3D1257A1B=584041441B1D1E,465CA1C=5D1B4" +
            "D60,4E1171130BA1D=51521A1B4A,54553E6DA1E=16184C0645,5A5B12A1F=24171841425E5F2F590D,3" +
            "2336322A20=025345,396B3C5623A21=020304695345,0708562BA22=0531,0E0F10128EA23=49190C38" +
            ",1F20213B3536153E788FA24=4F502543261C1E,28292A48A25=2C2D2E274B,61623464147DA26=663F3" +
            "767681B301D1E,6A3A3D120B57A27=584041441B1D1E,465CA28=16185D4D60,4E1113A29=2417185152" +
            "1A4A,54553E6DA2A=4C0645,5A5B7612A2B=41425E5F2F590D,3233632272A2C=0253,396B3C475623A2" +
            "D=1601020304695345,0708562BA2E=4F50010531,0E0F1012A2F=01190C38,1F20213B3536153EA30=0" +
            "12543261C1E,28292A09900B4882A31=012C2D2E274B,6162346414726E7E73A32=16183F376768301D1" +
            "E,6A3A3D126F7257A33=2417185D4041441D1E,465CA34=5D4D60,4E1113A35=51521A4A,5455763E6D8" +
            "3A36=4C06,5A5B12A37=4941425E5F2F590D,3233632272A38=4F50029145,396B3C567223A39=020304" +
            "695345,070874562BA3A=0531,0E0F10120BA3B=190C38,1F20213B6C903536153E75B00=01701718254" +
            "A31,1F20216162B01=0118582C26,674C38B02=50013F375152432D2E591D1E,121448B03=4901401B1D" +
            "4B1E,393A5B11B04=014142441A69,4657B05=681B05,6B4E3E5CB06=682F0C4D6045,54550708323312" +
            "15B07=1C,0E0F3C636DB08=1602415E5F27530645,3536136456B09=0230530D45,6A28292A0B56B0A=1" +
            "7180304,126F342223B0B=1819,3D103E2BB0C=50254A311E,1F202161626EB0D=49582C26,671B4C38B" +
            "0E=3F375152432D2E591D,121448B0F=01401B1D4B,393A3B5A5B11B10=014142441A1B69,4657B11=01" +
            "681B05,6B4E3E5CB12=16015D2F0C4D6045,5455070832331215B13=011C,0E0F3C630B6E6DB14=02171" +
            "8415E5F27530645,3536136456B15=021830530D45,6A28292A56B16=500304,12342223B17=4919,3D1" +
            "03E2BB18=254A31,1F4E21616278B19=582C26,671B4C38B1A=3F375152432D2E1B591D1E,121448B1B=" +
            "401B1D4B1E,393A3B5A5B1147B1C=164142441A1B69,467157B1D=6805,6B4E0B3E5CB1E=17185D2F0C9" +
            "26045,5455070832331215B1F=181C,0E0F3C636DB20=5002415E5F27530645,3536136456B21=490230" +
            "530D45,6A28292A56B22=0304,12342223B23=19,3D103E2BB24=254A311E,1F20136162B25=582C2667" +
            "1B4C38,00B26=163F375152432D2E1B591D1E,121448B27=401D4B1E,393A3B5A5B110BB28=171841424" +
            "41A69,4657B29=186805,6B4E3E5CB2A=505D2F0C4D6045,54550708323376121585B2B=491C,0E0F3C6" +
            "3726DB2C=02415E5F275306,3536136456B2D=010230530D45,6A28292A56B2E=010304,12342223B2F=" +
            "0119,3D103E2BB30=1601254A311E,1F2021616209906584B31=0166582C26674C38,0B726EB32=17183" +
            "F375152432D2E591D1E,126F147248B33=18401D4B1E,393A3B5A5B11B34=504142441A69,4657B35=49" +
            "681B05,6B4E763E5CB36=5D2F0C4D60,5455070832331215B37=1C,0E0F3C63726DB38=02415E5F27530" +
            "645,353613645672B39=0230530D45,6A28292A744756B3A=160304,12342223B3B=19,3D106C900B3E2" +
            "BC00=500170661825670C,5A5B1013141523C01=4F4901182C1C,1F2021222BC02=011637261B271D311" +
            "E,6B1112C03=01402D2E1A1B311D381E,0708C04=0143,6A3A0E0F7148C05=41442F4B,32334635360B3" +
            "EC06=24164A4D45,3C28292A4E1257C07=174C,545563345CC08=025D6859536045,3D56C09=0241425E" +
            "5F5345,4764566DC0A=50186906,393B126FC0B=4F4918581951520304050D,61623EC0C=25671B0C1E," +
            "5A5B101314156E23C0D=2C1B1C,1F2021222BC0E=3F37264B1D31,6B1112C0F=01402D2E1A1B301D38,0" +
            "7080BC10=241601431B,6A3A0E0F48C11=011741442F4B,32334635363EC12=014A4D45,3C28292A4E12" +
            "57C13=014C,545563346E5CC14=5002185D6804536045,3D56C15=4F49021841425E5F5345,64566DC16" +
            "=6906,393B12C17=581951524404050D,61623EC18=25670C,5A5B101314152386C19=2C1B1C,1F20212" +
            "20B2BC1A=24163F37261B271D31,6B1112C1B=17402D2E1A1B301D381E,0708C1C=43,6A3A0E0F48C1D=" +
            "41582F4B,32334635363EC1E=50184A4D45,3C28292A4E1257C1F=4F49184C,545563345CC20=025D685" +
            "9536045,3D56C21=0241425E5F5345,64566DC22=6906,393B12C23=581951520304050D,61620B3EC24" +
            "=241625671B0C1E,5A5B1013141523C25=172C1B1C,1F2021222BC26=3F3726271D311E,6B1112C27=40" +
            "2D2E1A301D381E,0708C28=501843,6A5B0E0F48C29=4F491841442F4B,32334635363EC2A=4A4D45,3C" +
            "28292A4E761257C2B=4C,54556334725C93C2C=025D68595360,3D56C2D=010241425E5F5345,640B566" +
            "DC2E=2416016906,393B12C2F=0117581951520304050D,61623EC30=0125670C,5A5B10099013141523" +
            "86C31=012C1C,1F202122726E2B75C32=50183F3726271D311E,6B11126F72C33=4F4918402D2E1A301D" +
            "381E,070847C34=431B,6A3A0E0F48C35=41442F4B,3233467635363EC36=4A4D,3C28292A4E1257C37=" +
            "4C,545563340B725CC38=2416025D6859536045,3D5672C39=021741425E5F5345,7464566DC3A=6906," +
            "393B12C3B=581951520304050D,61626C903E6573";

    /**
     * 闰冬月年份
     */
    private static final int[] LEAP_11 = {75, 94, 170, 238, 265, 322, 389, 469, 553, 583, 610, 678, 735, 754, 773, 849, 887, 936, 1050, 1069, 1126, 1145, 1164, 1183, 1259, 1278, 1308, 1373, 1403, 1441, 1460, 1498, 1555, 1593, 1612, 1631, 1642, 2033, 2128, 2147, 2242, 2614, 2728, 2910, 3062, 3244, 3339, 3616, 3711, 3730, 3825, 4007, 4159, 4197, 4322, 4341, 4379, 4417, 4531, 4599, 4694, 4713, 4789, 4808, 4971, 5085, 5104, 5161, 5180, 5199, 5294, 5305, 5476, 5677, 5696, 5772, 5791, 5848, 5886, 6049, 6068, 6144, 6163, 6258, 6402, 6440, 6497, 6516, 6630, 6641, 6660, 6679, 6736, 6774, 6850, 6869, 6899, 6918, 6994, 7013, 7032, 7051, 7070, 7089, 7108, 7127, 7146, 7222, 7271, 7290, 7309, 7366, 7385, 7404, 7442, 7461, 7480, 7491, 7499, 7594, 7624, 7643, 7662, 7681, 7719, 7738, 7814, 7863, 7882, 7901, 7939, 7958, 7977, 7996, 8034, 8053, 8072, 8091, 8121, 8159, 8186, 8216, 8235, 8254, 8273, 8311, 8330, 8341, 8349, 8368, 8444, 8463, 8474, 8493, 8531, 8569, 8588, 8626, 8664, 8683, 8694, 8702, 8713, 8721, 8751, 8789, 8808, 8816, 8827, 8846, 8884, 8903, 8922, 8941, 8971, 9036, 9066, 9085, 9104, 9123, 9142, 9161, 9180, 9199, 9218, 9256, 9294, 9313, 9324, 9343, 9362, 9381, 9419, 9438, 9476, 9514, 9533, 9544, 9552, 9563, 9571, 9582, 9601, 9639, 9658, 9666, 9677, 9696, 9734, 9753, 9772, 9791, 9802, 9821, 9886, 9897, 9916, 9935, 9954, 9973, 9992};
    /**
     * 闰腊月年份
     */
    private static final int[] LEAP_12 = {37, 56, 113, 132, 151, 189, 208, 227, 246, 284, 303, 341, 360, 379, 417, 436, 458, 477, 496, 515, 534, 572, 591, 629, 648, 667, 697, 716, 792, 811, 830, 868, 906, 925, 944, 963, 982, 1001, 1020, 1039, 1058, 1088, 1153, 1202, 1221, 1240, 1297, 1335, 1392, 1411, 1422, 1430, 1517, 1525, 1536, 1574, 3358, 3472, 3806, 3988, 4751, 4941, 5066, 5123, 5275, 5343, 5438, 5457, 5495, 5533, 5552, 5715, 5810, 5829, 5905, 5924, 6421, 6535, 6793, 6812, 6888, 6907, 7002, 7184, 7260, 7279, 7374, 7556, 7746, 7757, 7776, 7833, 7852, 7871, 7966, 8015, 8110, 8129, 8148, 8224, 8243, 8338, 8406, 8425, 8482, 8501, 8520, 8558, 8596, 8607, 8615, 8645, 8740, 8778, 8835, 8865, 8930, 8960, 8979, 8998, 9017, 9055, 9074, 9093, 9112, 9150, 9188, 9237, 9275, 9332, 9351, 9370, 9408, 9427, 9446, 9457, 9465, 9495, 9560, 9590, 9628, 9647, 9685, 9715, 9742, 9780, 9810, 9818, 9829, 9848, 9867, 9905, 9924, 9943, 9962, 10000};

    /**
     * 元
     */
    private static final String[] YUAN = {"下", "上", "中"};

    /**
     * 运
     */
    private static final String[] YUN = {"七", "八", "九", "一", "二", "三", "四", "五", "六"};

    /**
     * 24节气表（对应阳历的准确时刻）
     */
    private final Map<String, Solar> solarTerm = new LinkedHashMap<>();
    /**
     * 对应阳历
     */
    private final Solar solar;
    /**
     * 阳历小时
     */
    private final int hour;
    /**
     * 阳历分钟
     */
    private final int minute;
    /**
     * 阳历秒钟
     */
    private final int second;
    /**
     * 农历年
     */
    private int year;
    /**
     * 农历月，闰月为负，即闰2月=-2
     */
    private int month;
    /**
     * 农历日
     */
    private int day;
    /**
     * 时对应的天干下标，0-9
     */
    private int timeGanIndex;
    /**
     * 时对应的地支下标，0-11
     */
    private int timeZhiIndex;
    /**
     * 日对应的天干下标，0-9
     */
    private int dayGanIndex;
    /**
     * 日对应的地支下标，0-11
     */
    private int dayZhiIndex;
    /**
     * 日对应的天干下标（八字流派1，晚子时日柱算明天），0-9
     */
    private int dayGanIndexExact;
    /**
     * 日对应的地支下标（八字流派1，晚子时日柱算明天），0-11
     */
    private int dayZhiIndexExact;
    /**
     * 日对应的天干下标（八字流派2，晚子时日柱算当天），0-9
     */
    private int dayGanIndexExact2;
    /**
     * 日对应的地支下标（八字流派2，晚子时日柱算当天），0-11
     */
    private int dayZhiIndexExact2;
    /**
     * 月对应的天干下标（以节交接当天起算），0-9
     */
    private int monthGanIndex;
    /**
     * 月对应的地支下标（以节交接当天起算），0-11
     */
    private int monthZhiIndex;
    /**
     * 月对应的天干下标（最精确的，供八字用，以节交接时刻起算），0-9
     */
    private int monthGanIndexExact;
    /**
     * 月对应的地支下标（最精确的，供八字用，以节交接时刻起算），0-11
     */
    private int monthZhiIndexExact;
    /**
     * 年对应的天干下标（国标，以正月初一为起点），0-9
     */
    private int yearGanIndex;
    /**
     * 年对应的地支下标（国标，以正月初一为起点），0-11
     */
    private int yearZhiIndex;
    /**
     * 年对应的天干下标（月干计算用，以立春为起点），0-9
     */
    private int yearGanIndexByLiChun;
    /**
     * 年对应的地支下标（月支计算用，以立春为起点），0-11
     */
    private int yearZhiIndexByLiChun;
    /**
     * 年对应的天干下标（最精确的，供八字用，以立春交接时刻为起点），0-9
     */
    private int yearGanIndexExact;
    /**
     * 年对应的地支下标（最精确的，供八字用，以立春交接时刻为起点），0-11
     */
    private int yearZhiIndexExact;
    /**
     * 周下标，1-7
     */
    private int weekIndex;
    /**
     * 八字
     */
    private EightChar eightChar;

    /**
     * 默认使用当前日期初始化
     */
    public Lunar() {
        this(new Date());
    }

    /**
     * 通过阳历日期初始化
     *
     * @param date 阳历日期
     */
    public Lunar(Date date) {
        this(Solar.from(date));
    }

    /**
     * 通过阳历初始化
     *
     * @param solar 阳历日期
     */
    public Lunar(Solar solar) {
        Year year = Year.from(solar.getYear());
        for (Month m : year.getMonths()) {
            int days = solar.subtract(Solar.from(m.getFirstJulianDay()));
            if (days < m.getDayCount()) {
                this.year = m.getYear();
                this.month = m.getMonth();
                this.day = days + 1;
                break;
            }
        }
        this.hour = solar.getHour();
        this.minute = solar.getMinute();
        this.second = solar.getSecond();
        this.solar = solar;
        this.initialize(year);
    }

    /**
     * 通过阳历日期初始化
     *
     * @param calendar 阳历日期
     */
    public Lunar(Calendar calendar) {
        this(calendar.getTime());
    }

    /**
     * 通过农历年月日初始化
     *
     * @param year  年（农历）
     * @param month 月（农历），1到12，闰月为负，即闰2月=-2
     * @param day   日（农历），1到30
     */
    public Lunar(int year, int month, int day) {
        this(year, month, day, 0, 0, 0);
    }

    /**
     * 通过农历年月日时初始化
     *
     * @param year   年（农历）
     * @param month  月（农历），1到12，闰月为负，即闰2月=-2
     * @param day    日（农历），1到30
     * @param hour   小时（阳历）
     * @param minute 分钟（阳历）
     * @param second 秒钟（阳历）
     */
    public Lunar(int year, int month, int day, int hour, int minute, int second) {
        Year y = Year.from(year);
        Month m = y.getMonth(month);
        if (null == m) {
            throw new IllegalArgumentException(String.format("wrong lunar year %d month %d", year, month));
        }
        if (day < 1) {
            throw new IllegalArgumentException("lunar day must bigger than 0");
        }
        int days = m.getDayCount();
        if (day > days) {
            throw new IllegalArgumentException(String.format("only %d days in lunar year %d month %d", days, year, month));
        }
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        Solar noon = Solar.from(m.getFirstJulianDay() + day - 1);
        this.solar = Solar.from(noon.getYear(), noon.getMonth(), noon.getDay(), hour, minute, second);
        if (noon.getYear() != year) {
            y = Year.from(noon.getYear());
        }
        this.initialize(y);
    }

    /**
     * 通过指定阳历日期获取农历
     *
     * @param date 阳历日期
     * @return 农历
     */
    public static Lunar from(Date date) {
        return new Lunar(date);
    }

    /**
     * 通过指定日历获取阳历
     *
     * @param calendar 日历
     * @return 阳历
     */
    public static Lunar from(Calendar calendar) {
        return new Lunar(calendar);
    }

    /**
     * 通过指定农历年月日获取农历
     *
     * @param year  年（农历）
     * @param month 月（农历），1到12，闰月为负，即闰2月=-2
     * @param day   日（农历），1到31
     * @return 农历
     */
    public static Lunar from(int year, int month, int day) {
        return new Lunar(year, month, day);
    }

    /**
     * 通过指定农历年月日获取农历
     *
     * @param year   年（农历）
     * @param month  月（农历），1到12，闰月为负，即闰2月=-2
     * @param day    日（农历），1到31
     * @param hour   小时（阳历）
     * @param minute 分钟（阳历）
     * @param second 秒钟（阳历）
     * @return 农历
     */
    public static Lunar from(int year, int month, int day, int hour, int minute, int second) {
        return new Lunar(year, month, day, hour, minute, second);
    }

    /**
     * 获取某年某月有多少天
     *
     * @param year  农历年
     * @param month 农历月，闰月为负数
     * @return 天数
     */
    public static int getDaysOfMonth(int year, int month) {
        int m = month - 1;
        int d = Fields.DAYS_OF_MONTH[m];
        //公历闰年2月多一天
        if (m == Calendar.FEBRUARY && Solar.isLeapYear(year)) {
            d++;
        }
        return d;
    }

    /**
     * 获取HH:mm时刻的地支序号，非法的时刻返回0
     *
     * @param hm HH:mm时刻
     * @return 地支序号，0到11
     */
    public static int getTimeZhiIndex(String hm) {
        if (null == hm) {
            return 0;
        }
        if (hm.length() > 5) {
            hm = hm.substring(0, 5);
        }
        int x = 1;
        for (int i = 1; i < 22; i += 2) {
            if (hm.compareTo((i < 10 ? "0" : Normal.EMPTY) + i + ":00") >= 0 && hm.compareTo((i + 1 < 10 ? "0" : Normal.EMPTY) + (i + 1) + ":59") <= 0) {
                return x;
            }
            x++;
        }
        return 0;
    }

    /**
     * 将HH:mm时刻转换为时辰（地支），非法的时刻返回子
     *
     * @param hm HH:mm时刻
     * @return 时辰(地支)，如子
     */
    public static String convertTime(String hm) {
        return Fields.CN_ZHI[getTimeZhiIndex(hm)];
    }

    /**
     * 数字转十六进制
     *
     * @param n 数字
     * @return 十六进制
     */
    private static String hex(int n) {
        String hex = Integer.toHexString(n & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 获取干支对应的甲子序号
     *
     * @param ganZhi 干支
     * @return 甲子序号
     */
    public static int getJiaZiIndex(String ganZhi) {
        for (int i = 0, j = Fields.CN_JIA_ZI.length; i < j; i++) {
            if (Fields.CN_JIA_ZI[i].equals(ganZhi)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取日宜
     *
     * @param monthGanZhi 月干支
     * @param dayGanZhi   日干支
     * @return 宜
     */
    public static List<String> getDayYi(String monthGanZhi, String dayGanZhi) {
        List<String> l = new ArrayList<>();
        String day = hex(getJiaZiIndex(dayGanZhi));
        String month = hex(getJiaZiIndex(monthGanZhi));
        String right = DAY_YI_JI;
        int index = right.indexOf(day + Symbol.EQUAL);
        while (index > -1) {
            right = right.substring(index + 3);
            String left = right;
            if (left.contains(Symbol.EQUAL)) {
                left = left.substring(0, left.indexOf(Symbol.EQUAL) - 2);
            }
            boolean matched = false;
            String months = left.substring(0, left.indexOf(Symbol.C_COLON));
            for (int i = 0, j = months.length(); i < j; i += 2) {
                if (month.equals(months.substring(i, i + 2))) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                String ys = left.substring(left.indexOf(Symbol.C_COLON) + 1);
                ys = ys.substring(0, ys.indexOf(Symbol.COMMA));
                for (int i = 0, j = ys.length(); i < j; i += 2) {
                    l.add(YI_JI[Integer.parseInt(ys.substring(i, i + 2), 16)]);
                }
                break;
            }
            index = right.indexOf(day + Symbol.EQUAL);
        }
        if (l.isEmpty()) {
            l.add("无");
        }
        return l;
    }

    /**
     * 获取日忌
     *
     * @param monthGanZhi 月干支
     * @param dayGanZhi   日干支
     * @return 忌
     */
    public static List<String> getDayJi(String monthGanZhi, String dayGanZhi) {
        List<String> l = new ArrayList<>();
        String day = hex(getJiaZiIndex(dayGanZhi));
        String month = hex(getJiaZiIndex(monthGanZhi));
        String right = DAY_YI_JI;
        int index = right.indexOf(day + Symbol.EQUAL);
        while (index > -1) {
            right = right.substring(index + 3);
            String left = right;
            if (left.contains(Symbol.EQUAL)) {
                left = left.substring(0, left.indexOf(Symbol.EQUAL) - 2);
            }
            boolean matched = false;
            String months = left.substring(0, left.indexOf(Symbol.C_COLON));
            for (int i = 0, j = months.length(); i < j; i += 2) {
                String m = months.substring(i, i + 2);
                if (m.equals(month)) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                String js = left.substring(left.indexOf(Symbol.COMMA) + 1);
                for (int i = 0, j = js.length(); i < j; i += 2) {
                    String m = js.substring(i, i + 2);
                    l.add(YI_JI[Integer.parseInt(m, 16)]);
                }
                break;
            }
            index = right.indexOf(day + Symbol.EQUAL);
        }
        if (l.isEmpty()) {
            l.add("无");
        }
        return l;
    }

    /**
     * 获取日吉神
     *
     * @param lunarMonth 月
     * @param dayGanZhi  日干支
     * @return 日吉神
     */
    public static List<String> getDayJiShen(int lunarMonth, String dayGanZhi) {
        List<String> l = new ArrayList<>();
        String day = hex(getJiaZiIndex(dayGanZhi));
        String month = Integer.toHexString(Math.abs(lunarMonth) & 0xFF).toUpperCase();
        int index = DAY_SHEN_SHA.indexOf(month + day + Symbol.EQUAL);
        if (index > -1) {
            String left = DAY_SHEN_SHA.substring(index + 4);
            if (left.contains(Symbol.EQUAL)) {
                left = left.substring(0, left.indexOf(Symbol.EQUAL) - 3);
            }
            String js = left.substring(0, left.indexOf(Symbol.COMMA));
            for (int i = 0, j = js.length(); i < j; i += 2) {
                String m = js.substring(i, i + 2);
                l.add(SHEN_SHA[Integer.parseInt(m, 16)]);
            }
        }
        if (l.isEmpty()) {
            l.add("无");
        }
        return l;
    }

    /**
     * 获取日凶煞
     *
     * @param lunarMonth 月
     * @param dayGanZhi  日干支
     * @return 日凶煞
     */
    public static List<String> getDayXiongSha(int lunarMonth, String dayGanZhi) {
        List<String> l = new ArrayList<>();
        String day = hex(getJiaZiIndex(dayGanZhi));
        String month = Integer.toHexString(Math.abs(lunarMonth) & 0xFF).toUpperCase();
        int index = DAY_SHEN_SHA.indexOf(month + day + Symbol.EQUAL);
        if (index > -1) {
            String left = DAY_SHEN_SHA.substring(index + 4);
            if (left.contains(Symbol.EQUAL)) {
                left = left.substring(0, left.indexOf(Symbol.EQUAL) - 3);
            }
            String xs = left.substring(left.indexOf(Symbol.COMMA) + 1);
            for (int i = 0, j = xs.length(); i < j; i += 2) {
                String m = xs.substring(i, i + 2);
                l.add(SHEN_SHA[Integer.parseInt(m, 16)]);
            }
        }
        if (l.isEmpty()) {
            l.add("无");
        }
        return l;
    }

    /**
     * 获取时辰宜
     *
     * @param dayGanZhi  日干支
     * @param timeGanZhi 时干支
     * @return 宜
     */
    public static List<String> getTimeYi(String dayGanZhi, String timeGanZhi) {
        List<String> l = new ArrayList<>();
        String day = hex(getJiaZiIndex(dayGanZhi));
        String time = hex(getJiaZiIndex(timeGanZhi));
        int index = TIME_YI_JI.indexOf(day + time + Symbol.EQUAL);
        if (index > -1) {
            String left = TIME_YI_JI.substring(index + 5);
            if (left.contains(Symbol.EQUAL)) {
                left = left.substring(0, left.indexOf(Symbol.EQUAL) - 4);
            }
            String ys = left.substring(0, left.indexOf(Symbol.COMMA));
            for (int i = 0, j = ys.length(); i < j; i += 2) {
                String m = ys.substring(i, i + 2);
                l.add(YI_JI[Integer.parseInt(m, 16)]);
            }
        }
        if (l.isEmpty()) {
            l.add("无");
        }
        return l;
    }

    /**
     * 获取时辰忌
     *
     * @param dayGanZhi  日干支
     * @param timeGanZhi 时干支
     * @return 忌
     */
    public static List<String> getTimeJi(String dayGanZhi, String timeGanZhi) {
        List<String> l = new ArrayList<>();
        String day = hex(getJiaZiIndex(dayGanZhi));
        String time = hex(getJiaZiIndex(timeGanZhi));
        int index = TIME_YI_JI.indexOf(day + time + Symbol.EQUAL);
        if (index > -1) {
            String left = TIME_YI_JI.substring(index + 5);
            if (left.contains(Symbol.EQUAL)) {
                left = left.substring(0, left.indexOf(Symbol.EQUAL) - 4);
            }
            String js = left.substring(left.indexOf(Symbol.COMMA) + 1);
            for (int i = 0, j = js.length(); i < j; i += 2) {
                String m = js.substring(i, i + 2);
                l.add(YI_JI[Integer.parseInt(m, 16)]);
            }
        }
        if (l.isEmpty()) {
            l.add("无");
        }
        return l;
    }

    /**
     * 获取干支所在旬下标，0-5
     *
     * @param ganZhi 干支
     * @return 旬下标，0-5
     */
    protected static int getXunIndex(String ganZhi) {
        String gan = ganZhi.substring(0, 1);
        String zhi = ganZhi.substring(1);
        int ganIndex = 0;
        int zhiIndex = 0;
        for (int i = 0, j = Fields.CN_GAN.length; i < j; i++) {
            if (Fields.CN_GAN[i].equals(gan)) {
                ganIndex = i;
                break;
            }
        }
        for (int i = 0, j = Fields.CN_ZHI.length; i < j; i++) {
            if (Fields.CN_ZHI[i].equals(zhi)) {
                zhiIndex = i;
                break;
            }
        }
        int diff = ganIndex - zhiIndex;
        if (diff < 0) {
            diff += 12;
        }
        return diff / 2;
    }

    /**
     * 获取干支所在旬
     *
     * @param ganZhi 干支
     * @return 旬
     */
    public static String getXun(String ganZhi) {
        return XUN[getXunIndex(ganZhi)];
    }

    /**
     * 获取干支所在旬对应的旬空(空亡)
     *
     * @param ganZhi 干支
     * @return 旬空(空亡)
     */
    public static String getXunKong(String ganZhi) {
        return XUN_KONG[getXunIndex(ganZhi)];
    }

    /**
     * 获取年份的天干（以正月初一作为新年的开始）
     *
     * @return 天干，如辛
     */
    public String getYearGan() {
        return Fields.CN_GAN[yearGanIndex];
    }

    /**
     * 获取年份的天干（以立春当天作为新年的开始）
     *
     * @return 天干，如辛
     */
    public String getYearGanByLiChun() {
        return Fields.CN_GAN[yearGanIndexByLiChun];
    }

    /**
     * 获取最精确的年份天干（以立春交接的时刻作为新年的开始）
     *
     * @return 天干，如辛
     */
    public String getYearGanExact() {
        return Fields.CN_GAN[yearGanIndexExact];
    }

    /**
     * 获取年份的地支（以正月初一作为新年的开始）
     *
     * @return 地支，如亥
     */
    public String getYearZhi() {
        return Fields.CN_ZHI[yearZhiIndex];
    }

    /**
     * 获取年份的地支（以立春当天作为新年的开始）
     *
     * @return 地支，如亥
     */
    public String getYearZhiByLiChun() {
        return Fields.CN_ZHI[yearZhiIndexByLiChun];
    }

    /**
     * 获取最精确的年份地支（以立春交接的时刻作为新年的开始）
     *
     * @return 地支，如亥
     */
    public String getYearZhiExact() {
        return Fields.CN_ZHI[yearZhiIndexExact];
    }

    /**
     * 获取干支纪年（年柱）（以正月初一作为新年的开始）
     *
     * @return 年份的干支（年柱），如辛亥
     */
    public String getYearInGanZhi() {
        return getYearGan() + getYearZhi();
    }

    /**
     * 获取干支纪年（年柱）（以立春当天作为新年的开始）
     *
     * @return 年份的干支（年柱），如辛亥
     */
    public String getYearInGanZhiByLiChun() {
        return getYearGanByLiChun() + getYearZhiByLiChun();
    }

    /**
     * 获取干支纪年（年柱）（以立春交接的时刻作为新年的开始）
     *
     * @return 年份的干支（年柱），如辛亥
     */
    public String getYearInGanZhiExact() {
        return getYearGanExact() + getYearZhiExact();
    }

    /**
     * 获取干支纪月（月柱）（以节交接当天起算）
     * <p>月天干口诀：甲己丙寅首，乙庚戊寅头。丙辛从庚寅，丁壬壬寅求，戊癸甲寅居，周而复始流。</p>
     * <p>月地支：正月起寅</p>
     *
     * @return 干支纪月（月柱），如己卯
     */
    public String getMonthInGanZhi() {
        return getMonthGan() + getMonthZhi();
    }

    /**
     * 获取精确的干支纪月（月柱）（以节交接时刻起算）
     * <p>月天干口诀：甲己丙寅首，乙庚戊寅头。丙辛从庚寅，丁壬壬寅求，戊癸甲寅居，周而复始流。</p>
     * <p>月地支：正月起寅</p>
     *
     * @return 干支纪月（月柱），如己卯
     */
    public String getMonthInGanZhiExact() {
        return getMonthGanExact() + getMonthZhiExact();
    }

    /**
     * 获取月天干（以节交接当天起算）
     *
     * @return 月天干，如己
     */
    public String getMonthGan() {
        return Fields.CN_GAN[monthGanIndex];
    }

    /**
     * 获取精确的月天干（以节交接时刻起算）
     *
     * @return 月天干，如己
     */
    public String getMonthGanExact() {
        return Fields.CN_GAN[monthGanIndexExact];
    }

    /**
     * 获取月地支（以节交接当天起算）
     *
     * @return 月地支，如卯
     */
    public String getMonthZhi() {
        return Fields.CN_ZHI[monthZhiIndex];
    }

    /**
     * 获取精确的月地支（以节交接时刻起算）
     *
     * @return 月地支，如卯
     */
    public String getMonthZhiExact() {
        return Fields.CN_ZHI[monthZhiIndexExact];
    }

    /**
     * 获取干支纪日（日柱）
     *
     * @return 干支纪日（日柱），如己卯
     */
    public String getDayInGanZhi() {
        return getDayGan() + getDayZhi();
    }

    /**
     * 获取干支纪日（日柱，晚子时日柱算明天）
     *
     * @return 干支纪日（日柱），如己卯
     */
    public String getDayInGanZhiExact() {
        return getDayGanExact() + getDayZhiExact();
    }

    /**
     * 获取干支纪日（日柱，晚子时日柱算当天）
     *
     * @return 干支纪日（日柱），如己卯
     */
    public String getDayInGanZhiExact2() {
        return getDayGanExact2() + getDayZhiExact2();
    }

    /**
     * 获取日天干
     *
     * @return 日天干，如甲
     */
    public String getDayGan() {
        return Fields.CN_GAN[dayGanIndex];
    }

    /**
     * 获取日天干（晚子时日柱算明天）
     *
     * @return 日天干，如甲
     */
    public String getDayGanExact() {
        return Fields.CN_GAN[dayGanIndexExact];
    }

    /**
     * 获取日天干（晚子时日柱算当天）
     *
     * @return 日天干，如甲
     */
    public String getDayGanExact2() {
        return Fields.CN_GAN[dayGanIndexExact2];
    }

    /**
     * 获取日地支
     *
     * @return 日地支，如卯
     */
    public String getDayZhi() {
        return Fields.CN_ZHI[dayZhiIndex];
    }

    /**
     * 获取日地支（晚子时日柱算明天）
     *
     * @return 日地支，如卯
     */
    public String getDayZhiExact() {
        return Fields.CN_ZHI[dayZhiIndexExact];
    }

    /**
     * 获取日地支（晚子时日柱算当天）
     *
     * @return 日地支，如卯
     */
    public String getDayZhiExact2() {
        return Fields.CN_ZHI[dayZhiIndexExact2];
    }

    /**
     * 获取年生肖（以正月初一起算）
     *
     * @return 年生肖，如虎
     */
    public String getYearAnimal() {
        return Fields.CN_ANIMAL[yearZhiIndex];
    }

    /**
     * 获取年生肖（以立春当天起算）
     *
     * @return 年生肖，如虎
     */
    public String getYearAnimalByLiChun() {
        return Fields.CN_ANIMAL[yearZhiIndexByLiChun];
    }

    /**
     * 获取精确的年生肖（以立春交接时刻起算）
     *
     * @return 年生肖，如虎
     */
    public String getYearAnimalExact() {
        return Fields.CN_ANIMAL[yearZhiIndexExact];
    }

    /**
     * 获取月生肖
     *
     * @return 月生肖，如虎
     */
    public String getMonthAnimal() {
        return Fields.CN_ANIMAL[monthZhiIndex];
    }

    /**
     * 获取日生肖
     *
     * @return 日生肖，如虎
     */
    public String getDayAnimal() {
        return Fields.CN_ANIMAL[dayZhiIndex];
    }

    /**
     * 获取时辰生肖
     *
     * @return 时辰生肖，如虎
     */
    public String getTimeAnimal() {
        return Fields.CN_ANIMAL[timeZhiIndex];
    }

    /**
     * 获取中文的年
     *
     * @return 中文年，如二零零一
     */
    public String getYearInChinese() {
        String y = Normal.EMPTY + year;
        StringBuilder s = new StringBuilder();
        for (int i = 0, j = y.length(); i < j; i++) {
            s.append(Fields.CN_NUMBER[y.charAt(i) - '0']);
        }
        return s.toString();
    }

    /**
     * 获取中文的月
     *
     * @return 中文月，如正
     */
    public String getMonthInChinese() {
        return (month < 0 ? "闰" : Normal.EMPTY) + Fields.CN_MONTH[Math.abs(month) - 1];
    }

    /**
     * 获取中文日
     *
     * @return 中文日，如初一
     */
    public String getDayInChinese() {
        return Fields.CN_DAY[day - 1];
    }

    /**
     * 获取时辰（地支）
     *
     * @return 时辰（地支）
     */
    public String getTimeZhi() {
        return Fields.CN_ZHI[timeZhiIndex];
    }

    /**
     * 获取时辰（天干）
     *
     * @return 时辰（天干）
     */
    public String getTimeGan() {
        return Fields.CN_GAN[timeGanIndex];
    }

    /**
     * 获取时辰干支（时柱），支持早子时和晚子时
     *
     * @return 时辰干支（时柱）
     */
    public String getTimeInGanZhi() {
        return getTimeGan() + getTimeZhi();
    }

    /**
     * 获取季节
     *
     * @return 农历季节
     */
    public String getSeason() {
        return Fields.CN_SEASON[Math.abs(month)];
    }

    protected String convertJieQi(String name) {
        String jq = name;
        if ("DONG_ZHI".equals(jq)) {
            jq = "冬至";
        } else if ("DA_HAN".equals(jq)) {
            jq = "大寒";
        } else if ("XIAO_HAN".equals(jq)) {
            jq = "小寒";
        } else if ("LI_CHUN".equals(jq)) {
            jq = "立春";
        } else if ("DA_XUE".equals(jq)) {
            jq = "大雪";
        } else if ("YU_SHUI".equals(jq)) {
            jq = "雨水";
        } else if ("JING_ZHE".equals(jq)) {
            jq = "惊蛰";
        }
        return jq;
    }

    /**
     * 获取节令
     *
     * @return 节令
     */
    public String getJie() {
        for (int i = 0, j = JIE_QI_IN_USE.length; i < j; i += 2) {
            String key = JIE_QI_IN_USE[i];
            Solar d = solarTerm.get(key);
            if (d.getYear() == solar.getYear() && d.getMonth() == solar.getMonth() && d.getDay() == solar.getDay()) {
                return convertJieQi(key);
            }
        }
        return Normal.EMPTY;
    }

    /**
     * 获取气令
     *
     * @return 气令
     */
    public String getQi() {
        for (int i = 1, j = JIE_QI_IN_USE.length; i < j; i += 2) {
            String key = JIE_QI_IN_USE[i];
            Solar d = solarTerm.get(key);
            if (d.getYear() == solar.getYear() && d.getMonth() == solar.getMonth() && d.getDay() == solar.getDay()) {
                return convertJieQi(key);
            }
        }
        return Normal.EMPTY;
    }

    /**
     * 获取星期，1代表周日
     *
     * @return 1234567
     */
    public int getWeek() {
        return weekIndex;
    }

    /**
     * 获取星期的中文
     *
     * @return 星期x
     */
    public String getWeekInChinese() {
        return Fields.Week.getCnNameByCode(getWeek());
    }

    /**
     * 获取宿
     *
     * @return 宿
     */
    public String getXiu() {
        return XIU.get(getDayZhi() + (getWeek() - 1));
    }

    /**
     * 获取宿吉凶
     *
     * @return 吉/凶
     */
    public String getXiuLuck() {
        return XIU_LUCK.get(getXiu());
    }

    /**
     * 获取宿歌诀
     *
     * @return 宿歌诀
     */
    public String getXiuSong() {
        return XIU_SONG.get(getXiu());
    }

    /**
     * 获取政
     *
     * @return 政
     */
    public String getZheng() {
        return ZHENG.get(getXiu());
    }

    /**
     * 获取动物
     *
     * @return 动物
     */
    public String getAnimal() {
        return ANIMAL.get(getXiu());
    }

    /**
     * 获取宫
     *
     * @return 宫
     */
    public String getGong() {
        return GONG.get(getXiu());
    }

    /**
     * 获取兽
     *
     * @return 兽
     */
    public String getShou() {
        return SHOU.get(getGong());
    }

    /**
     * 获取节日，有可能一天会有多个节日
     *
     * @return 节日列表，如春节
     */
    public List<String> getFestivals() {
        List<String> l = new ArrayList<>();
        String f = FESTIVAL.get(month + Symbol.MINUS + day);
        if (null != f) {
            l.add(f);
        }
        if (Math.abs(month) == 12 && day >= 29 && year != next(1).getYear()) {
            l.add("除夕");
        }
        return l;
    }

    /**
     * 获取非正式的节日，有可能一天会有多个节日
     *
     * @return 非正式的节日列表，如中元节
     */
    public List<String> getOtherFestivals() {
        List<String> l = new ArrayList<>();
        List<String> fs = OTHER_FESTIVAL.get(month + Symbol.MINUS + day);
        if (null != fs) {
            l.addAll(fs);
        }
        String solarYmd = solar.toYmd();
        if (solarYmd.equals(solarTerm.get("清明").next(-1).build())) {
            l.add("寒食节");
        }
        Solar jq = solarTerm.get("立春");
        int offset = 4 - jq.getLunar().getDayGanIndex();
        if (offset < 0) {
            offset += 10;
        }
        if (solarYmd.equals(jq.next(offset + 40).toYmd())) {
            l.add("春社");
        }

        jq = solarTerm.get("立秋");
        offset = 4 - jq.getLunar().getDayGanIndex();
        if (offset < 0) {
            offset += 10;
        }
        if (solarYmd.equals(jq.next(offset + 40).toYmd())) {
            l.add("秋社");
        }
        return l;
    }

    /**
     * 获取彭祖百忌天干
     *
     * @return 彭祖百忌天干
     */
    public String getPengZuGan() {
        return PENGZU_GAN[dayGanIndex];
    }

    /**
     * 获取彭祖百忌地支
     *
     * @return 彭祖百忌地支
     */
    public String getPengZuZhi() {
        return PENGZU_ZHI[dayZhiIndex];
    }

    /**
     * 获取日喜神方位
     *
     * @return 方位，如艮
     */
    public String getDayPositionXi() {
        return POSITION_XI[dayGanIndex];
    }

    /**
     * 获取日喜神方位描述
     *
     * @return 方位描述，如东北
     */
    public String getDayPositionXiDesc() {
        return POSITION_DESC.get(getDayPositionXi());
    }

    /**
     * 获取日阳贵神方位
     *
     * @return 方位，如艮
     */
    public String getDayPositionYangGui() {
        return POSITION_YANG_GUI[dayGanIndex];
    }

    /**
     * 获取日阳贵神方位描述
     *
     * @return 方位描述，如东北
     */
    public String getDayPositionYangGuiDesc() {
        return POSITION_DESC.get(getDayPositionYangGui());
    }

    /**
     * 获取日阴贵神方位
     *
     * @return 方位，如艮
     */
    public String getDayPositionYinGui() {
        return POSITION_YIN_GUI[dayGanIndex];
    }

    /**
     * 获取日阴贵神方位描述
     *
     * @return 方位描述，如东北
     */
    public String getDayPositionYinGuiDesc() {
        return POSITION_DESC.get(getDayPositionYinGui());
    }

    /**
     * 获取日福神方位（默认流派：2）
     *
     * @return 方位，如艮
     */
    public String getDayPositionFu() {
        return getDayPositionFu(2);
    }

    /**
     * 获取日福神方位
     *
     * @param sect 流派，1或2
     * @return 方位，如艮
     */
    public String getDayPositionFu(int sect) {
        return (1 == sect ? POSITION_FU : POSITION_FU_2)[dayGanIndex + 1];
    }

    /**
     * 获取日福神方位描述（默认流派：2）
     *
     * @return 方位描述，如东北
     */
    public String getDayPositionFuDesc() {
        return getDayPositionFuDesc(2);
    }

    /**
     * 获取日福神方位描述
     *
     * @param sect 流派，1或2
     * @return 方位描述，如东北
     */
    public String getDayPositionFuDesc(int sect) {
        return POSITION_DESC.get(getDayPositionFu(sect));
    }

    /**
     * 获取日财神方位
     *
     * @return 方位，如艮
     */
    public String getDayPositionCai() {
        return POSITION_CAI[dayGanIndex];
    }

    /**
     * 获取日财神方位描述
     *
     * @return 方位描述，如东北
     */
    public String getDayPositionCaiDesc() {
        return POSITION_DESC.get(getDayPositionCai());
    }

    /**
     * 获取年太岁方位（默认流派2新年以立春零点起算）
     *
     * @return 方位，如艮
     */
    public String getYearPositionTaiSui() {
        return getYearPositionTaiSui(2);
    }

    /**
     * 获取年太岁方位
     *
     * @param sect 流派：2为新年以立春零点起算；1为新年以正月初一起算；3为新年以立春节气交接的时刻起算
     * @return 方位，如艮
     */
    public String getYearPositionTaiSui(int sect) {
        int yearZhiIndex;
        switch (sect) {
            case 1:
                yearZhiIndex = this.yearZhiIndex;
                break;
            case 3:
                yearZhiIndex = this.yearZhiIndexExact;
                break;
            default:
                yearZhiIndex = this.yearZhiIndexByLiChun;
        }
        return POSITION_TAI_SUI_YEAR[yearZhiIndex];
    }

    /**
     * 获取年太岁方位描述（默认流派2新年以立春零点起算）
     *
     * @return 太岁方位描述，如东北
     */
    public String getYearPositionTaiSuiDesc() {
        return getYearPositionTaiSuiDesc(2);
    }

    /**
     * 获取年太岁方位描述
     *
     * @param sect 流派：2为新年以立春零点起算；1为新年以正月初一起算；3为新年以立春节气交接的时刻起算
     * @return 方位描述，如东北
     */
    public String getYearPositionTaiSuiDesc(int sect) {
        return POSITION_DESC.get(getYearPositionTaiSui(sect));
    }

    protected String getMonthPositionTaiSui(int monthZhiIndex, int monthGanIndex) {
        String p;
        int m = monthZhiIndex - BASE_MONTH_ZHI_INDEX;
        if (m < 0) {
            m += 12;
        }
        m = m % 4;
        switch (m) {
            case 0:
                p = "艮";
                break;
            case 2:
                p = "坤";
                break;
            case 3:
                p = "巽";
                break;
            default:
                p = POSITION_GAN[monthGanIndex];
        }
        return p;
    }

    /**
     * 获取月太岁方位（默认流派2新的一月以节交接当天零点起算）
     *
     * @return 方位，如艮
     */
    public String getMonthPositionTaiSui() {
        return getMonthPositionTaiSui(2);
    }

    /**
     * 获取月太岁方位
     *
     * @param sect 流派：2为新的一月以节交接当天零点起算；3为新的一月以节交接准确时刻起算
     * @return 方位，如艮
     */
    public String getMonthPositionTaiSui(int sect) {
        int monthZhiIndex;
        int monthGanIndex;
        if (sect == 3) {
            monthZhiIndex = this.monthZhiIndexExact;
            monthGanIndex = this.monthGanIndexExact;
        } else {
            monthZhiIndex = this.monthZhiIndex;
            monthGanIndex = this.monthGanIndex;
        }
        return getMonthPositionTaiSui(monthZhiIndex, monthGanIndex);
    }

    /**
     * 获取月太岁方位描述（默认流派2新的一月以节交接当天零点起算）
     *
     * @return 方位描述，如东北
     */
    public String getMonthPositionTaiSuiDesc() {
        return getMonthPositionTaiSuiDesc(2);
    }

    /**
     * 获取月太岁方位描述
     *
     * @param sect 流派：2为新的一月以节交接当天零点起算；3为新的一月以节交接准确时刻起算
     * @return 方位描述，如东北
     */
    public String getMonthPositionTaiSuiDesc(int sect) {
        return POSITION_DESC.get(getMonthPositionTaiSui(sect));
    }

    protected String getDayPositionTaiSui(String dayInGanZhi, int yearZhiIndex) {
        String p;
        if ("甲子,乙丑,丙寅,丁卯,戊辰,已巳".contains(dayInGanZhi)) {
            p = "震";
        } else if ("丙子,丁丑,戊寅,已卯,庚辰,辛巳".contains(dayInGanZhi)) {
            p = "离";
        } else if ("戊子,已丑,庚寅,辛卯,壬辰,癸巳".contains(dayInGanZhi)) {
            p = "中";
        } else if ("庚子,辛丑,壬寅,癸卯,甲辰,乙巳".contains(dayInGanZhi)) {
            p = "兑";
        } else if ("壬子,癸丑,甲寅,乙卯,丙辰,丁巳".contains(dayInGanZhi)) {
            p = "坎";
        } else {
            p = POSITION_TAI_SUI_YEAR[yearZhiIndex];
        }
        return p;
    }

    /**
     * 获取日太岁方位（默认流派2新年以立春零点起算）
     *
     * @return 方位，如艮
     */
    public String getDayPositionTaiSui() {
        return getDayPositionTaiSui(2);
    }

    /**
     * 获取日太岁方位
     *
     * @param sect 流派：2新年以立春零点起算；1新年以正月初一起算；3新年以立春节气交接的时刻起算
     * @return 方位，如艮
     */
    public String getDayPositionTaiSui(int sect) {
        String dayInGanZhi;
        int yearZhiIndex;
        switch (sect) {
            case 1:
                dayInGanZhi = getDayInGanZhi();
                yearZhiIndex = this.yearZhiIndex;
                break;
            case 3:
                dayInGanZhi = getDayInGanZhi();
                yearZhiIndex = this.yearZhiIndexExact;
                break;
            default:
                dayInGanZhi = getDayInGanZhiExact2();
                yearZhiIndex = this.yearZhiIndexByLiChun;
        }
        return getDayPositionTaiSui(dayInGanZhi, yearZhiIndex);
    }

    /**
     * 获取日太岁方位描述（默认流派2新年以立春零点起算）
     *
     * @return 方位描述，如东北
     */
    public String getDayPositionTaiSuiDesc() {
        return getDayPositionTaiSuiDesc(2);
    }

    /**
     * 获取日太岁方位描述
     *
     * @param sect 流派：2新年以立春零点起算；1新年以正月初一起算；3新年以立春节气交接的时刻起算
     * @return 方位描述，如东北
     */
    public String getDayPositionTaiSuiDesc(int sect) {
        return POSITION_DESC.get(getDayPositionTaiSui(sect));
    }

    /**
     * 获取时辰喜神方位
     *
     * @return 方位，如艮
     */
    public String getTimePositionXi() {
        return POSITION_XI[timeGanIndex];
    }

    /**
     * 获取时辰喜神方位描述
     *
     * @return 喜神方位描述，如东北
     */
    public String getTimePositionXiDesc() {
        return POSITION_DESC.get(getTimePositionXi());
    }

    /**
     * 获取时辰阳贵神方位
     *
     * @return 方位，如艮
     */
    public String getTimePositionYangGui() {
        return POSITION_YANG_GUI[timeGanIndex];
    }

    /**
     * 获取时辰阳贵神方位描述
     *
     * @return 方位描述，如东北
     */
    public String getTimePositionYangGuiDesc() {
        return POSITION_DESC.get(getTimePositionYangGui());
    }

    /**
     * 获取时辰阴贵神方位
     *
     * @return 方位，如艮
     */
    public String getTimePositionYinGui() {
        return POSITION_YIN_GUI[timeGanIndex];
    }

    /**
     * 获取时辰阴贵神方位描述
     *
     * @return 方位描述，如东北
     */
    public String getTimePositionYinGuiDesc() {
        return POSITION_DESC.get(getTimePositionYinGui());
    }

    /**
     * 获取时辰福神方位，默认流派2
     *
     * @return 方位，如艮
     */
    public String getTimePositionFu() {
        return getTimePositionFu(2);
    }

    /**
     * 获取时辰福神方位
     *
     * @param sect 流派，1或2
     * @return 方位，如艮
     */
    public String getTimePositionFu(int sect) {
        return (1 == sect ? POSITION_FU : POSITION_FU_2)[timeGanIndex + 1];
    }

    /**
     * 获取时辰福神方位描述，默认流派2
     *
     * @return 方位描述，如东北
     */
    public String getTimePositionFuDesc() {
        return getTimePositionFuDesc(2);
    }

    /**
     * 获取时辰福神方位描述
     *
     * @param sect 流派，1或2
     * @return 方位描述，如东北
     */
    public String getTimePositionFuDesc(int sect) {
        return POSITION_DESC.get(getTimePositionFu(sect));
    }

    /**
     * 获取时辰财神方位
     *
     * @return 方位，如艮
     */
    public String getTimePositionCai() {
        return POSITION_CAI[timeGanIndex];
    }

    /**
     * 获取时辰财神方位描述
     *
     * @return 方位描述，如东北
     */
    public String getTimePositionCaiDesc() {
        return POSITION_DESC.get(getTimePositionCai());
    }

    /**
     * 获取年纳音
     *
     * @return 年纳音，如剑锋金
     */
    public String getYearNaYin() {
        return NAYIN.get(getYearInGanZhi());
    }

    /**
     * 获取月纳音
     *
     * @return 月纳音，如剑锋金
     */
    public String getMonthNaYin() {
        return NAYIN.get(getMonthInGanZhi());
    }

    /**
     * 获取日纳音
     *
     * @return 日纳音，如剑锋金
     */
    public String getDayNaYin() {
        return NAYIN.get(getDayInGanZhi());
    }

    /**
     * 获取时辰纳音
     *
     * @return 时辰纳音，如剑锋金
     */
    public String getTimeNaYin() {
        return NAYIN.get(getTimeInGanZhi());
    }

    /**
     * 获取十二执星：建、除、满、平、定、执、破、危、成、收、开、闭。当月支与日支相同即为建，依次类推
     *
     * @return 执星
     */
    public String getZhiXing() {
        int offset = dayZhiIndex - monthZhiIndex;
        if (offset < 0) {
            offset += 12;
        }
        return ZHI_XING[offset];
    }

    /**
     * 获取值日天神
     *
     * @return 值日天神
     */
    public String getDayTianShen() {
        return TIAN_SHEN[(dayZhiIndex + ZHI_TIAN_SHEN_OFFSET.get(getMonthZhi())) % 12 + 1];
    }

    /**
     * 获取值时天神
     *
     * @return 值时天神
     */
    public String getTimeTianShen() {
        return TIAN_SHEN[(timeZhiIndex + ZHI_TIAN_SHEN_OFFSET.get(getDayZhiExact())) % 12 + 1];
    }

    /**
     * 获取值日天神类型：黄道/黑道
     *
     * @return 值日天神类型：黄道/黑道
     */
    public String getDayTianShenType() {
        return TIAN_SHEN_TYPE.get(getDayTianShen());
    }

    /**
     * 获取值时天神类型：黄道/黑道
     *
     * @return 值时天神类型：黄道/黑道
     */
    public String getTimeTianShenType() {
        return TIAN_SHEN_TYPE.get(getTimeTianShen());
    }

    /**
     * 获取值日天神吉凶
     *
     * @return 吉/凶
     */
    public String getDayTianShenLuck() {
        return TIAN_SHEN_TYPE_LUCK.get(getDayTianShenType());
    }

    /**
     * 获取值时天神吉凶
     *
     * @return 吉/凶
     */
    public String getTimeTianShenLuck() {
        return TIAN_SHEN_TYPE_LUCK.get(getTimeTianShenType());
    }

    /**
     * 获取逐日胎神方位
     *
     * @return 逐日胎神方位
     */
    public String getDayPositionTai() {
        return POSITION_TAI_DAY[getJiaZiIndex(getDayInGanZhi())];
    }

    /**
     * 获取逐月胎神方位，闰月无
     *
     * @return 逐月胎神方位
     */
    public String getMonthPositionTai() {
        if (month < 0) {
            return Normal.EMPTY;
        }
        return POSITION_TAI_MONTH[month - 1];
    }

    /**
     * 使用默认流派1（以节交接当天起算月）获取每日宜，如果没有，返回["无"]
     *
     * @return 宜
     */
    public List<String> getDayYi() {
        return getDayYi(1);
    }

    /**
     * 获取每日宜，如果没有，返回["无"]
     *
     * @param sect 流派，1以节交接当天起算月，2以节交接时刻起算月
     * @return 宜
     */
    public List<String> getDayYi(int sect) {
        return getDayYi(2 == sect ? getMonthInGanZhiExact() : getMonthInGanZhi(), getDayInGanZhi());
    }

    /**
     * 使用默认流派1（以节交接当天起算月）获取每日忌，如果没有，返回["无"]
     *
     * @return 忌
     */
    public List<String> getDayJi() {
        return getDayJi(1);
    }

    /**
     * 获取每日忌，如果没有，返回["无"]
     *
     * @param sect 流派，1以节交接当天起算月，2以节交接时刻起算月
     * @return 忌
     */
    public List<String> getDayJi(int sect) {
        return getDayJi(2 == sect ? getMonthInGanZhiExact() : getMonthInGanZhi(), getDayInGanZhi());
    }

    /**
     * 获取日吉神（宜趋），如果没有，返回["无"]
     *
     * @return 日吉神
     */
    public List<String> getDayJiShen() {
        return getDayJiShen(getMonth(), getDayInGanZhi());
    }

    /**
     * 获取日凶煞（宜忌），如果没有，返回["无"]
     *
     * @return 日凶煞
     */
    public List<String> getDayXiongSha() {
        return getDayXiongSha(getMonth(), getDayInGanZhi());
    }

    /**
     * 获取日冲
     *
     * @return 日冲，如申
     */
    public String getDayChong() {
        return CHONG[dayZhiIndex];
    }

    /**
     * 获取日煞
     *
     * @return 日煞，如北
     */
    public String getDaySha() {
        return SHA.get(getDayZhi());
    }

    /**
     * 获取日冲描述
     *
     * @return 日冲描述，如(壬申)猴
     */
    public String getDayChongDesc() {
        return Symbol.PARENTHESE_LEFT + getDayChongGan() + getDayChong() + Symbol.PARENTHESE_RIGHT + getDayChongAnimal();
    }

    /**
     * 获取日冲生肖
     *
     * @return 日冲生肖，如猴
     */
    public String getDayChongAnimal() {
        String chong = getDayChong();
        for (int i = 0, j = Fields.CN_ZHI.length; i < j; i++) {
            if (Fields.CN_ZHI[i].equals(chong)) {
                return Fields.CN_ANIMAL[i];
            }
        }
        return Normal.EMPTY;
    }

    /**
     * 获取无情之克的日冲天干
     *
     * @return 无情之克的日冲天干，如甲
     */
    public String getDayChongGan() {
        return CHONG_GAN[dayGanIndex];
    }

    /**
     * 获取有情之克的日冲天干
     *
     * @return 有情之克的日冲天干，如甲
     */
    public String getDayChongGanTie() {
        return CHONG_GAN_TIE[dayGanIndex];
    }

    /**
     * 获取时冲
     *
     * @return 时冲，如申
     */
    public String getTimeChong() {
        return CHONG[timeZhiIndex];
    }

    /**
     * 获取时煞
     *
     * @return 时煞，如北
     */
    public String getTimeSha() {
        return SHA.get(getTimeZhi());
    }

    /**
     * 获取时冲生肖
     *
     * @return 时冲生肖，如猴
     */
    public String getTimeChongAnimal() {
        String chong = getTimeChong();
        for (int i = 0, j = Fields.CN_ZHI.length; i < j; i++) {
            if (Fields.CN_ZHI[i].equals(chong)) {
                return Fields.CN_ANIMAL[i];
            }
        }
        return Normal.EMPTY;
    }

    /**
     * 获取时冲描述
     *
     * @return 时冲描述，如(壬申)猴
     */
    public String getTimeChongDesc() {
        return Symbol.PARENTHESE_LEFT + getTimeChongGan() + getTimeChong() + Symbol.PARENTHESE_RIGHT + getTimeChongAnimal();
    }

    /**
     * 获取无情之克的时冲天干
     *
     * @return 无情之克的时冲天干，如甲
     */
    public String getTimeChongGan() {
        return CHONG_GAN[timeGanIndex];
    }

    /**
     * 获取有情之克的时冲天干
     *
     * @return 有情之克的时冲天干，如甲
     */
    public String getTimeChongGanTie() {
        return CHONG_GAN_TIE[timeGanIndex];
    }

    /**
     * 获取时辰宜，如果没有，返回["无"]
     *
     * @return 宜
     */
    public List<String> getTimeYi() {
        return getTimeYi(getDayInGanZhiExact(), getTimeInGanZhi());
    }

    /**
     * 获取时辰忌，如果没有，返回["无"]
     *
     * @return 忌
     */
    public List<String> getTimeJi() {
        return getTimeJi(getDayInGanZhiExact(), getTimeInGanZhi());
    }

    /**
     * 获取月相
     *
     * @return 月相
     */
    public String getYueXiang() {
        return YUE_XIANG[day - 1];
    }

    protected NineStar getYearNineStar(String yearInGanZhi) {
        int indexExact = getJiaZiIndex(yearInGanZhi) + 1;
        int index = getJiaZiIndex(this.getYearInGanZhi()) + 1;
        int yearOffset = indexExact - index;
        if (yearOffset > 1) {
            yearOffset -= 60;
        } else if (yearOffset < -1) {
            yearOffset += 60;
        }
        int yuan = ((this.year + yearOffset + 2696) / 60) % 3;
        int offset = (62 + yuan * 3 - indexExact) % 9;
        if (0 == offset) {
            offset = 9;
        }
        return new NineStar(offset - 1);
    }

    /**
     * 获取值年九星（默认流派2新年以立春零点起算。流年紫白星起例歌诀：年上吉星论甲子，逐年星逆中宫起；上中下作三元汇，一上四中七下兑。）
     *
     * @return 九星
     */
    public NineStar getYearNineStar() {
        return getYearNineStar(2);
    }

    /**
     * 获取值年九星（流年紫白星起例歌诀：年上吉星论甲子，逐年星逆中宫起；上中下作三元汇，一上四中七下兑。）
     *
     * @param sect 流派：2为新年以立春零点起算；1为新年以正月初一起算；3为新年以立春节气交接的时刻起算
     * @return 九星
     */
    public NineStar getYearNineStar(int sect) {
        String yearInGanZhi;
        switch (sect) {
            case 1:
                yearInGanZhi = this.getYearInGanZhi();
                break;
            case 3:
                yearInGanZhi = this.getYearInGanZhiExact();
                break;
            default:
                yearInGanZhi = this.getYearInGanZhiByLiChun();
        }
        return getYearNineStar(yearInGanZhi);
    }

    protected NineStar getMonthNineStar(int yearZhiIndex, int monthZhiIndex) {
        int index = yearZhiIndex % 3;
        int n = 27 - (index * 3);
        if (monthZhiIndex < BASE_MONTH_ZHI_INDEX) {
            n -= 3;
        }
        int offset = (n - monthZhiIndex) % 9;
        return new NineStar(offset);
    }

    /**
     * 获取值月九星（流派2新的一月以节交接当天零点起算。月紫白星歌诀：子午卯酉八白起，寅申巳亥二黑求，辰戌丑未五黄中。）
     *
     * @return 九星
     */
    public NineStar getMonthNineStar() {
        return getMonthNineStar(2);
    }

    /**
     * 获取值月九星（月紫白星歌诀：子午卯酉八白起，寅申巳亥二黑求，辰戌丑未五黄中。）
     *
     * @param sect 流派：2为新的一月以节交接当天零点起算；3为新的一月以节交接准确时刻起算
     * @return 九星
     */
    public NineStar getMonthNineStar(int sect) {
        int yearZhiIndex;
        int monthZhiIndex;
        switch (sect) {
            case 1:
                yearZhiIndex = this.yearZhiIndex;
                monthZhiIndex = this.monthZhiIndex;
                break;
            case 3:
                yearZhiIndex = this.yearZhiIndexExact;
                monthZhiIndex = this.monthZhiIndexExact;
                break;
            default:
                yearZhiIndex = this.yearZhiIndexByLiChun;
                monthZhiIndex = this.monthZhiIndex;
        }
        return getMonthNineStar(yearZhiIndex, monthZhiIndex);
    }

    /**
     * 获取值日九星
     * （日家紫白星歌诀：日家白法不难求，二十四气六宫周；冬至雨水及谷雨，阳顺一七四中游；夏至处暑霜降后，九三六星逆行求。）
     *
     * @return 九星
     */
    public NineStar getDayNineStar() {
        String solarYmd = solar.build(false);
        Solar dongZhi = solarTerm.get("冬至");
        Solar dongZhi2 = solarTerm.get("DONG_ZHI");
        Solar xiaZhi = solarTerm.get("夏至");
        int dongZhiIndex = getJiaZiIndex(dongZhi.getLunar().getDayInGanZhi());
        int dongZhiIndex2 = getJiaZiIndex(dongZhi2.getLunar().getDayInGanZhi());
        int xiaZhiIndex = getJiaZiIndex(xiaZhi.getLunar().getDayInGanZhi());
        Solar solarShunBai;
        Solar solarShunBai2;
        Solar solarNiZi;
        if (dongZhiIndex > 29) {
            solarShunBai = dongZhi.next(60 - dongZhiIndex);
        } else {
            solarShunBai = dongZhi.next(-dongZhiIndex);
        }
        String solarShunBaiYmd = solarShunBai.build(false);
        if (dongZhiIndex2 > 29) {
            solarShunBai2 = dongZhi2.next(60 - dongZhiIndex2);
        } else {
            solarShunBai2 = dongZhi2.next(-dongZhiIndex2);
        }
        String solarShunBaiYmd2 = solarShunBai2.build(false);
        if (xiaZhiIndex > 29) {
            solarNiZi = xiaZhi.next(60 - xiaZhiIndex);
        } else {
            solarNiZi = xiaZhi.next(-xiaZhiIndex);
        }
        String solarNiZiYmd = solarNiZi.build(false);
        int offset = 0;
        if (solarYmd.compareTo(solarShunBaiYmd) >= 0 && solarYmd.compareTo(solarNiZiYmd) < 0) {
            offset = solar.subtract(solarShunBai) % 9;
        } else if (solarYmd.compareTo(solarNiZiYmd) >= 0 && solarYmd.compareTo(solarShunBaiYmd2) < 0) {
            offset = 8 - (solar.subtract(solarNiZi) % 9);
        } else if (solarYmd.compareTo(solarShunBaiYmd2) >= 0) {
            offset = solar.subtract(solarShunBai2) % 9;
        } else if (solarYmd.compareTo(solarShunBaiYmd) < 0) {
            offset = (8 + solarShunBai.subtract(solar)) % 9;
        }
        return new NineStar(offset);
    }

    /**
     * 获取值时九星
     * 时家紫白星歌诀：三元时白最为佳，冬至阳生顺莫差，孟日七宫仲一白，季日四绿发萌芽，每把时辰起甲子，本时星耀照光华，
     * 时星移入中宫去，顺飞八方逐细查。夏至阴生逆回首，孟归三碧季加六，仲在九宫时起甲，依然掌中逆轮跨。
     *
     * @return 九星
     */
    public NineStar getTimeNineStar() {
        String solarYmd = solar.build(false);
        boolean asc = false;
        if ((solarYmd.compareTo(solarTerm.get("冬至").build(false)) >= 0 && solarYmd.compareTo(solarTerm.get("夏至").build(false)) < 0)) {
            asc = true;
        } else if (solarYmd.compareTo(solarTerm.get("DONG_ZHI").build(false)) >= 0) {
            asc = true;
        }
        int[] offset = asc ? new int[]{0, 3, 6} : new int[]{8, 5, 2};
        int start = offset[getDayZhiIndex() % 3];
        int index = asc ? start + timeZhiIndex : start + 9 - timeZhiIndex;
        return new NineStar(index % 9);
    }

    /**
     * 获取节气表（节气名称:阳历），节气交接时刻精确到秒，以冬至开头，按先后顺序排列
     *
     * @return 节气表
     */
    public Map<String, Solar> getSolarTermTable() {
        return solarTerm;
    }

    /**
     * 获取下一节令（顺推的第一个节令）
     *
     * @return 节气
     */
    public SolarTerm getNextJie() {
        return getNextJie(false);
    }

    /**
     * 获取下一节令（顺推的第一个节令）
     *
     * @param wholeDay 是否按天计
     * @return 节气
     */
    public SolarTerm getNextJie(boolean wholeDay) {
        int l = JIE_QI_IN_USE.length / 2;
        String[] conditions = new String[l];
        for (int i = 0; i < l; i++) {
            conditions[i] = JIE_QI_IN_USE[i * 2];
        }
        return getNearJieQi(true, conditions, wholeDay);
    }

    /**
     * 获取上一节令（逆推的第一个节令）
     *
     * @return 节气
     */
    public SolarTerm getPrevJie() {
        return getPrevJie(false);
    }

    /**
     * 获取上一节令（逆推的第一个节令）
     *
     * @param wholeDay 是否按天计
     * @return 节气
     */
    public SolarTerm getPrevJie(boolean wholeDay) {
        int l = JIE_QI_IN_USE.length / 2;
        String[] conditions = new String[l];
        for (int i = 0; i < l; i++) {
            conditions[i] = JIE_QI_IN_USE[i * 2];
        }
        return getNearJieQi(false, conditions, wholeDay);
    }

    /**
     * 获取下一气令（顺推的第一个气令）
     *
     * @return 节气
     */
    public SolarTerm getNextQi() {
        return getNextQi(false);
    }

    /**
     * 获取下一气令（顺推的第一个气令）
     *
     * @param wholeDay 是否按天计
     * @return 节气
     */
    public SolarTerm getNextQi(boolean wholeDay) {
        int l = JIE_QI_IN_USE.length / 2;
        String[] conditions = new String[l];
        for (int i = 0; i < l; i++) {
            conditions[i] = JIE_QI_IN_USE[i * 2 + 1];
        }
        return getNearJieQi(true, conditions, wholeDay);
    }

    /**
     * 获取上一气令（逆推的第一个气令）
     *
     * @return 节气
     */
    public SolarTerm getPrevQi() {
        return getPrevQi(false);
    }

    /**
     * 获取上一气令（逆推的第一个气令）
     *
     * @param wholeDay 是否按天计
     * @return 节气
     */
    public SolarTerm getPrevQi(boolean wholeDay) {
        int l = JIE_QI_IN_USE.length / 2;
        String[] conditions = new String[l];
        for (int i = 0; i < l; i++) {
            conditions[i] = JIE_QI_IN_USE[i * 2 + 1];
        }
        return getNearJieQi(false, conditions, wholeDay);
    }

    /**
     * 获取下一节气（顺推的第一个节气）
     *
     * @return 节气
     */
    public SolarTerm getNextJieQi() {
        return getNextJieQi(false);
    }

    /**
     * 获取下一节气（顺推的第一个节气）
     *
     * @param wholeDay 是否按天计
     * @return 节气
     */
    public SolarTerm getNextJieQi(boolean wholeDay) {
        return getNearJieQi(true, null, wholeDay);
    }

    /**
     * 获取上一节气（逆推的第一个节气）
     *
     * @return 节气
     */
    public SolarTerm getPrevJieQi() {
        return getPrevJieQi(false);
    }

    /**
     * 获取上一节气（逆推的第一个节气）
     *
     * @param wholeDay 是否按天计
     * @return 节气
     */
    public SolarTerm getPrevJieQi(boolean wholeDay) {
        return getNearJieQi(false, null, wholeDay);
    }

    /**
     * 获取最近的节气，如果未找到匹配的，返回null
     *
     * @param forward    是否顺推，true为顺推，false为逆推
     * @param conditions 过滤条件，如果设置过滤条件，仅返回匹配该名称的
     * @param wholeDay   是否按天计
     * @return 节气
     */
    protected SolarTerm getNearJieQi(boolean forward, String[] conditions, boolean wholeDay) {
        String name = null;
        Solar near = null;
        Set<String> filters = new HashSet<>();
        if (null != conditions) {
            Collections.addAll(filters, conditions);
        }
        boolean filter = !filters.isEmpty();
        String today = wholeDay ? solar.build() : solar.build(true);
        for (Map.Entry<String, Solar> entry : solarTerm.entrySet()) {
            String jq = convertJieQi(entry.getKey());
            if (filter) {
                if (!filters.contains(jq)) {
                    continue;
                }
            }
            Solar solar = entry.getValue();
            String day = wholeDay ? solar.build() : solar.build(true);
            if (forward) {
                if (day.compareTo(today) < 0) {
                    continue;
                }
                if (null == near) {
                    name = jq;
                    near = solar;
                } else {
                    String nearDay = wholeDay ? near.build() : solar.build(true);
                    if (day.compareTo(nearDay) < 0) {
                        name = jq;
                        near = solar;
                    }
                }
            } else {
                if (day.compareTo(today) > 0) {
                    continue;
                }
                if (null == near) {
                    name = jq;
                    near = solar;
                } else {
                    String nearDay = wholeDay ? near.build() : solar.build(true);
                    if (day.compareTo(nearDay) > 0) {
                        name = jq;
                        near = solar;
                    }
                }
            }
        }
        if (null == near) {
            return null;
        }
        return new SolarTerm(name, near);
    }

    /**
     * 获取节气名称，如果无节气，返回空字符串
     *
     * @return 节气名称
     */
    public String getSolarTerm() {
        for (Map.Entry<String, Solar> jq : solarTerm.entrySet()) {
            Solar d = jq.getValue();
            if (d.getYear() == solar.getYear() && d.getMonth() == solar.getMonth() && d.getDay() == solar.getDay()) {
                return convertJieQi(jq.getKey());
            }
        }
        return Normal.EMPTY;
    }

    /**
     * 获取当天节气对象，如果无节气，返回null
     *
     * @return 节气对象
     */
    public SolarTerm getCurrentSolarTerm() {
        for (Map.Entry<String, Solar> jq : solarTerm.entrySet()) {
            Solar d = jq.getValue();
            if (d.getYear() == solar.getYear() && d.getMonth() == solar.getMonth() && d.getDay() == solar.getDay()) {
                return new SolarTerm(convertJieQi(jq.getKey()), d);
            }
        }
        return null;
    }

    /**
     * 获取当天节令对象，如果无节令，返回null
     *
     * @return 节气对象
     */
    public SolarTerm getCurrentJie() {
        for (int i = 0, j = JIE_QI_IN_USE.length; i < j; i += 2) {
            String key = JIE_QI_IN_USE[i];
            Solar d = solarTerm.get(key);
            if (d.getYear() == solar.getYear() && d.getMonth() == solar.getMonth() && d.getDay() == solar.getDay()) {
                return new SolarTerm(convertJieQi(key), d);
            }
        }
        return null;
    }

    /**
     * 获取当天气令对象，如果无气令，返回null
     *
     * @return 节气对象
     */
    public SolarTerm getCurrentQi() {
        for (int i = 1, j = JIE_QI_IN_USE.length; i < j; i += 2) {
            String key = JIE_QI_IN_USE[i];
            Solar d = solarTerm.get(key);
            if (d.getYear() == solar.getYear() && d.getMonth() == solar.getMonth() && d.getDay() == solar.getDay()) {
                return new SolarTerm(convertJieQi(key), d);
            }
        }
        return null;
    }

    /**
     * 获取年份
     *
     * @return 如2015
     */
    public int getYear() {
        return year;
    }

    /**
     * 获取月份
     *
     * @return 1到12，负数为闰月
     */
    public int getMonth() {
        return month;
    }

    /**
     * 获取日期
     *
     * @return 日期
     */
    public int getDay() {
        return day;
    }

    /**
     * 获取小时
     *
     * @return 0到23之间的数字
     */
    public int getHour() {
        return hour;
    }

    /**
     * 获取分钟
     *
     * @return 0到59之间的数字
     */
    public int getMinute() {
        return minute;
    }

    /**
     * 获取秒钟
     *
     * @return 0到59之间的数字
     */
    public int getSecond() {
        return second;
    }

    public int getTimeGanIndex() {
        return timeGanIndex;
    }

    public int getTimeZhiIndex() {
        return timeZhiIndex;
    }

    public int getDayGanIndex() {
        return dayGanIndex;
    }

    public int getDayZhiIndex() {
        return dayZhiIndex;
    }

    public int getMonthGanIndex() {
        return monthGanIndex;
    }

    public int getMonthZhiIndex() {
        return monthZhiIndex;
    }

    public int getYearGanIndex() {
        return yearGanIndex;
    }

    public int getYearZhiIndex() {
        return yearZhiIndex;
    }

    public int getYearGanIndexByLiChun() {
        return yearGanIndexByLiChun;
    }

    public int getYearZhiIndexByLiChun() {
        return yearZhiIndexByLiChun;
    }

    public int getDayGanIndexExact() {
        return dayGanIndexExact;
    }

    public int getDayGanIndexExact2() {
        return dayGanIndexExact2;
    }

    public int getDayZhiIndexExact() {
        return dayZhiIndexExact;
    }

    public int getDayZhiIndexExact2() {
        return dayZhiIndexExact2;
    }

    public int getMonthGanIndexExact() {
        return monthGanIndexExact;
    }

    public int getMonthZhiIndexExact() {
        return monthZhiIndexExact;
    }

    public int getYearGanIndexExact() {
        return yearGanIndexExact;
    }

    public int getYearZhiIndexExact() {
        return yearZhiIndexExact;
    }

    public Solar getSolar() {
        return solar;
    }

    public EightChar getEightChar() {
        if (null == eightChar) {
            eightChar = new EightChar(this);
        }
        return eightChar;
    }

    /**
     * 获取往后推几天的农历日期，如果要往前推，则天数用负数
     *
     * @param days 天数
     * @return 农历日期
     */
    public Lunar next(int days) {
        return this.solar.next(days).getLunar();
    }

    /**
     * 获取年所在旬（以正月初一作为新年的开始）
     *
     * @return 旬
     */
    public String getYearXun() {
        return getXun(getYearInGanZhi());
    }

    /**
     * 获取年所在旬（以立春当天作为新年的开始）
     *
     * @return 旬
     */
    public String getYearXunByLiChun() {
        return getXun(getYearInGanZhiByLiChun());
    }

    /**
     * 获取年所在旬（以立春交接时刻作为新年的开始）
     *
     * @return 旬
     */
    public String getYearXunExact() {
        return getXun(getYearInGanZhiExact());
    }

    /**
     * 获取值年空亡（以正月初一作为新年的开始）
     *
     * @return 空亡(旬空)
     */
    public String getYearXunKong() {
        return getXunKong(getYearInGanZhi());
    }

    /**
     * 获取值年空亡（以立春当天作为新年的开始）
     *
     * @return 空亡(旬空)
     */
    public String getYearXunKongByLiChun() {
        return getXunKong(getYearInGanZhiByLiChun());
    }

    /**
     * 获取值年空亡（以立春交接时刻作为新年的开始）
     *
     * @return 空亡(旬空)
     */
    public String getYearXunKongExact() {
        return getXunKong(getYearInGanZhiExact());
    }

    /**
     * 获取月所在旬（以节交接当天起算）
     *
     * @return 旬
     */
    public String getMonthXun() {
        return getXun(getMonthInGanZhi());
    }

    /**
     * 获取月所在旬（以节交接时刻起算）
     *
     * @return 旬
     */
    public String getMonthXunExact() {
        return getXun(getMonthInGanZhiExact());
    }

    /**
     * 获取值月空亡（以节交接当天起算）
     *
     * @return 空亡(旬空)
     */
    public String getMonthXunKong() {
        return getXunKong(getMonthInGanZhi());
    }

    /**
     * 获取值月空亡（以节交接时刻起算）
     *
     * @return 空亡(旬空)
     */
    public String getMonthXunKongExact() {
        return getXunKong(getMonthInGanZhiExact());
    }

    /**
     * 获取日所在旬（以节交接当天起算）
     *
     * @return 旬
     */
    public String getDayXun() {
        return getXun(getDayInGanZhi());
    }

    /**
     * 获取日所在旬（晚子时日柱算明天）
     *
     * @return 旬
     */
    public String getDayXunExact() {
        return getXun(getDayInGanZhiExact());
    }

    /**
     * 获取日所在旬（晚子时日柱算当天）
     *
     * @return 旬
     */
    public String getDayXunExact2() {
        return getXun(getDayInGanZhiExact2());
    }

    /**
     * 获取值日空亡
     *
     * @return 空亡(旬空)
     */
    public String getDayXunKong() {
        return getXunKong(getDayInGanZhi());
    }

    /**
     * 获取值日空亡（晚子时日柱算明天）
     *
     * @return 空亡(旬空)
     */
    public String getDayXunKongExact() {
        return getXunKong(getDayInGanZhiExact());
    }

    /**
     * 获取值日空亡（晚子时日柱算当天）
     *
     * @return 空亡(旬空)
     */
    public String getDayXunKongExact2() {
        return getXunKong(getDayInGanZhiExact2());
    }

    /**
     * 获取时辰所在旬
     *
     * @return 旬
     */
    public String getTimeXun() {
        return getXun(getTimeInGanZhi());
    }

    /**
     * 获取值时空亡
     *
     * @return 空亡(旬空)
     */
    public String getTimeXunKong() {
        return getXunKong(getTimeInGanZhi());
    }

    /**
     * 获取数九
     *
     * @return 数九，如果不是数九天，返回null
     */
    public NinePeriod getShuJiu() {
        Solar current = Solar.from(solar.getYear(), solar.getMonth(), solar.getDay());
        Solar start = solarTerm.get("DONG_ZHI");
        start = Solar.from(start.getYear(), start.getMonth(), start.getDay());

        if (current.isBefore(start)) {
            start = solarTerm.get("冬至");
            start = Solar.from(start.getYear(), start.getMonth(), start.getDay());
        }

        Solar end = Solar.from(start.getYear(), start.getMonth(), start.getDay()).next(81);

        if (current.isBefore(start) || !current.isBefore(end)) {
            return null;
        }

        int days = current.subtract(start);
        return new NinePeriod(Fields.CN_NUMBER[days / 9 + 1] + "九", days % 9 + 1);
    }

    /**
     * 获取三伏
     *
     * @return 三伏，如果不是伏天，返回null
     */
    public Dogdays getDogdays() {
        Calendar current = Kalendar.calendar(solar.getYear(), solar.getMonth(), solar.getDay());
        Solar xiaZhi = this.solarTerm.get("夏至");
        Solar liQiu = this.solarTerm.get("立秋");
        Calendar start = Kalendar.calendar(xiaZhi.getYear(), xiaZhi.getMonth(), xiaZhi.getDay());

        // 第1个庚日
        int add = 6 - xiaZhi.getLunar().getDayGanIndex();
        if (add < 0) {
            add += 10;
        }
        // 第3个庚日，即初伏第1天
        add += 20;
        start.add(Calendar.DATE, add);

        // 初伏以前
        if (current.compareTo(start) < 0) {
            return null;
        }

        int days = Solar.getDays(start, current);
        if (days < 10) {
            return new Dogdays("初伏", days + 1);
        }

        // 第4个庚日，中伏第1天
        start.add(Calendar.DATE, 10);

        days = Solar.getDays(start, current);
        if (days < 10) {
            return new Dogdays("中伏", days + 1);
        }

        // 第5个庚日，中伏第11天或末伏第1天
        start.add(Calendar.DATE, 10);
        Calendar liQiuCalendar = Kalendar.calendar(liQiu.getYear(), liQiu.getMonth(), liQiu.getDay());

        days = Solar.getDays(start, current);
        // 末伏
        if (liQiuCalendar.compareTo(start) <= 0) {
            if (days < 10) {
                return new Dogdays("末伏", days + 1);
            }
        } else {
            // 中伏
            if (days < 10) {
                return new Dogdays("中伏", days + 11);
            }
            // 末伏第1天
            start.add(Calendar.DATE, 10);
            days = Solar.getDays(start, current);
            if (days < 10) {
                return new Dogdays("末伏", days + 1);
            }
        }
        return null;
    }

    /**
     * 获取六曜
     *
     * @return 六曜
     */
    public String getLiuYao() {
        return LIU_YAO[(Math.abs(month) - 1 + day - 1) % 6];
    }

    /**
     * 获取物候
     *
     * @return 物候
     */
    public String getWuHou() {
        SolarTerm jieQi = getPrevJieQi(true);
        int offset = 0;
        for (int i = 0, j = Fields.CN_SOLARTERM.length; i < j; i++) {
            if (jieQi.getName().equals(Fields.CN_SOLARTERM[i])) {
                offset = i;
                break;
            }
        }
        int index = solar.subtract(jieQi.getSolar()) / 5;
        if (index > 2) {
            index = 2;
        }
        return WU_HOU[(offset * 3 + index) % WU_HOU.length];
    }

    /**
     * 获取候
     *
     * @return 候
     */
    public String getHou() {
        SolarTerm jieQi = getPrevJieQi(true);
        int max = Lunar.HOU.length - 1;
        int offset = solar.subtract(jieQi.getSolar()) / 5;
        if (offset > max) {
            offset = max;
        }
        return String.format("%s %s", jieQi.getName(), Lunar.HOU[offset]);
    }

    /**
     * 获取日禄
     *
     * @return 日禄
     */
    public String getDayLu() {
        String gan = LU.get(getDayGan());
        String zhi = LU.get(getDayZhi());
        String lu = gan + "命互禄";
        if (null != zhi) {
            lu += " " + zhi + "命进禄";
        }
        return lu;
    }

    /**
     * 获取时辰
     *
     * @return 时辰
     */
    public Time getTime() {
        return new Time(year, month, day, hour, minute, second);
    }

    /**
     * 获取当天的时辰列表
     *
     * @return 时辰列表
     */
    public List<Time> getTimes() {
        List<Time> l = new ArrayList<>();
        l.add(new Time(year, month, day, 0, 0, 0));
        for (int i = 0; i < 12; i++) {
            l.add(new Time(year, month, day, (i + 1) * 2 - 1, 0, 0));
        }
        return l;
    }

    /**
     * 获取佛历
     *
     * @return 佛历
     */
    public Buddhist getFoto() {
        return Buddhist.from(this);
    }

    /**
     * 获取道历
     *
     * @return 佛历
     */
    public Taoist getTao() {
        return Taoist.from(this);
    }

    /**
     * 构建字符串内容
     *
     * @param args 可选参数-简化输出
     * @return 字符串内容
     */
    public String build(boolean... args) {
        String strYmd = getYearInChinese() + "年" + getMonthInChinese() + "月" + getDayInChinese();
        if (ObjectKit.isNotEmpty(args) && BooleanKit.and(args)) {
            StringBuilder s = new StringBuilder();
            s.append(strYmd);
            s.append(Symbol.SPACE);
            s.append(getYearInGanZhi());
            s.append(Symbol.C_PARENTHESE_LEFT);
            s.append(getYearAnimal());
            s.append(")年 ");
            s.append(getMonthInGanZhi());
            s.append(Symbol.C_PARENTHESE_LEFT);
            s.append(getMonthAnimal());
            s.append(")月 ");
            s.append(getDayInGanZhi());
            s.append(Symbol.C_PARENTHESE_LEFT);
            s.append(getDayAnimal());
            s.append(")日 ");
            s.append(getTimeZhi());
            s.append(Symbol.C_PARENTHESE_LEFT);
            s.append(getTimeAnimal());
            s.append(")时 纳音[");
            s.append(getYearNaYin());
            s.append(Symbol.SPACE);
            s.append(getMonthNaYin());
            s.append(Symbol.SPACE);
            s.append(getDayNaYin());
            s.append(Symbol.SPACE);
            s.append(getTimeNaYin());
            s.append("]" + Symbol.SPACE);
            s.append(getWeekInChinese());
            for (String f : getFestivals()) {
                s.append(" (");
                s.append(f);
                s.append(Symbol.PARENTHESE_RIGHT);
            }
            for (String f : getOtherFestivals()) {
                s.append(" (");
                s.append(f);
                s.append(Symbol.PARENTHESE_RIGHT);
            }
            String jq = getSolarTerm();
            if (jq.length() > 0) {
                s.append(" [");
                s.append(jq);
                s.append("]");
            }
            s.append(Symbol.SPACE);
            s.append(getGong());
            s.append("方");
            s.append(getShou());
            s.append(" 星宿[");
            s.append(getXiu());
            s.append(getZheng());
            s.append(getAnimal());
            s.append("](");
            s.append(getXiuLuck());
            s.append(") 彭祖百忌[");
            s.append(getPengZuGan());
            s.append(Symbol.SPACE);
            s.append(getPengZuZhi());
            s.append("] 喜神方位[");
            s.append(getDayPositionXi());
            s.append("](");
            s.append(getDayPositionXiDesc());
            s.append(") 阳贵神方位[");
            s.append(getDayPositionYangGui());
            s.append("](");
            s.append(getDayPositionYangGuiDesc());
            s.append(") 阴贵神方位[");
            s.append(getDayPositionYinGui());
            s.append("](");
            s.append(getDayPositionYinGuiDesc());
            s.append(") 福神方位[");
            s.append(getDayPositionFu());
            s.append("](");
            s.append(getDayPositionFuDesc());
            s.append(") 财神方位[");
            s.append(getDayPositionCai());
            s.append("](");
            s.append(getDayPositionCaiDesc());
            s.append(") 冲[");
            s.append(getDayChongDesc());
            s.append("] 煞[");
            s.append(getDaySha());
            s.append("]");
            return s.toString();
        }
        return strYmd;
    }

    /**
     * 初始化信息
     */
    private void initialize(Year year) {
        this.initTerm(year);
        this.initYear();
        this.initMonth();
        this.initDay();
        this.initTime();
        this.initWeek();
    }

    /**
     * 计算节气表
     */
    private void initTerm(Year year) {
        List<Double> julianDays = year.getJieQiJulianDays();
        for (int i = 0, j = JIE_QI_IN_USE.length; i < j; i++) {
            this.solarTerm.put(JIE_QI_IN_USE[i], Solar.from(julianDays.get(i)));
        }
    }

    /**
     * 计算干支纪年
     */
    private void initYear() {
        // 以正月初一开始
        int offset = year - 4;
        yearGanIndex = offset % 10;
        yearZhiIndex = offset % 12;

        if (yearGanIndex < 0) {
            yearGanIndex += 10;
        }

        if (yearZhiIndex < 0) {
            yearZhiIndex += 12;
        }

        // 以立春作为新一年的开始的干支纪年
        int g = yearGanIndex;
        int z = yearZhiIndex;

        // 精确的干支纪年，以立春交接时刻为准
        int gExact = yearGanIndex;
        int zExact = yearZhiIndex;

        int solarYear = solar.getYear();
        String solarYmd = solar.build(false);
        String solarYmdHms = solar.build();

        //获取立春的阳历时刻
        Solar liChun = solarTerm.get("立春");
        if (liChun.getYear() != solarYear) {
            liChun = solarTerm.get("LI_CHUN");
        }
        String liChunYmd = liChun.build(false);
        String liChunYmdHms = liChun.build();

        //阳历和阴历年份相同代表正月初一及以后
        if (year == solarYear) {
            //立春日期判断
            if (solarYmd.compareTo(liChunYmd) < 0) {
                g--;
                z--;
            }
            //立春交接时刻判断
            if (solarYmdHms.compareTo(liChunYmdHms) < 0) {
                gExact--;
                zExact--;
            }
        } else if (year < solarYear) {
            if (solarYmd.compareTo(liChunYmd) >= 0) {
                g++;
                z++;
            }
            if (solarYmdHms.compareTo(liChunYmdHms) >= 0) {
                gExact++;
                zExact++;
            }
        }

        yearGanIndexByLiChun = (g < 0 ? g + 10 : g) % 10;
        yearZhiIndexByLiChun = (z < 0 ? z + 12 : z) % 12;

        yearGanIndexExact = (gExact < 0 ? gExact + 10 : gExact) % 10;
        yearZhiIndexExact = (zExact < 0 ? zExact + 12 : zExact) % 12;
    }

    /**
     * 计算干支纪月
     */
    private void initMonth() {
        Solar start = null;
        Solar end;
        String ymd = solar.build(false);
        String time = solar.build();
        int size = JIE_QI_IN_USE.length;

        // 序号：大雪以前-3，大雪到小寒之间-2，小寒到立春之间-1，立春之后0
        int index = -3;
        for (int i = 0; i < size; i += 2) {
            end = solarTerm.get(JIE_QI_IN_USE[i]);
            String symd = null == start ? ymd : start.build(false);
            if (ymd.compareTo(symd) >= 0 && ymd.compareTo(end.build(false)) < 0) {
                break;
            }
            start = end;
            index++;
        }

        // 干偏移值（以立春当天起算）
        int offset = (((yearGanIndexByLiChun + (index < 0 ? 1 : 0)) % 5 + 1) * 2) % 10;
        monthGanIndex = ((index < 0 ? index + 10 : index) + offset) % 10;
        monthZhiIndex = ((index < 0 ? index + 12 : index) + BASE_MONTH_ZHI_INDEX) % 12;

        start = null;
        index = -3;
        for (int i = 0; i < size; i += 2) {
            end = solarTerm.get(JIE_QI_IN_USE[i]);
            String stime = null == start ? time : start.build();
            if (time.compareTo(stime) >= 0 && time.compareTo(end.build()) < 0) {
                break;
            }
            start = end;
            index++;
        }

        // 干偏移值（以立春交接时刻起算）
        offset = (((yearGanIndexExact + (index < 0 ? 1 : 0)) % 5 + 1) * 2) % 10;
        monthGanIndexExact = ((index < 0 ? index + 10 : index) + offset) % 10;
        monthZhiIndexExact = ((index < 0 ? index + 12 : index) + BASE_MONTH_ZHI_INDEX) % 12;
    }

    /**
     * 计算干支纪日
     */
    private void initDay() {
        Solar noon = Solar.from(solar.getYear(), solar.getMonth(), solar.getDay(), 12, 0, 0);
        int offset = (int) noon.getJulianDay() - 11;
        dayGanIndex = offset % 10;
        dayZhiIndex = offset % 12;

        int dayGanExact = dayGanIndex;
        int dayZhiExact = dayZhiIndex;

        // 八字流派2，晚子时（夜子/子夜）日柱算当天
        dayGanIndexExact2 = dayGanExact;
        dayZhiIndexExact2 = dayZhiExact;

        // 八字流派1，晚子时（夜子/子夜）日柱算明天
        String hm = (hour < 10 ? "0" : Normal.EMPTY) + hour + Symbol.COLON + (minute < 10 ? "0" : Normal.EMPTY) + minute;
        if (hm.compareTo("23:00") >= 0 && hm.compareTo("23:59") <= 0) {
            dayGanExact++;
            if (dayGanExact >= 10) {
                dayGanExact -= 10;
            }
            dayZhiExact++;
            if (dayZhiExact >= 12) {
                dayZhiExact -= 12;
            }
        }

        dayGanIndexExact = dayGanExact;
        dayZhiIndexExact = dayZhiExact;
    }

    /**
     * 计算干支纪时
     */
    private void initTime() {
        String hm = (hour < 10 ? "0" : Normal.EMPTY) + hour + Symbol.C_COLON + (minute < 10 ? "0" : Normal.EMPTY) + minute;
        timeZhiIndex = getTimeZhiIndex(hm);
        timeGanIndex = (dayGanIndexExact % 5 * 2 + timeZhiIndex) % 10;
    }

    /**
     * 计算星期
     */
    private void initWeek() {
        this.weekIndex = solar.getWeek();
    }

    /**
     * 节气
     */
    public static class SolarTerm {

        /**
         * 名称
         */
        private String name;

        /**
         * 阳历日期
         */
        private Solar solar;

        /**
         * 是否节令
         */
        private boolean jie;

        /**
         * 是否气令
         */
        private boolean qi;

        public SolarTerm() {

        }

        /**
         * 初始化
         *
         * @param name  名称
         * @param solar 阳历日期
         */
        public SolarTerm(String name, Solar solar) {
            setName(name);
            this.solar = solar;
        }

        /**
         * 获取名称
         *
         * @return 名称
         */
        public String getName() {
            return this.name;
        }

        /**
         * 设置名称
         *
         * @param name 名称
         */
        public void setName(String name) {
            this.name = name;
            for (int i = 0, j = Fields.CN_SOLARTERM.length; i < j; i++) {
                if (name.equals(Fields.CN_SOLARTERM[i])) {
                    if (i % 2 == 0) {
                        this.qi = true;
                    } else {
                        this.jie = true;
                    }
                    return;
                }
            }
        }

        /**
         * 获取阳历日期
         *
         * @return 阳历日期
         */
        public Solar getSolar() {
            return this.solar;
        }

        /**
         * 设置阳历日期
         *
         * @param solar 阳历日期
         */
        public void setSolar(Solar solar) {
            this.solar = solar;
        }

        /**
         * 是否节令
         *
         * @return true/false
         */
        public boolean isJie() {
            return this.jie;
        }

        /**
         * 是否气令
         *
         * @return true/false
         */
        public boolean isQi() {
            return this.qi;
        }

        /**
         * 构建字符串内容
         *
         * @param args 可选参数-简化输出
         * @return 字符串内容
         */
        public String build(boolean... args) {
            return this.name;
        }

    }

    /**
     * 三伏
     * 从夏至后第3个庚日算起，
     * 初伏为10天，中伏为10天或20天，末伏为10天。
     * 当夏至与立秋之间出现4个庚日时中伏为10天，出现5个庚日则为20天
     */
    public static class Dogdays {

        /**
         * 名称：初伏、中伏、末伏
         */
        private String name;

        /**
         * 当前入伏第几天，1-20
         */
        private int index;


        public Dogdays() {
        }

        public Dogdays(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return name + " 第" + index + "天";
        }

    }

    /**
     * 数九
     */
    public static class NinePeriod {

        /**
         * 名称，如一九、二九
         */
        private String name;

        /**
         * 当前数九第几天，1-9
         */
        private int index;

        public NinePeriod() {

        }

        public NinePeriod(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return name + " 第" + index + "天";
        }

    }

    /**
     * 农历年
     */
    public static class Year {

        private static final Map<Integer, Integer> LEAP = new HashMap<>();

        private static final Map<Integer, Year> CACHE = new HashMap<>();

        static {
            for (int y : LEAP_11) {
                LEAP.put(y, 13);
            }
            for (int y : LEAP_12) {
                LEAP.put(y, 14);
            }
        }

        /**
         * 农历年
         */
        private int year;

        /**
         * 天干下标
         */
        private int ganIndex;

        /**
         * 地支下标
         */
        private int zhiIndex;

        /**
         * 农历月们
         */
        private List<Month> months = new ArrayList<>();

        /**
         * 节气儒略日们
         */
        private List<Double> jieQiJulianDays = new ArrayList<>();

        /**
         * 初始化
         *
         * @param lunarYear 农历年
         */
        public Year(int lunarYear) {
            this.year = lunarYear;
            int offset = lunarYear - 4;
            int yearGanIndex = offset % 10;
            int yearZhiIndex = offset % 12;
            if (yearGanIndex < 0) {
                yearGanIndex += 10;
            }
            if (yearZhiIndex < 0) {
                yearZhiIndex += 12;
            }
            this.ganIndex = yearGanIndex;
            this.zhiIndex = yearZhiIndex;
            compute();
        }

        /**
         * 通过农历年初始化
         *
         * @param lunarYear 农历年
         * @return 农历年
         */
        public static Year from(int lunarYear) {
            Year year = CACHE.get(lunarYear);
            if (null == year) {
                year = new Year(lunarYear);
                CACHE.put(lunarYear, year);
            }
            return year;
        }

        private void compute() {
            // 节气(中午12点)
            double[] jq = new double[27];
            // 合朔，即每月初一(中午12点)
            double[] hs = new double[16];
            // 每月天数
            int[] dayCounts = new int[hs.length - 1];

            int currentYear = this.year;

            int year = currentYear - 2000;
            // 从上年的大雪到下年的立春
            for (int i = 0, j = JIE_QI_IN_USE.length; i < j; i++) {
                // 精确的节气
                double t = 36525 * Galaxy.Astronomy.s_aLon_t((year + (17 + i) * 15d / 360) * 2 * Math.PI);
                t += 1d / 3 - Galaxy.Astronomy.dt_t(t);
                jieQiJulianDays.add(t + Solar.J2000);
                // 按中午12点算的节气
                if (i > 0 && i < 28) {
                    jq[i - 1] = Math.round(t);
                }
            }

            // 冬至前的初一
            double w = Galaxy.QiShuo.calc(jq[0]);
            if (w > jq[0]) {
                if (currentYear != 41 && currentYear != 193 && currentYear != 288 && currentYear != 345 && currentYear != 918 && currentYear != 1013) {
                    w -= 29.5306;
                }
            }
            // 递推每月初一
            for (int i = 0, j = hs.length; i < j; i++) {
                hs[i] = Galaxy.QiShuo.calc(w + 29.5306 * i);
            }
            // 每月天数
            for (int i = 0, j = dayCounts.length; i < j; i++) {
                dayCounts[i] = (int) (hs[i + 1] - hs[i]);
            }

            int prevYear = currentYear - 1;

            int leapYear = -1;
            int leapIndex = -1;

            Integer leap = LEAP.get(currentYear);
            if (null == leap) {
                leap = LEAP.get(prevYear);
                if (null == leap) {
                    if (hs[13] <= jq[24]) {
                        int i = 1;
                        while (hs[i + 1] > jq[2 * i] && i < 13) {
                            i++;
                        }
                        leapYear = currentYear;
                        leapIndex = i;
                    }
                } else {
                    leapYear = prevYear;
                    leapIndex = leap - 12;
                }
            } else {
                leapYear = currentYear;
                leapIndex = leap;
            }

            int y = prevYear;
            int m = 11;
            int index = m;
            for (int i = 0, j = dayCounts.length; i < j; i++) {
                int cm = m;
                if (y == leapYear && i == leapIndex) {
                    cm = -cm;
                }
                this.months.add(new Month(y, cm, dayCounts[i], hs[i] + Solar.J2000, index));
                if (y != leapYear || i + 1 != leapIndex) {
                    m++;
                }
                index++;
                if (m == 13) {
                    m = 1;
                    index = 1;
                    y++;
                }
            }
        }

        /**
         * 获取总天数
         *
         * @return 天数
         */
        public int getDayCount() {
            int n = 0;
            for (Month m : months) {
                if (m.getYear() == year) {
                    n += m.getDayCount();
                }
            }
            return n;
        }

        /**
         * 获取当年的农历月们
         *
         * @return 农历月们
         */
        public List<Month> getMonthsInYear() {
            List<Month> l = new ArrayList<>();
            for (Month m : months) {
                if (m.getYear() == year) {
                    l.add(m);
                }
            }
            return l;
        }

        /**
         * 获取农历年
         *
         * @return 农历年
         */
        public int getYear() {
            return year;
        }

        /**
         * 获取农历月们
         *
         * @return 农历月们
         */
        public List<Month> getMonths() {
            return months;
        }

        /**
         * 获取节气儒略日们
         *
         * @return 节气儒略日们
         */
        public List<Double> getJieQiJulianDays() {
            return jieQiJulianDays;
        }

        /**
         * 获取天干序号，从0开始
         *
         * @return 序号
         */
        public int getGanIndex() {
            return ganIndex;
        }

        /**
         * 获取地支序号，从0开始
         *
         * @return 序号
         */
        public int getZhiIndex() {
            return zhiIndex;
        }

        /**
         * 获取天干
         *
         * @return 天干
         */
        public String getGan() {
            return Fields.CN_GAN[ganIndex + 1];
        }

        /**
         * 获取地支
         *
         * @return 地支
         */
        public String getZhi() {
            return Fields.CN_ZHI[zhiIndex + 1];
        }

        /**
         * 获取干支
         *
         * @return 干支
         */
        public String getGanZhi() {
            return getGan() + getZhi();
        }

        /**
         * 获取农历月
         *
         * @param lunarMonth 月，1-12，闰月为负数，如闰2月为-2
         * @return 农历月
         */
        public Month getMonth(int lunarMonth) {
            for (Month m : months) {
                if (m.getYear() == year && m.getMonth() == lunarMonth) {
                    return m;
                }
            }
            return null;
        }

        /**
         * 获取闰月
         *
         * @return 闰月数字，1代表闰1月，0代表无闰月
         */
        public int getLeapMonth() {
            for (Month m : months) {
                if (m.getYear() == year && m.isLeap()) {
                    return Math.abs(m.getMonth());
                }
            }
            return 0;
        }

        protected String getZaoByGan(int index, String name) {
            int offset = index - Solar.from(getMonth(1).getFirstJulianDay()).getLunar().getDayGanIndex();
            if (offset < 0) {
                offset += 10;
            }
            return name.replaceFirst("几", Fields.CN_NUMBER[offset + 1]);
        }

        protected String getZaoByZhi(int index, String name) {
            int offset = index - Solar.from(getMonth(1).getFirstJulianDay()).getLunar().getDayZhiIndex();
            if (offset < 0) {
                offset += 12;
            }
            return name.replaceFirst("几", Fields.CN_NUMBER[offset + 1]);
        }

        /**
         * 获取几鼠偷粮
         *
         * @return 几鼠偷粮
         */
        public String getTouLiang() {
            return getZaoByZhi(0, "几鼠偷粮");
        }

        /**
         * 获取草子几分
         *
         * @return 草子几分
         */
        public String getCaoZi() {
            return getZaoByZhi(0, "草子几分");
        }

        /**
         * 获取耕田（正月第一个丑日是初几，就是几牛耕田）
         *
         * @return 耕田，如：六牛耕田
         */
        public String getGengTian() {
            return getZaoByZhi(1, "几牛耕田");
        }

        /**
         * 获取花收几分
         *
         * @return 花收几分
         */
        public String getHuaShou() {
            return getZaoByZhi(3, "花收几分");
        }

        /**
         * 获取治水（正月第一个辰日是初几，就是几龙治水）
         *
         * @return 治水，如：二龙治水
         */
        public String getZhiShui() {
            return getZaoByZhi(4, "几龙治水");
        }

        /**
         * 获取几马驮谷
         *
         * @return 几马驮谷
         */
        public String getTuoGu() {
            return getZaoByZhi(6, "几马驮谷");
        }

        /**
         * 获取几鸡抢米
         *
         * @return 几鸡抢米
         */
        public String getQiangMi() {
            return getZaoByZhi(9, "几鸡抢米");
        }

        /**
         * 获取几姑看蚕
         *
         * @return 几姑看蚕
         */
        public String getKanCan() {
            return getZaoByZhi(9, "几姑看蚕");
        }

        /**
         * 获取几屠共猪
         *
         * @return 几屠共猪
         */
        public String getGongZhu() {
            return getZaoByZhi(11, "几屠共猪");
        }

        /**
         * 获取甲田几分
         *
         * @return 甲田几分
         */
        public String getJiaTian() {
            return getZaoByGan(0, "甲田几分");
        }

        /**
         * 获取分饼（正月第一个丙日是初几，就是几人分饼）
         *
         * @return 分饼，如：六人分饼
         */
        public String getFenBing() {
            return getZaoByGan(2, "几人分饼");
        }

        /**
         * 获取得金（正月第一个辛日是初几，就是几日得金）
         *
         * @return 得金，如：一日得金
         */
        public String getDeJin() {
            return getZaoByGan(7, "几日得金");
        }

        /**
         * 获取几人几丙
         *
         * @return 几人几丙
         */
        public String getRenBing() {
            return getZaoByGan(2, getZaoByZhi(2, "几人几丙"));
        }

        /**
         * 获取几人几锄
         *
         * @return 几人几锄
         */
        public String getRenChu() {
            return getZaoByGan(3, getZaoByZhi(2, "几人几锄"));
        }

        /**
         * 获取三元
         *
         * @return 元
         */
        public String getYuan() {
            return YUAN[((year + 2696) / 60) % 3] + "元";
        }

        /**
         * 获取九运
         *
         * @return 运
         */
        public String getYun() {
            return YUN[((year + 2696) / 20) % 9] + "运";
        }

        /**
         * 获取九星
         *
         * @return 九星
         */
        public NineStar getNineStar() {
            int index = getJiaZiIndex(getGanZhi()) + 1;
            int yuan = ((this.year + 2696) / 60) % 3;
            int offset = (62 + yuan * 3 - index) % 9;
            if (0 == offset) {
                offset = 9;
            }
            return new NineStar(offset - 1);
        }

        /**
         * 获取喜神方位
         *
         * @return 方位，如艮
         */
        public String getPositionXi() {
            return POSITION_XI[ganIndex + 1];
        }

        /**
         * 获取喜神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionXiDesc() {
            return POSITION_DESC.get(getPositionXi());
        }

        /**
         * 获取阳贵神方位
         *
         * @return 方位，如艮
         */
        public String getPositionYangGui() {
            return POSITION_YANG_GUI[ganIndex + 1];
        }

        /**
         * 获取阳贵神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionYangGuiDesc() {
            return POSITION_DESC.get(getPositionYangGui());
        }

        /**
         * 获取阴贵神方位
         *
         * @return 方位，如艮
         */
        public String getPositionYinGui() {
            return POSITION_YIN_GUI[ganIndex + 1];
        }

        /**
         * 获取阴贵神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionYinGuiDesc() {
            return POSITION_DESC.get(getPositionYinGui());
        }

        /**
         * 获取福神方位（默认流派：2）
         *
         * @return 方位，如艮
         */
        public String getPositionFu() {
            return getPositionFu(2);
        }

        /**
         * 获取福神方位
         *
         * @param sect 流派，1或2
         * @return 方位，如艮
         */
        public String getPositionFu(int sect) {
            return (1 == sect ? POSITION_FU : POSITION_FU_2)[ganIndex + 1];
        }

        /**
         * 获取福神方位描述（默认流派：2）
         *
         * @return 方位描述，如东北
         */
        public String getPositionFuDesc() {
            return getPositionFuDesc(2);
        }

        /**
         * 获取福神方位描述
         *
         * @param sect 流派，1或2
         * @return 方位描述，如东北
         */
        public String getPositionFuDesc(int sect) {
            return POSITION_DESC.get(getPositionFu(sect));
        }

        /**
         * 获取财神方位
         *
         * @return 方位，如艮
         */
        public String getPositionCai() {
            return POSITION_CAI[ganIndex + 1];
        }

        /**
         * 获取财神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionCaiDesc() {
            return POSITION_DESC.get(getPositionCai());
        }

        /**
         * 获取太岁方位
         *
         * @return 方位，如艮
         */
        public String getPositionTaiSui() {
            return POSITION_TAI_SUI_YEAR[zhiIndex];
        }

        /**
         * 获取太岁方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionTaiSuiDesc() {
            return POSITION_DESC.get(getPositionTaiSui());
        }

        /**
         * 获取往后推几年的阴历年，如果要往前推，则年数用负数
         *
         * @param n 年数
         * @return 阴历年
         */
        public Year next(int n) {
            return Year.from(year + n);
        }

        /**
         * 构建字符串内容
         *
         * @param args 可选参数-简化输出
         * @return 字符串内容
         */
        public String build(boolean... args) {
            if (ObjectKit.isNotEmpty(args) && BooleanKit.and(args)) {
                return this.year + "年";
            }
            return StringKit.toString(this.year);
        }

    }


    /**
     * 农历月
     */
    public static class Month {

        private final int index;
        private final int zhiIndex;
        /**
         * 农历年
         */
        private int year;
        /**
         * 农历月：1-12，闰月为负数，如闰2月为-2
         */
        private int month;
        /**
         * 天数，大月30天，小月29天
         */
        private int dayCount;
        /**
         * 初一的儒略日
         */
        private double firstJulianDay;

        /**
         * 初始化
         *
         * @param lunarYear      农历年
         * @param lunarMonth     农历月：1-12，闰月为负数，如闰2月为-2
         * @param dayCount       天数
         * @param firstJulianDay 初一的儒略日
         */
        public Month(int lunarYear, int lunarMonth, int dayCount, double firstJulianDay, int index) {
            this.year = lunarYear;
            this.month = lunarMonth;
            this.dayCount = dayCount;
            this.firstJulianDay = firstJulianDay;
            this.index = index;
            this.zhiIndex = (index - 1 + BASE_MONTH_ZHI_INDEX) % 12;
        }

        /**
         * 通过农历年月初始化
         *
         * @param lunarYear  农历年
         * @param lunarMonth 农历月：1-12，闰月为负数，如闰2月为-2
         * @return 农历月
         */
        public static Month from(int lunarYear, int lunarMonth) {
            return Year.from(lunarYear).getMonth(lunarMonth);
        }

        /**
         * 获取农历年
         *
         * @return 农历年
         */
        public int getYear() {
            return year;
        }

        /**
         * 获取农历月
         *
         * @return 农历月：1-12，闰月为负数，如闰2月为-2
         */
        public int getMonth() {
            return month;
        }

        /**
         * 是否闰月
         *
         * @return true/false
         */
        public boolean isLeap() {
            return month < 0;
        }

        /**
         * 获取天数
         *
         * @return 天数
         */
        public int getDayCount() {
            return dayCount;
        }


        /**
         * 获取初一的儒略日
         *
         * @return 初一的儒略日
         */
        public double getFirstJulianDay() {
            return firstJulianDay;
        }

        public int getIndex() {
            return index;
        }

        public int getZhiIndex() {
            return zhiIndex;
        }

        public int getGanIndex() {
            int offset = (Year.from(year).getGanIndex() + 1) % 5 * 2;
            return (index - 1 + offset) % 10;
        }

        public String getGan() {
            return Fields.CN_GAN[getGanIndex() + 1];
        }

        public String getZhi() {
            return Fields.CN_ZHI[zhiIndex + 1];
        }

        public String getGanZhi() {
            return getGan() + getZhi();
        }

        public String getPositionXi() {
            return POSITION_XI[getGanIndex() + 1];
        }

        public String getPositionXiDesc() {
            return POSITION_DESC.get(getPositionXi());
        }

        public String getPositionYangGui() {
            return POSITION_YANG_GUI[getGanIndex() + 1];
        }

        public String getPositionYangGuiDesc() {
            return POSITION_DESC.get(getPositionYangGui());
        }

        public String getPositionYinGui() {
            return POSITION_YIN_GUI[getGanIndex() + 1];
        }

        public String getPositionYinGuiDesc() {
            return POSITION_DESC.get(getPositionYinGui());
        }

        public String getPositionFu(int sect) {
            return (1 == sect ? POSITION_FU : POSITION_FU_2)[getGanIndex() + 1];
        }

        public String getPositionFuDesc(int sect) {
            return POSITION_DESC.get(getPositionFu(sect));
        }

        public String getPositionCai() {
            return POSITION_CAI[getGanIndex() + 1];
        }

        public String getPositionCaiDesc() {
            return POSITION_DESC.get(getPositionCai());
        }

        /**
         * 获取太岁方位
         *
         * @return 方位，如艮
         */
        public String getPositionTaiSui() {
            String p;
            int m = Math.abs(month) % 4;
            switch (m) {
                case 0:
                    p = "巽";
                    break;
                case 1:
                    p = "艮";
                    break;
                case 3:
                    p = "坤";
                    break;
                default:
                    p = POSITION_GAN[Solar.from(getFirstJulianDay()).getLunar().getMonthGanIndex()];
            }
            return p;
        }

        /**
         * 获取太岁方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionTaiSuiDesc() {
            return POSITION_DESC.get(getPositionTaiSui());
        }

        /**
         * 获取月九星
         *
         * @return 九星
         */
        public NineStar getNineStar() {
            int index = Year.from(year).getZhiIndex() % 3;
            int m = Math.abs(month);
            int monthZhiIndex = (13 + m) % 12;
            int n = 27 - (index * 3);
            if (monthZhiIndex < BASE_MONTH_ZHI_INDEX) {
                n -= 3;
            }
            int offset = (n - monthZhiIndex) % 9;
            return new NineStar(offset);
        }

        /**
         * 获取往后推几个月的阴历月，如果要往前推，则月数用负数
         *
         * @param n 月数
         * @return 阴历月
         */
        public Month next(int n) {
            if (0 == n) {
                return Month.from(year, month);
            } else if (n > 0) {
                int rest = n;
                int ny = year;
                int iy = ny;
                int im = month;
                int index = 0;
                List<Month> months = Year.from(ny).getMonths();
                while (true) {
                    int size = months.size();
                    for (int i = 0; i < size; i++) {
                        Month m = months.get(i);
                        if (m.getYear() == iy && m.getMonth() == im) {
                            index = i;
                            break;
                        }
                    }
                    int more = size - index - 1;
                    if (rest < more) {
                        break;
                    }
                    rest -= more;
                    Month lastMonth = months.get(size - 1);
                    iy = lastMonth.getYear();
                    im = lastMonth.getMonth();
                    ny++;
                    months = Year.from(ny).getMonths();
                }
                return months.get(index + rest);
            } else {
                int rest = -n;
                int ny = year;
                int iy = ny;
                int im = month;
                int index = 0;
                List<Month> months = Year.from(ny).getMonths();
                while (true) {
                    int size = months.size();
                    for (int i = 0; i < size; i++) {
                        Month m = months.get(i);
                        if (m.getYear() == iy && m.getMonth() == im) {
                            index = i;
                            break;
                        }
                    }
                    if (rest <= index) {
                        break;
                    }
                    rest -= index;
                    Month firstMonth = months.get(0);
                    iy = firstMonth.getYear();
                    im = firstMonth.getMonth();
                    ny--;
                    months = Year.from(ny).getMonths();
                }
                return months.get(index - rest);
            }
        }

        @Override
        public String toString() {
            return year + "年" + (isLeap() ? "闰" : Normal.EMPTY) + Fields.CN_MONTH[Math.abs(month)] + "月(" + dayCount + "天)";
        }

    }

    /**
     * 时辰
     */
    public static class Time {

        /**
         * 天干下标，0-9
         */
        private final int ganIndex;

        /**
         * 地支下标，0-11
         */
        private final int zhiIndex;

        /**
         * 阴历
         */
        private final Lunar lunar;

        public Time(int lunarYear, int lunarMonth, int lunarDay, int hour, int minute, int second) {
            this.lunar = Lunar.from(lunarYear, lunarMonth, lunarDay, hour, minute, second);
            this.zhiIndex = Lunar.getTimeZhiIndex(String.format("%02d:%02d", hour, minute));
            this.ganIndex = (lunar.getDayGanIndexExact() % 5 * 2 + zhiIndex) % 10;
        }

        public static Time from(int lunarYear, int lunarMonth, int lunarDay, int hour, int minute, int second) {
            return new Time(lunarYear, lunarMonth, lunarDay, hour, minute, second);
        }

        public int getGanIndex() {
            return ganIndex;
        }

        public int getZhiIndex() {
            return zhiIndex;
        }

        /**
         * 获取生肖
         *
         * @return 生肖，如虎
         */
        public String getShengXiao() {
            return Fields.CN_ANIMAL[zhiIndex + 1];
        }

        /**
         * 获取地支
         *
         * @return 地支
         */
        public String getZhi() {
            return Fields.CN_ZHI[zhiIndex + 1];
        }

        /**
         * 获取天干
         *
         * @return 天干
         */
        public String getGan() {
            return Fields.CN_GAN[ganIndex + 1];
        }

        /**
         * 获取干支（时柱）
         *
         * @return 干支（时柱）
         */
        public String getGanZhi() {
            return getGan() + getZhi();
        }

        /**
         * 获取喜神方位
         *
         * @return 方位，如艮
         */
        public String getPositionXi() {
            return POSITION_XI[ganIndex + 1];
        }

        /**
         * 获取喜神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionXiDesc() {
            return POSITION_DESC.get(getPositionXi());
        }

        /**
         * 获取阳贵神方位
         *
         * @return 方位，如艮
         */
        public String getPositionYangGui() {
            return POSITION_YANG_GUI[ganIndex + 1];
        }

        /**
         * 获取阳贵神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionYangGuiDesc() {
            return POSITION_DESC.get(getPositionYangGui());
        }

        /**
         * 获取阴贵神方位
         *
         * @return 阴贵神方位，如艮
         */
        public String getPositionYinGui() {
            return POSITION_YIN_GUI[ganIndex + 1];
        }

        /**
         * 获取阴贵神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionYinGuiDesc() {
            return POSITION_DESC.get(getPositionYinGui());
        }

        /**
         * 获取福神方位
         *
         * @return 方位，如艮
         */
        public String getPositionFu() {
            return getPositionFu(2);
        }

        /**
         * 获取福神方位
         *
         * @param sect 流派，1或2
         * @return 方位，如艮
         */
        public String getPositionFu(int sect) {
            return (1 == sect ? POSITION_FU : POSITION_FU_2)[ganIndex + 1];
        }

        /**
         * 获取福神方位描述（默认流派：2）
         *
         * @return 福神方位描述，如东北
         */
        public String getPositionFuDesc() {
            return getPositionFuDesc(2);
        }

        /**
         * 获取福神方位描述
         *
         * @param sect 流派，1或2
         * @return 方位描述，如东北
         */
        public String getPositionFuDesc(int sect) {
            return POSITION_DESC.get(getPositionFu(sect));
        }

        /**
         * 获取财神方位
         *
         * @return 方位，如艮
         */
        public String getPositionCai() {
            return POSITION_CAI[ganIndex + 1];
        }

        /**
         * 获取财神方位描述
         *
         * @return 方位描述，如东北
         */
        public String getPositionCaiDesc() {
            return POSITION_DESC.get(getPositionCai());
        }

        /**
         * 获取纳音
         *
         * @return 纳音，如剑锋金
         */
        public String getNaYin() {
            return NAYIN.get(getGanZhi());
        }

        /**
         * 获取值时天神
         *
         * @return 值时天神
         */
        public String getTianShen() {
            return TIAN_SHEN[(zhiIndex + ZHI_TIAN_SHEN_OFFSET.get(lunar.getDayZhiExact())) % 12 + 1];
        }

        /**
         * 获取值时天神类型：黄道/黑道
         *
         * @return 值时天神类型：黄道/黑道
         */
        public String getTianShenType() {
            return TIAN_SHEN_TYPE.get(getTianShen());
        }

        /**
         * 获取值时天神吉凶
         *
         * @return 吉/凶
         */
        public String getTianShenLuck() {
            return TIAN_SHEN_TYPE_LUCK.get(getTianShenType());
        }

        /**
         * 获取时冲
         *
         * @return 时冲，如申
         */
        public String getChong() {
            return CHONG[zhiIndex];
        }

        /**
         * 获取时煞
         *
         * @return 时煞，如北
         */
        public String getSha() {
            return SHA.get(getZhi());
        }

        /**
         * 获取时冲生肖
         *
         * @return 时冲生肖，如猴
         */
        public String getChongShengXiao() {
            String chong = getChong();
            for (int i = 0, j = Fields.CN_ZHI.length; i < j; i++) {
                if (Fields.CN_ZHI[i].equals(chong)) {
                    return Fields.CN_ANIMAL[i];
                }
            }
            return "";
        }

        /**
         * 获取时冲描述
         *
         * @return 时冲描述，如(壬申)猴
         */
        public String getChongDesc() {
            return "(" + getChongGan() + getChong() + ")" + getChongShengXiao();
        }

        /**
         * 获取无情之克的时冲天干
         *
         * @return 无情之克的时冲天干，如甲
         */
        public String getChongGan() {
            return CHONG_GAN[ganIndex];
        }

        /**
         * 获取有情之克的时冲天干
         *
         * @return 有情之克的时冲天干，如甲
         */
        public String getChongGanTie() {
            return CHONG_GAN_TIE[ganIndex];
        }

        /**
         * 获取宜，如果没有，返回["无"]
         *
         * @return 宜
         */
        public List<String> getYi() {
            return getTimeYi(lunar.getDayInGanZhiExact(), getGanZhi());
        }

        /**
         * 获取忌，如果没有，返回["无"]
         *
         * @return 忌
         */
        public List<String> getJi() {
            return getTimeJi(lunar.getDayInGanZhiExact(), getGanZhi());
        }

        /**
         * 获取值时九星（时家紫白星歌诀：三元时白最为佳，冬至阳生顺莫差，孟日七宫仲一白，季日四绿发萌芽，每把时辰起甲子，本时星耀照光华，时星移入中宫去，顺飞八方逐细查。夏至阴生逆回首，孟归三碧季加六，仲在九宫时起甲，依然掌中逆轮跨。）
         *
         * @return 值时九星
         */
        public NineStar getNineStar() {
            //顺逆
            String solarYmd = lunar.getSolar().build(false);
            Map<String, Solar> jieQi = lunar.getSolarTermTable();
            boolean asc = solarYmd.compareTo(jieQi.get("冬至").toYmd()) >= 0 && solarYmd.compareTo(jieQi.get("夏至").toYmd()) < 0;
            int start = asc ? 7 : 3;
            String dayZhi = lunar.getDayZhi();
            if ("子午卯酉".contains(dayZhi)) {
                start = asc ? 1 : 9;
            } else if ("辰戌丑未".contains(dayZhi)) {
                start = asc ? 4 : 6;
            }
            int index = asc ? start + zhiIndex - 1 : start - zhiIndex - 1;
            if (index > 8) {
                index -= 9;
            }
            if (index < 0) {
                index += 9;
            }
            return new NineStar(index);
        }

        /**
         * 获取所在旬
         *
         * @return 旬
         */
        public String getXun() {
            return Lunar.getXun(getGanZhi());
        }

        /**
         * 获取值时空亡
         *
         * @return 空亡(旬空)
         */
        public String getXunKong() {
            return Lunar.getXunKong(getGanZhi());
        }

        /**
         * 获取当前时辰的最早时分
         *
         * @return 时分，如：21:00
         */
        public String getMinHm() {
            int hour = lunar.getHour();
            if (hour < 1) {
                return "00:00";
            } else if (hour > 22) {
                return "23:00";
            }
            return String.format("%02d:00", hour % 2 == 0 ? hour - 1 : hour);
        }

        /**
         * 获取当前时辰的最晚时分
         *
         * @return 时分，如：22:59
         */
        public String getMaxHm() {
            int hour = lunar.getHour();
            if (hour < 1) {
                return "00:59";
            } else if (hour > 22) {
                return "23:59";
            }
            return String.format("%02d:59", hour % 2 == 0 ? hour : hour + 1);
        }

        @Override
        public String toString() {
            return getGanZhi();
        }

    }

}