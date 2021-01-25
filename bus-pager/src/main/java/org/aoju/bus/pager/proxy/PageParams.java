/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.pager.proxy;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.pager.Page;
import org.aoju.bus.pager.PageContext;
import org.aoju.bus.pager.Paging;
import org.aoju.bus.pager.RowBounds;
import org.aoju.bus.pager.plugin.PageFromObject;

import java.util.Properties;

/**
 * Page 参数信息
 *
 * @author Kimi Liu
 * @version 6.1.9
 * @since JDK 1.8+
 */
public class PageParams {

    /**
     * RowBounds参数offset作为PageNo使用 - 默认不使用
     */
    protected boolean offsetAsPageNo = false;
    /**
     * RowBounds是否进行count查询 - 默认不查询
     */
    protected boolean rowBoundsWithCount = false;
    /**
     * 当设置为true的时候,如果pagesize设置为0(或RowBounds的limit=0),就不执行分页,返回全部结果
     */
    protected boolean pageSizeZero = false;
    /**
     * 分页合理化
     */
    protected boolean reasonable = false;
    /**
     * 是否支持接口参数来传递分页参数,默认false
     */
    protected boolean supportMethodsArguments = false;
    /**
     * 默认count(0)
     */
    protected String countColumn = Symbol.ZERO;

    /**
     * 获取分页参数
     *
     * @param parameterObject 参数
     * @param rowBounds       rowBounds对象
     * @return the page
     */
    public Page getPage(Object parameterObject, org.apache.ibatis.session.RowBounds rowBounds) {
        Page page = PageContext.getLocalPage();
        if (page == null) {
            if (rowBounds != org.apache.ibatis.session.RowBounds.DEFAULT) {
                if (offsetAsPageNo) {
                    page = new Page(rowBounds.getOffset(), rowBounds.getLimit(), rowBoundsWithCount);
                } else {
                    page = new Page(new int[]{rowBounds.getOffset(), rowBounds.getLimit()}, rowBoundsWithCount);
                    // offsetAsPageNo=false的时候,由于PageNo问题,不能使用reasonable,这里会强制为false
                    page.setReasonable(false);
                }
                if (rowBounds instanceof RowBounds) {
                    RowBounds pageRowBounds = (RowBounds) rowBounds;
                    page.setCount(pageRowBounds.getCount() == null || pageRowBounds.getCount());
                }
            } else if (parameterObject instanceof Paging || supportMethodsArguments) {
                try {
                    page = PageFromObject.getPageFromObject(parameterObject, false);
                } catch (Exception e) {
                    return null;
                }
            }
            if (page == null) {
                return null;
            }
            PageContext.setLocalPage(page);
        }
        // 分页合理化
        if (page.getReasonable() == null) {
            page.setReasonable(reasonable);
        }
        // 当设置为true的时候,如果pagesize设置为0(或RowBounds的limit=0),就不执行分页,返回全部结果
        if (page.getPageSizeZero() == null) {
            page.setPageSizeZero(pageSizeZero);
        }
        return page;
    }

    public void setProperties(Properties properties) {
        // offset作为PageNo使用
        String offsetAsPageNo = properties.getProperty("offsetAsPageNo");
        this.offsetAsPageNo = Boolean.parseBoolean(offsetAsPageNo);
        // RowBounds方式是否做count查询
        String rowBoundsWithCount = properties.getProperty("rowBoundsWithCount");
        this.rowBoundsWithCount = Boolean.parseBoolean(rowBoundsWithCount);
        // 当设置为true的时候,如果pagesize设置为0(或RowBounds的limit=0),就不执行分页
        String pageSizeZero = properties.getProperty("pageSizeZero");
        this.pageSizeZero = Boolean.parseBoolean(pageSizeZero);
        // 分页合理化,true开启,如果分页参数不合理会自动修正 默认false不启用
        String reasonable = properties.getProperty("reasonable");
        this.reasonable = Boolean.parseBoolean(reasonable);
        // 是否支持接口参数来传递分页参数,默认false
        String supportMethodsArguments = properties.getProperty("supportMethodsArguments");
        this.supportMethodsArguments = Boolean.parseBoolean(supportMethodsArguments);
        // 默认count列
        String countColumn = properties.getProperty("countColumn");
        if (PageFromObject.isNotEmpty(countColumn)) {
            this.countColumn = countColumn;
        }
        // 当offsetAsPageNo=false的时候,不能参数映射
        PageFromObject.setParams(properties.getProperty("params"));
    }

    public boolean isOffsetAsPageNo() {
        return offsetAsPageNo;
    }

    public boolean isRowBoundsWithCount() {
        return rowBoundsWithCount;
    }

    public boolean isPageSizeZero() {
        return pageSizeZero;
    }

    public boolean isReasonable() {
        return reasonable;
    }

    public boolean isSupportMethodsArguments() {
        return supportMethodsArguments;
    }

    public String getCountColumn() {
        return countColumn;
    }

}
