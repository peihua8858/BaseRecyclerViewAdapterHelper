package com.chad.library.adapter.base

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 加载更多
 * @author dingpeihua
 * @date 2021/3/11 17:48
 * @version 1.0
 */
abstract class BaseMultiItemLoadMoreAdapter<
        T : MultiItemEntity,
        VH : BaseViewHolder,
        >(data: MutableList<T>? = null) :
        BaseMultiItemQuickAdapter<T, VH>(data), LoadMoreModule