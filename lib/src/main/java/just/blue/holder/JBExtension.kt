package just.blue.holder

import android.os.Build
import android.util.SparseArray
import android.view.View
import android.view.ViewTreeObserver
import just.blue.holder.adapter.BaseHolder
import java.lang.ref.WeakReference

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

internal inline fun <T> WeakReference<T>?.runNonNull(block: (T) -> Unit) =
        this?.get()?.let(block)

internal inline fun View.doOnParentLayout(l: ViewTreeObserver.OnGlobalLayoutListener) {
    val p = this.parent
    if (p != null && p is View) {
        p.viewTreeObserver.addOnGlobalLayoutListener(l)
    }
}

internal inline fun View.rmOnParentLayout(l: ViewTreeObserver.OnGlobalLayoutListener) {
    val p = this.parent
    if (p != null && p is View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            p.viewTreeObserver.removeOnGlobalLayoutListener(l)
        } else {
            p.viewTreeObserver.removeGlobalOnLayoutListener(l)
        }
    }
}