package com.chad.library.adapter.base

import androidx.annotation.IntRange
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.listener.IExpandable
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.util.*


/**
 * 多层级嵌套树形适配器
 *
 * @Deprecated [BaseNodeAdapter]
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/7 15:26
 */
@Deprecated(message = "Please use BaseNodeAdapter class",
        replaceWith = ReplaceWith("BaseNodeAdapter",
                "com.chad.library.adapter.base.BaseNodeAdapter"))
abstract class BaseMultiTreeAdapter<T : MultiItemEntity, K : BaseViewHolder>(data: MutableList<T>?) : BaseMultiItemQuickAdapter<T, K>(data) {
    private fun recursiveCollapse(@IntRange(from = 0) position: Int): Int {
        val item: T? = getItem(position)
        if (item == null || !isExpandable(item)) {
            return 0
        }
        val expandable = item as IExpandable<*>
        if (!expandable.isExpanded) {
            return 0
        }
        val collapseList: MutableList<T> = ArrayList()
        val itemLevel = expandable.level
        var itemTemp: T
        var i = position + 1
        val n = data.size
        while (i < n) {
            itemTemp = data[i]
            if (itemTemp is IExpandable<*> && (itemTemp as IExpandable<*>).level <= itemLevel) {
                break
            }
            collapseList.add(itemTemp)
            i++
        }
        data.removeAll(collapseList)
        return collapseList.size
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item, which includes the header layout count.
     * @param animate  collapse with animation or not.
     * @param notify   notify the recyclerView refresh UI or not.
     * @return the number of subItems collapsed.
     */
    open fun collapse(@IntRange(from = 0) position: Int, animate: Boolean, notify: Boolean): Int {
        var pos = position
        pos -= headerLayoutCount
        val expandable = getExpandableItem(pos) ?: return 0
        val subItemCount = recursiveCollapse(pos)
        expandable.isExpanded = false
        val parentPos = pos + headerLayoutCount
        if (notify) {
            if (animate) {
                notifyItemChanged(parentPos)
                notifyItemRangeRemoved(parentPos + 1, subItemCount)
            } else {
                notifyDataSetChanged()
            }
        }
        return subItemCount
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item, which includes the header layout count.
     * @return the number of subItems collapsed.
     */
    fun collapse(@IntRange(from = 0) position: Int): Int {
        return collapse(position, true, true)
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item, which includes the header layout count.
     * @return the number of subItems collapsed.
     */
    fun collapse(@IntRange(from = 0) position: Int, animate: Boolean): Int {
        return collapse(position, animate, true)
    }

    fun isExpandable(item: T?): Boolean {
        return item != null && item is IExpandable<*>
    }

    private fun getExpandableItem(position: Int): IExpandable<T>? {
        val item: T? = getItem(position)
        return if (isExpandable(item)) {
            item as IExpandable<T>?
        } else {
            null
        }
    }

    fun hasSubItems(item: IExpandable<*>?): Boolean {
        if (item == null) {
            return false
        }
        val list = item.subItems
        return list != null && list.size > 0
    }

    private fun recursiveExpand(position: Int, list: List<*>): Int {
        var count = list.size
        var pos = position + list.size - 1
        var i = list.size - 1
        while (i >= 0) {
            if (list[i] is IExpandable<*>) {
                val item = list[i] as IExpandable<*>
                if (item.isExpanded && hasSubItems(item)) {
                    val subList = item.subItems
                    data.addAll(pos + 1, subList as Collection<T>)
                    val subItemCount = recursiveExpand(pos + 1, subList)
                    count += subItemCount
                }
            }
            i--
            pos--
        }
        return count
    }

    /**
     * Expand an expandable item
     *
     * @param position     position of the item
     * @param animate      expand items with animation
     * @param shouldNotify notify the RecyclerView to rebind items, **false** if you want to do it
     * yourself.
     * @return the number of items that have been added.
     */
    open fun expand(@IntRange(from = 0) position: Int, animate: Boolean, shouldNotify: Boolean): Int {
        var position = position
        position -= headerLayoutCount
        val expandable = getExpandableItem(position) ?: return 0
        if (!hasSubItems(expandable)) {
            expandable.isExpanded = true
            notifyItemChanged(position)
            return 0
        }
        var subItemCount = 0
        if (!expandable.isExpanded) {
            val list = expandable.subItems
            data.addAll(position + 1, list)
            subItemCount += recursiveExpand(position + 1, list)
            expandable.isExpanded = true
            //            subItemCount += list.size();
        }
        val parentPos = position + headerLayoutCount
        if (shouldNotify) {
            if (animate) {
                notifyItemChanged(parentPos)
                notifyItemRangeInserted(parentPos + 1, subItemCount)
            } else {
                notifyDataSetChanged()
            }
        }
        return subItemCount
    }

    /**
     * Expand an expandable item with animation.
     *
     * @param position position of the item, which includes the header layout count.
     * @return the number of items that have been added.
     */
    open fun expand(@IntRange(from = 0) position: Int): Int {
        return expand(position, true, true)
    }

    open fun expandAll(position: Int, animate: Boolean, notify: Boolean): Int {
        var position = position
        position -= headerLayoutCount
        var endItem: T? = null
        if (position + 1 < this.data.size) {
            endItem = getItem(position + 1)
        }
        val expandable = getExpandableItem(position) ?: return 0
        if (!hasSubItems(expandable)) {
            expandable.isExpanded = true
            notifyItemChanged(position)
            return 0
        }
        var count = expand(position + headerLayoutCount, false, false)
        for (i in position + 1 until this.data.size) {
            val item: T? = getItem(i)
            if (item != null && item == endItem) {
                break
            }
            if (isExpandable(item)) {
                count += expand(i + headerLayoutCount, false, false)
            }
        }
        if (notify) {
            if (animate) {
                notifyItemRangeInserted(position + headerLayoutCount + 1, count)
            } else {
                notifyDataSetChanged()
            }
        }
        return count
    }

    /**
     * expand the item and all its subItems
     *
     * @param position position of the item, which includes the header layout count.
     * @param init     whether you are initializing the recyclerView or not.
     * if **true**, it won't notify recyclerView to redraw UI.
     * @return the number of items that have been added to the adapter.
     */
    fun expandAll(position: Int, init: Boolean): Int {
        return expandAll(position, true, !init)
    }

    open fun expandAll() {
        for (i in data.size - 1 + headerLayoutCount downTo headerLayoutCount) {
            expandAll(i, false, false)
        }
    }

    /**
     * Get the parent item position of the IExpandable item
     *
     * @return return the closest parent item position of the IExpandable.
     * if the IExpandable item's level is 0, return itself position.
     * if the item's level is negative which mean do not implement this, return a negative
     * if the item is not exist in the data list, return a negative.
     */
    open fun getParentPosition(item: T): Int {
        val position = getItemPosition(item)
        if (position == -1) {
            return -1
        }
        // if the item is IExpandable, return a closest IExpandable item position whose level smaller than this.
        // if it is not, return the closest IExpandable item position whose level is not negative
        val level: Int = if (item is IExpandable<*>) {
            (item as IExpandable<*>).level
        } else {
            Int.MAX_VALUE
        }
        if (level == 0) {
            return position
        } else if (level == -1) {
            return -1
        }
        for (i in position downTo 0) {
            val temp: T = data[i]
            if (temp is IExpandable<*>) {
                if (temp.level in 0 until level) {
                    return i
                }
            }
        }
        return -1
    }
}