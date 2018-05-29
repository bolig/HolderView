package just.blue.holder

import android.support.annotation.LayoutRes
import android.util.SparseArray
import android.view.View
import just.blue.holder.adapter.BaseHolder
import java.lang.ref.WeakReference

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */

internal inline fun <T> SparseArray<T>.getAndRemove(key: Int): T? {
    val value = this[key]
    value?.let { remove(key) }

    return value
}

internal inline fun BaseHolder?.show() {
    if (this != null) {
        contentView.visibility = View.VISIBLE
    }
}

internal inline fun BaseHolder?.gone() {
    if (this != null) {
        contentView.visibility = View.GONE
    }
}

internal inline fun Int.validLayout(): Boolean {
    return this != HolderView.NULL_LAYOUT_ID
}

internal inline fun <T> WeakReference<T>?.runNonNull(block: (T) -> Unit) {
    if (this != null) {
        get()?.let(block)
    }
}