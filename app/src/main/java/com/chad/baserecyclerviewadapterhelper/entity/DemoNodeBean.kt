package com.chad.baserecyclerviewadapterhelper.entity

import com.chad.library.adapter.base.node.BaseNode

class DemoNodeBean : BaseNode<DemoNodeBean> {
    var products: MutableList<DemoNodeBean>? = null
    override val childNode: MutableList<DemoNodeBean>
        get() = products ?: mutableListOf()
}