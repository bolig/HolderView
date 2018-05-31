package just.blue.holder.adapter

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import just.blue.holder.HolderView

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
class BaseHolder(var contentView: View) {

    private var state = -1
    private val mViewCache =
            SparseArray<View?>()

    fun <T : View> getView(@IdRes resId: Int): T {
        val view = mViewCache.get(resId)

        if (view != null) {
            return view as T
        }
        return contentView.findViewById(resId)
                ?: throw NullPointerException("not find View by id")
    }

    internal fun getParent(): ViewGroup? =
            contentView.parent?.let {
                it as ViewGroup
            } ?: null

    internal fun bindState(state: Int) {
        this.state = state
    }

    fun getState(): Int = state

    internal fun clear() = mViewCache.clear()

}