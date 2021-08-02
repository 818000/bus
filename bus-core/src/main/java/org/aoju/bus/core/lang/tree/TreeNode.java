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
package org.aoju.bus.core.lang.tree;

import java.util.Map;
import java.util.Objects;

/**
 * 树节点 每个属性都可以在{@link  NodeConfig}中被重命名
 * 在你的项目里它可以是部门实体、地区实体等任意类树节点实体
 * 类树节点实体: 包含key，父Key.不限于这些属性的可以构造成一颗树的实体对象
 *
 * @param <T> ID类型
 * @author Kimi Liu
 * @version 6.2.6
 * @since JDK 1.8+
 */
public class TreeNode<T> implements Node<T> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private T id;
    /**
     * 父节点ID
     */
    private T parentId;
    /**
     * 名称
     */
    private CharSequence name;
    /**
     * 顺序 越小优先级越高 默认0
     */
    private Comparable<?> weight = 0;
    /**
     * 扩展字段
     */
    private Map<String, Object> extra;

    /**
     * 空构造
     */
    public TreeNode() {
    }

    /**
     * 构造
     *
     * @param id       ID
     * @param parentId 父节点ID
     * @param name     名称
     * @param weight   权重
     */
    public TreeNode(T id, T parentId, String name, Comparable<?> weight) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        if (weight != null) {
            this.weight = weight;
        }

    }

    @Override
    public T getId() {
        return id;
    }

    @Override
    public TreeNode<T> setId(T id) {
        this.id = id;
        return this;
    }

    @Override
    public T getParentId() {
        return this.parentId;
    }

    @Override
    public TreeNode<T> setParentId(T parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public CharSequence getName() {
        return name;
    }

    @Override
    public TreeNode<T> setName(CharSequence name) {
        this.name = name;
        return this;
    }

    @Override
    public Comparable<?> getWeight() {
        return weight;
    }

    @Override
    public TreeNode<T> setWeight(Comparable<?> weight) {
        this.weight = weight;
        return this;
    }

    /**
     * 获取扩展字段
     *
     * @return 扩展字段Map
     */
    public Map<String, Object> getExtra() {
        return extra;
    }

    /**
     * 设置扩展字段
     *
     * @param extra 扩展字段
     * @return this
     */
    public TreeNode<T> setExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return Objects.equals(id, treeNode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
