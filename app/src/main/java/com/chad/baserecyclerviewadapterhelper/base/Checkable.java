package com.chad.baserecyclerviewadapterhelper.base;

/**
 * 是否可选中
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/14 10:33
 */
public interface Checkable extends Cloneable {
    /**
     * 设置选中状态
     *
     * @param checked The new checked state
     */
    void setChecked(boolean checked);

    /**
     * 返回选中状态
     *
     * @return The current checked state of the view
     */
    boolean isChecked();
}
