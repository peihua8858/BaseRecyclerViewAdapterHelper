package com.chad.library.adapter.base.node

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.fz.common.collections.isNonEmpty
import java.util.*

/**
 * 节点数据包装类
 * @author dingpeihua
 * @date 2020/12/20 10:39
 * @version 1.0
 */
abstract class BaseNodeModel<T> : BaseExpandNode<T>(), MultiItemEntity {
    override var itemType: Int = 0
    final override val childNode: MutableList<BaseNode<T>>?
        get() {
            return subItems as? MutableList<BaseNode<T>>
        }
    var parent: Any? = null
    var isChecked: Boolean = false

    abstract var subItems: MutableList<T>?
    var spitCount = -1
    /**
     * 获取选中的item
     *
     * @author dingpeihua
     * @date 2019/11/14 10:47
     * @version 1.0
     */
    fun getCheckedSubItems(): List<T> {
        val checkedItem: MutableList<T> = ArrayList()
        if (hasChildren) {
            val subItems: List<T>? = subItems
            if (subItems != null && subItems.isNotEmpty()) {
                for (child in subItems) {
                    if (child is BaseNodeModel<*> && child.isChecked) {
                        checkedItem.add(child);
                    }
                }
            }
        }
        return checkedItem
    }

    fun spitCount(): Int {
       return spitCount
    }

    fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        val subItems: MutableList<T>? = subItems
        return if (subItems.isNonEmpty()) {
            if (fromIndex >= subItems.size || toIndex > subItems.size) {
                subItems
            } else subItems.subList(fromIndex, toIndex)
        } else ArrayList()
    }

    fun getAllCount(): Int {
        TODO("Not yet implemented")
    }

    fun hasSubItem(): Boolean {
        return childNode.isNonEmpty()
    }

    /**
     * 路径的深度
     */
    var treeDepth: Int = 0

    open fun hasChildren(): Boolean {
        return childNode.isNonEmpty()
    }

    val hasChildren: Boolean
        get() {
            return hasChildren()
        }

    companion object {
        /**
         * 父节点
         */
        const val ITEM_TYPE_PARENT: Int = 0x1101

        /**
         * 子节点
         */
        const val ITEM_TYPE_CHILD: Int = 0x1102

        /**
         * 递归处理实体层级关系
         *
         * @param list
         * @param treeDepth 路径深度
         * @author dingpeihua
         * @date 2019/11/7 19:54
         * @version 1.0
         */
        @JvmStatic
        fun <T : BaseNodeModel<*>> processExpandableItem(list: List<T>?, treeDepth: Int) {
            var tempTreeDepth = treeDepth
            tempTreeDepth++
            if (list != null && list.isNotEmpty()) {
                for (expandableItem in list) {
                    expandableItem.treeDepth = tempTreeDepth
                    if (expandableItem.hasChildren) {
                        expandableItem.itemType = ITEM_TYPE_PARENT
                        val subItems = expandableItem.subItems
                        processExpandableItem(subItems as MutableList<BaseNodeModel<*>>, tempTreeDepth)
                    } else {
                        expandableItem.itemType = ITEM_TYPE_CHILD
                    }
                }
            }
        }

        /**
         * 递归处理实体层级关系
         *
         * @param data
         * @author dingpeihua
         * @date 2019/11/7 19:54
         * @version 1.0
         */
        @JvmStatic
        fun <T : BaseNodeModel<*>> processExpandableItem(data: List<T>?) {
            processExpandableItem(data, 0)
        }
    }
}