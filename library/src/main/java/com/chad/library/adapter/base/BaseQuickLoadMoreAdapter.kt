package com.chad.library.adapter.base

import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.module.BaseLoadMoreModule
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class BaseQuickLoadMoreAdapter<T, VH : BaseViewHolder>(
    @LayoutRes private val layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, VH>(layoutResId, data), LoadMoreModule {
    override fun addLoadMoreModule(baseQuickAdapter: BaseQuickAdapter<*, *>): BaseLoadMoreModule {
        return BaseLoadMoreModule(baseQuickAdapter)
    }
}