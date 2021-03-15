package com.chad.baserecyclerviewadapterhelper.base;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiTreeAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.IExpandable;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 多层级嵌套树形适配器
 * 解决多层级嵌套，导致子节点的子节点无法正确折叠的问题
 * 由于remove方法使用{@link Collection#removeAll(Collection)},因此需要重写{@link T}的equals方法，
 * 支持默认部分展开折叠
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/7 15:26
 */
public abstract class BaseMultistageTreeAdapter<T extends MultiItemEntity, K extends BaseViewHolder> extends BaseMultiTreeAdapter<T, K> {
    public BaseMultistageTreeAdapter(List<T> data) {
        super(data);
    }

    /**
     * 切换状态
     */
    public final void toggleAction(int position) {
        T item = getItem(position);
        if (isExpanded(item)) {
            collapse(position);
        } else {
            expand(position);
        }
    }

    @Override
    public final int collapse(int position, boolean animate, boolean notify) {
        position -= getHeaderLayoutCount();
        IExpandable expandable = getExpandableItem(position);
        if (expandable == null) {
            return 0;
        }
        int subItemCount;
        int positionStart = position;
        if (enabledDefaultExpend(position)) {
            subItemCount = recursiveDefaultCollapse(position);
            expandable.setExpanded(false);
            IDefaultExpand defaultExpand = (IDefaultExpand) expandable;
            int spitCount = defaultExpand.spitCount();
            if (defaultExpand.getAllCount() > spitCount) {
                positionStart += spitCount;
            }
        } else {
            subItemCount = recursiveCollapse(position);
        }
        expandable.setExpanded(false);
        int parentPos = position + getHeaderLayoutCount();
        positionStart += getHeaderLayoutCount();
        if (notify) {
            if (animate) {
                notifyItemChanged(parentPos);
                notifyItemRangeRemoved(positionStart + 1, subItemCount);
            } else {
                notifyDataSetChanged();
            }
        }
        return subItemCount;
    }

    /**
     * 递归获取当前父节点下的所有子节点，包括子节点的子节点
     *
     * @param position
     * @author dingpeihua
     * @date 2019/11/7 15:35
     * @version 1.0
     */
    private int recursiveCollapse(@IntRange(from = 0) int position) {
        T item = getItem(position);
        if (item == null || !isExpandable(item)) {
            return 0;
        }
        IExpandable expandable = (IExpandable) item;
        if (!expandable.isExpanded() && !isDefaultExpanded(item)) {
            return 0;
        }
        List<T> children = recursiveChildrenCount(item);
        removeAll(children);
        return children.size();
    }

    private int recursiveDefaultCollapse(@IntRange(from = 0) int position) {
        T item = getItem(position);
        if (item == null) {
            return 0;
        }
        IDefaultExpand expandable = (IDefaultExpand) item;
        if (!expandable.isExpanded() && !isDefaultExpanded(item)) {
            return 0;
        }
        List<T> children = recursiveDefaultChildrenCount(item);
        removeAll(children);
        return children.size();
    }

    private IDefaultExpand getDefaultExpanded(T item) {
        if (item instanceof IDefaultExpand) {
            return (IDefaultExpand) item;
        }
        return null;
    }

    private boolean isDefaultExpanded(T item) {
        IDefaultExpand iDefaultExpand = getDefaultExpanded(item);
        return iDefaultExpand != null && iDefaultExpand.isDefExpanded();
    }

    private boolean enabledDefaultExpend(int position) {
        return enabledDefaultExpend(getItem(position));
    }

    private boolean enabledDefaultExpend(T item) {
        IDefaultExpand iDefaultExpand = getDefaultExpanded(item);
        return iDefaultExpand != null && iDefaultExpand.enabledDefaultExpend();
    }

    private IExpandable getExpandableItem(int position) {
        T item = getItem(position);
        return getExpandableItem(item);
    }

    private IExpandable getExpandableItem(T item) {
        if (isExpandable(item)) {
            return (IExpandable) item;
        } else {
            return null;
        }
    }

    private boolean isExpanded(T item) {
        return isExpanded(getExpandableItem(item));
    }

    private boolean isExpanded(IExpandable iExpandable) {
        return iExpandable != null && iExpandable.isExpanded();
    }

    /**
     * 刪除列表，如果子节点和父节点不是同一类型，则可重写次方法，按照索引位置移除
     *
     * @param collection
     * @author dingpeihua
     * @date 2019/11/8 10:18
     * @version 1.0
     */
    public void removeAll(Collection<T> collection) {
        getData().removeAll(collection);
    }

    public void removeAll() {
        getData().clear();
        notifyDataSetChanged();
    }

    /**
     * 递归获取当前节点及其所有展开的子节点
     *
     * @param item 当前节点
     * @author dingpeihua
     * @date 2019/11/7 16:34
     * @version 1.0
     */
    private List<T> recursiveChildrenCount(T item) {
        List<T> list = new ArrayList<>();
        recursiveChild(0, item, list);
        return list;
    }

    /**
     * 递归获取当前节点下所有展开的子节点
     *
     * @param item 当前节点
     * @author dingpeihua
     * @date 2019/11/7 16:34
     * @version 1.0
     */
    private void recursiveChild(int index, T item, List<T> list) {
        if (index > 0) list.add(item);
        ++index;
        if (item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;
            List<T> subItems = ((IExpandable) item).getSubItems();
            if (!expandable.isExpanded() && !isDefaultExpanded(item)) {
                return;
            }
            if (subItems != null) {
                T itemTemp;
                for (int i = 0; i < subItems.size(); i++) {
                    itemTemp = subItems.get(i);
                    recursiveChild(index, itemTemp, list);
                }
            }
        }
    }

    private List<T> recursiveDefaultChildrenCount(T item) {
        List<T> list = new ArrayList<>();
        recursiveDefaultChild(0, item, list);
        return list;
    }

    private void recursiveDefaultChild(int index, T item, List<T> list) {
        if (index > 0) list.add(item);
        ++index;
        IDefaultExpand iDefaultExpand = getDefaultExpanded(item);
        if (iDefaultExpand.enabledDefaultExpend()) {
            List<T> subItems = iDefaultExpand.subList(iDefaultExpand.spitCount(), iDefaultExpand.getAllCount());
            if (!iDefaultExpand.isExpanded() && !iDefaultExpand.isDefExpanded()) {
                return;
            }
            if (subItems != null) {
                T itemTemp;
                for (int i = 0; i < subItems.size(); i++) {
                    itemTemp = subItems.get(i);
                    recursiveChild(index, itemTemp, list);
                }
            }
        }
    }

    @Override
    public int expand(int position, boolean animate, boolean shouldNotify) {
        position -= getHeaderLayoutCount();
        T item = getItem(position);
        IExpandable expandable = getExpandableItem(item);
        if (expandable == null) {
            return 0;
        }
        if (!hasSubItems(expandable)) {
            expandable.setExpanded(true);
            notifyItemChanged(position);
            return 0;
        }
        int subItemCount = 0;
        int positionStart = position;
        if (!isExpanded(expandable)) {
            if (enabledDefaultExpend(item)) {
                IDefaultExpand defaultExpand = (IDefaultExpand) expandable;
                int spitCount = defaultExpand.spitCount();
                spitCount = spitCount == -1 ? Integer.MAX_VALUE : spitCount;
                if (isDefaultExpanded(item)) {
                    List list = defaultExpand.subList(defaultExpand.spitCount(), defaultExpand.getAllCount());
                    positionStart += spitCount;
                    getData().addAll(positionStart + 1, list);
                    subItemCount += recursiveExpand(positionStart + 1, list);
                    expandable.setExpanded(true);
                } else {
                    List list = defaultExpand.subList(0, spitCount);
                    getData().addAll(positionStart + 1, list);
                    subItemCount += recursiveExpand(positionStart + 1, list);
                    expandable.setExpanded(list.size() == defaultExpand.getAllCount());
                    if (defaultExpand.getAllCount() > spitCount) {
                        defaultExpand.setDefExpanded(true);
                    }
                }
            } else {
                List list = expandable.getSubItems();
                getData().addAll(positionStart + 1, list);
                subItemCount += recursiveExpand(positionStart + 1, list);
                expandable.setExpanded(true);
            }
        }
        int parentPos = position + getHeaderLayoutCount();
        positionStart += getHeaderLayoutCount();
        if (shouldNotify) {
            if (animate) {
                notifyItemChanged(parentPos);
                notifyItemRangeInserted(positionStart + 1, subItemCount);
            } else {
                notifyDataSetChanged();
            }
        }
        return subItemCount;
    }

    private int recursiveExpand(int position, @NonNull List<T> list) {
        int count = list.size();
        int pos = position + list.size() - 1;
        for (int i = list.size() - 1; i >= 0; i--, pos--) {
            if (list.get(i) instanceof IExpandable) {
                IExpandable expandable = (IExpandable) list.get(i);
                if (hasSubItems(expandable)) {
                    if (expandable.isExpanded() && hasSubItems(expandable)) {
                        List subList = expandable.getSubItems();
                        getData().addAll(pos + 1, subList);
                        int subItemCount = recursiveExpand(pos + 1, subList);
                        count += subItemCount;
                    }
                }
            }
        }
        return count;

    }

    @Override
    public void expandAll() {
        expandAll(getData().size());
    }

    /**
     * 展开指定数目
     *
     * @param count
     * @author dingpeihua
     * @date 2019/11/14 21:05
     * @version 1.0
     */
    public void expandAll(int count) {
        for (int i = count - 1 + getHeaderLayoutCount(); i >= getHeaderLayoutCount(); i--) {
            setEnabledDefaultExpand(i);
            expandAll(i, false, false);
        }
    }

    private void setEnabledDefaultExpand(int position) {
        T item = getItem(position);
        IDefaultExpand iDefaultExpand = getDefaultExpanded(item);
        if (iDefaultExpand != null && iDefaultExpand.hasSubItem()) {
            iDefaultExpand.setEnabledDefaultExpand(true);
        }
    }
}
