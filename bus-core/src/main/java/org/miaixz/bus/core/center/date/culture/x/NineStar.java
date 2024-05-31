package org.miaixz.bus.core.center.date.culture.x;

/**
 * 九星
 * 玄空九星、奇门九星都来源于北斗九星，九数、七色、五行、后天八卦方位都是相通的。</p>
 */
public class NineStar {

    /**
     * 序号，0到8
     */
    protected int index;

    public NineStar(int index) {
        this.index = index;
    }

    public static NineStar fromIndex(int index) {
        return new NineStar(index);
    }

    /**
     * 获取九数
     *
     * @return 九数
     */
    public String getNumber() {
        return Literal.NINESTAR_NUMBER[index];
    }

    /**
     * 获取七色
     *
     * @return 七色
     */
    public String getColor() {
        return Literal.NINESTAR_COLOR[index];
    }

    /**
     * 获取五行
     *
     * @return 五行
     */
    public String getWuXing() {
        return Literal.NINESTAR_WU_XING[index];
    }


    /**
     * 获取方位
     *
     * @return 方位
     */
    public String getPosition() {
        return Literal.NINESTAR_POSITION[index];
    }

    /**
     * 获取方位描述
     *
     * @return 方位描述
     */
    public String getPositionDesc() {
        return Literal.LUNAR_POSITION_DESC.get(getPosition());
    }

    /**
     * 获取玄空九星名称
     *
     * @return 玄空九星名称
     */
    public String getNameInXuanKong() {
        return Literal.NINESTAR_NAME_XUAN_KONG[index];
    }

    /**
     * 获取北斗九星名称
     *
     * @return 北斗九星名称
     */
    public String getNameInBeiDou() {
        return Literal.NINESTAR_NAME_BEI_DOU[index];
    }

    /**
     * 获取奇门九星名称
     *
     * @return 奇门九星名称
     */
    public String getNameInQiMen() {
        return Literal.NINESTAR_NAME_QI_MEN[index];
    }

    /**
     * 获取太乙九神名称
     *
     * @return 太乙九神名称
     */
    public String getNameInTaiYi() {
        return Literal.NINESTAR_NAME_TAI_YI[index];
    }

    /**
     * 获取奇门九星吉凶
     *
     * @return 大吉/小吉/大凶/小凶
     */
    public String getLuckInQiMen() {
        return Literal.NINESTAR_NINESTAR_LUCK_QI_MEN[index];
    }

    /**
     * 获取玄空九星吉凶
     *
     * @return 吉/凶
     */
    public String getLuckInXuanKong() {
        return Literal.NINESTAR_LUCK_XUAN_KONG[index];
    }

    /**
     * 获取奇门九星阴阳
     *
     * @return 阴/阳
     */
    public String getYinYangInQiMen() {
        return Literal.NINESTAR_YIN_YANG_QI_MEN[index];
    }

    /**
     * 获取太乙九神类型
     *
     * @return 吉神/凶神/安神
     */
    public String getTypeInTaiYi() {
        return Literal.NINESTAR_TYPE_TAI_YI[index];
    }

    /**
     * 获取八门（奇门遁甲）
     *
     * @return 八门
     */
    public String getBaMenInQiMen() {
        return Literal.NINESTAR_BA_MEN_QI_MEN[index];
    }

    /**
     * 获取太乙九神歌诀
     *
     * @return 太乙九神歌诀
     */
    public String getSongInTaiYi() {
        return Literal.NINESTAR_SONG_TAI_YI[index];
    }

    /**
     * 获取九星序号，从0开始
     *
     * @return 序号
     */
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return getNumber() + getColor() + getWuXing() + getNameInBeiDou();
    }

    /**
     * 获取详细描述
     *
     * @return 详细描述
     */
    public String toFullString() {
        StringBuilder s = new StringBuilder();
        s.append(getNumber());
        s.append(getColor());
        s.append(getWuXing());
        s.append(" ");
        s.append(getPosition());
        s.append("(");
        s.append(getPositionDesc());
        s.append(") ");
        s.append(getNameInBeiDou());
        s.append(" 玄空[");
        s.append(getNameInXuanKong());
        s.append(" ");
        s.append(getLuckInXuanKong());
        s.append("] 奇门[");
        s.append(getNameInQiMen());
        s.append(" ");
        s.append(getLuckInQiMen());
        if (getBaMenInQiMen().length() > 0) {
            s.append(" ");
            s.append(getBaMenInQiMen());
            s.append("门");
        }
        s.append(" ");
        s.append(getYinYangInQiMen());
        s.append("] 太乙[");
        s.append(getNameInTaiYi());
        s.append(" ");
        s.append(getTypeInTaiYi());
        s.append("]");
        return s.toString();
    }

}
