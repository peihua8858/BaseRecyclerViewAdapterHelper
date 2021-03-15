package com.chad.library.adapter.base.node

import com.chad.library.adapter.base.provider.BaseItemProvider

abstract class BaseNodeProvider<T : BaseNode<T>> : BaseItemProvider<T>() {

    override fun getAdapter(): BaseMultiNodeAdapter<T>? {
        return super.getAdapter() as? BaseMultiNodeAdapter
    }
}