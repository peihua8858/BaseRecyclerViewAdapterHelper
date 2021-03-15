package com.chad.library.adapter.base.viewholder

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseNodeAdapter

/**
 * ViewHolder 基类
 */
@Keep
open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    /**
     * Views indexed with their IDs
     */
    private val views: SparseArray<View> = SparseArray()

    /**
     * 如果使用了 DataBinding 绑定 View，可调用此方法获取 [ViewDataBinding]
     *
     * Deprecated, Please use [BaseDataBindingHolder]
     *
     * @return B?
     */
    @Deprecated("Please use BaseDataBindingHolder class", ReplaceWith("DataBindingUtil.getBinding(itemView)", "androidx.databinding.DataBindingUtil"))
    open fun <B : ViewDataBinding> getBinding(): B? = DataBindingUtil.getBinding(itemView)


    open fun <T : View> getView(@IdRes viewId: Int): T {
        val view = getViewOrNull<T>(viewId)
        checkNotNull(view) { "No view found with id $viewId" }
        return view
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T : View> getViewOrNull(@IdRes viewId: Int): T? {
        val view = views.get(viewId)
        if (view == null) {
            itemView.findViewById<T>(viewId)?.let {
                views.put(viewId, it)
                return it
            }
        }
        return view as? T
    }

    open fun <T : View> Int.findView(): T? {
        return itemView.findViewById(this)
    }

    open fun setText(@IdRes viewId: Int, value: CharSequence?): BaseViewHolder {
        getView<TextView>(viewId).text = value
        return this
    }

    open fun setText(@IdRes viewId: Int, @StringRes strId: Int): BaseViewHolder {
        getView<TextView>(viewId).setText(strId)
        return this
    }

    open fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        getView<TextView>(viewId).setTextColor(color)
        return this
    }

    open fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorRes: Int): BaseViewHolder {
        getView<TextView>(viewId).setTextColor(itemView.resources.getColor(colorRes))
        return this
    }

    open fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BaseViewHolder {
        getView<ImageView>(viewId).setImageResource(imageResId)
        return this
    }

    open fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?): BaseViewHolder {
        getView<ImageView>(viewId).setImageDrawable(drawable)
        return this
    }

    open fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?): BaseViewHolder {
        getView<ImageView>(viewId).setImageBitmap(bitmap)
        return this
    }

    open fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        getView<View>(viewId).setBackgroundColor(color)
        return this
    }

    open fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BaseViewHolder {
        getView<View>(viewId).setBackgroundResource(backgroundRes)
        return this
    }

    open fun setVisible(@IdRes viewId: Int, isVisible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
        return this
    }

    open fun setInvisible(@IdRes viewId: Int, isVisible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        return this
    }

    /**
     * @Deprecated [setVisible]
     */
    @Deprecated("为兼容就的使用方式", replaceWith = ReplaceWith("BaseViewHolder.setVisible(viewId, isVisible)", "com.chad.library.adapter.base.viewholder.BaseViewHolder"))
    fun setGone(@IdRes viewId: Int, isVisible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
        return this
    }

    open fun setEnabled(@IdRes viewId: Int, isEnabled: Boolean): BaseViewHolder {
        getView<View>(viewId).isEnabled = isEnabled
        return this
    }

    /**
     * 设置选中状态
     * @param id
     * @param isChecked
     * @author dingpeihua
     * @date 2020/10/18 13:57
     * @version 1.0
     */
    fun setChecked(@IdRes id: Int, isChecked: Boolean): BaseViewHolder {
        val checkable: Checkable = this.getView(id)
        checkable.isChecked = isChecked
        return this
    }

    /**
     * 设置选中状态
     * @param id
     * @param isSelected
     * @author dingpeihua
     * @date 2020/10/18 13:57
     * @version 1.0
     */
    fun setSelected(@IdRes id: Int, isSelected: Boolean): BaseViewHolder {
        val view: View = this.getView(id)
        view.isSelected = isSelected
        return this
    }

    fun setAlpha(@IdRes viewId: Int, value: Float): BaseViewHolder {
        this.getView<View>(viewId).alpha = value
        return this
    }

    fun setTypeface(@IdRes viewId: Int, typeface: Typeface): BaseViewHolder {
        val view: TextView = this.getView(viewId)
        view.typeface = typeface
        view.paintFlags = view.paintFlags.or(128)
        return this
    }

    fun setTypeface(typeface: Typeface, vararg viewIds: Int): BaseViewHolder {
        val var4 = viewIds.size
        for (var5 in 0 until var4) {
            val viewId = viewIds[var5]
            val view: TextView = this.getView(viewId)
            view.typeface = typeface
            view.paintFlags = view.paintFlags.or(128)
        }
        return this
    }

    fun setProgress(@IdRes viewId: Int, progress: Int): BaseViewHolder {
        getView<ProgressBar>(viewId).progress = progress
        return this
    }

    fun setProgress(@IdRes viewId: Int, progress: Int, max: Int): BaseViewHolder {
        val view: ProgressBar = this.getView(viewId)
        view.max = max
        view.progress = progress
        return this
    }

    fun setMax(@IdRes viewId: Int, max: Int): BaseViewHolder {
        val view: ProgressBar = this.getView(viewId)
        view.max = max
        return this
    }

    fun setRating(@IdRes viewId: Int, rating: Float): BaseViewHolder {
        getView<RatingBar>(viewId).rating = rating
        return this
    }

    fun setRating(@IdRes viewId: Int, rating: Float, max: Int): BaseViewHolder {
        val view: RatingBar = getView(viewId)
        view.max = max
        view.rating = rating
        return this
    }

    fun setMiddleLines(@IdRes viewId: Int) {
        val view: TextView = getView(viewId)
        view.paint.flags = 8
        view.paint.isAntiAlias = true
        view.paint.flags = 17
    }

    fun linkify(@IdRes viewId: Int): BaseViewHolder {
        val view: TextView = this.getView(viewId)
        Linkify.addLinks(view, Linkify.ALL)
        return this
    }

    fun setMargins(
            @IdRes viewId: Int,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
    ): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.setMargins(left, top, right, bottom)
            view.layoutParams = params
        }
        return this
    }

    fun setMargins(@IdRes viewId: Int, margin: Int): BaseViewHolder {
        return setMargins(viewId, margin, margin, margin, margin)
    }

    fun setMarginsRelative(
            @IdRes viewId: Int, left: Int,
            top: Int,
            right: Int,
            bottom: Int,
    ): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.marginStart = left
            params.marginEnd = right
            params.topMargin = top
            params.bottomMargin = bottom
            view.layoutParams = params
        }
        return this
    }

    fun setMarginsRelative(@IdRes viewId: Int, margin: Int): BaseViewHolder {
        return setMarginsRelative(viewId, margin, margin, margin, margin)
    }

    fun setMarginEnd(@IdRes viewId: Int, end: Int): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.marginEnd = end
            view.layoutParams = params
        }
        return this
    }

    fun setMarginStart(@IdRes viewId: Int, start: Int): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.marginStart = start
            view.layoutParams = params
        }
        return this
    }

    fun setMarginStartAndEnd(
            @IdRes viewId: Int,
            start: Int,
            end: Int,
    ): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.marginStart = start
            params.marginEnd = end
            view.layoutParams = params
        }
        return this
    }

    fun setMarginBottom(@IdRes viewId: Int, bottom: Int): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.bottomMargin = bottom
            view.layoutParams = params
        }
        return this
    }

    fun setMarginTop(@IdRes viewId: Int, top: Int): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.topMargin = top
            view.layoutParams = params
        }
        return this
    }

    fun setMarginTopAndBottom(
            @IdRes viewId: Int,
            top: Int,
            bottom: Int,
    ): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            params.topMargin = top
            params.bottomMargin = bottom
            view.layoutParams = params
        }
        return this
    }

    fun setViewSize(@IdRes viewId: Int, width: Int, height: Int): BaseViewHolder {
        val view: View = this.getView(viewId)
        val params = view.layoutParams
        params.width = width
        params.height = height
        view.layoutParams = params
        return this
    }

    fun setOnClickListener(
            @IdRes viewId: Int,
            listener: View.OnClickListener,
    ): BaseViewHolder {
        val view: View = this.getView(viewId)
        view.setOnClickListener(listener)
        return this
    }

    fun setTag(@IdRes viewId: Int, tag: Any): BaseViewHolder {
        getView<View>(viewId).tag = tag
        return this
    }

    fun setTag(@IdRes viewId: Int, key: Int, tag: Any): BaseViewHolder {
        getView<View>(viewId).setTag(key, tag)
        return this
    }
}

