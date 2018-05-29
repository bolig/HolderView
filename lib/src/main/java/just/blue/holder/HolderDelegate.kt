package just.blue.holder

import android.app.Activity
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.view.View
import android.view.ViewGroup
import just.blue.holder.adapter.BaseAdapter
import just.blue.holder.adapter.JBLayoutAdapter
import just.blue.holder.adapter.JBViewAdapter
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */

class HolderDelegate(@IdRes private val layoutId: Int = HolderView.NULL_LAYOUT_ID,
                     val onChange: (HolderView) -> Unit = {}) {

    private var delegate: HolderView? = null

    operator fun getValue(act: Activity, property: KProperty<*>): HolderView? {
        if (delegate == null) {
            delegate = if (layoutId.validLayout()) {
                findHolderById(act, layoutId)
            } else {
                findFromActivity(act)
            }

            if (delegate != null) {
                onChange(delegate!!)
            }
        }

        return delegate
    }

    operator fun setValue(act: Activity, property: KProperty<*>, view: HolderView?) {
        if (delegate != view) {
            delegate = view

            if (delegate != null) {
                onChange(delegate!!)
            }
        }
    }

    companion object {

        fun create(state: Int, view: View) = JBViewAdapter(state, view)

        fun create(state: Int, layoutId: Int) = JBLayoutAdapter(state, layoutId)

        /**
         * 通过指定id查找[HolderView]
         */
        fun findHolderById(@NonNull act: Activity, @IdRes resId: Int): HolderView? {
            return act.findViewById(resId)
        }

        fun findFromActivity(@NonNull act: Activity): HolderView? {
            var view = act.window.decorView

            val holderView = findHolderInstance(view)

            if (holderView != null) {
                return holderView
            }

            return null
        }

        private fun findHolderInstance(vv: View): HolderView? {
            if (vv is HolderView) {
                return vv
            }

            return when (vv) {
                is ViewGroup -> {
                    for (i in 0 until vv.childCount) {
                        val holderView = findHolderInstance(vv.getChildAt(i))

                        if (holderView != null) {
                            return holderView
                        }
                    }
                    return null
                }
                else -> return null
            }
        }
    }
}