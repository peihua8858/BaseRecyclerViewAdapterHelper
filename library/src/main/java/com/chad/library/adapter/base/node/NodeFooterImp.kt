package com.chad.library.adapter.base.node

/**
 * 如果需要，可以实现此接口，返回脚部节点
 */
interface NodeFooterImp<T : BaseNode<T>> {
    /**
     * 返回脚部节点
     * @return BaseNode? 如果返回 null，则代表没有脚部节点
     */
    val footerNode: BaseNode<T>?
}