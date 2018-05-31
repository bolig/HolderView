package just.blue.holder

import android.os.Build
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import just.blue.holder.adapter.BaseAdapter
import just.blue.holder.adapter.BaseHolder
import java.lang.ref.WeakReference

internal inline fun <T> SparseArray<T>.getAndRemove(key: Int): T? {
    val value = this[key]
    value?.let { remove(key) }

    return value
}

internal inline fun Pair<BaseAdapter<*>, BaseHolder>?.show() {
    if (this != null) {
        second.contentView.visibility = View.VISIBLE
    }
}

internal inline fun Pair<BaseAdapter<*>, BaseHolder>?.gone() {
    if (this != null) {
        second.contentView.visibility = View.GONE
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

internal inline fun ViewGroup.tryGetAllChildView(reverse: Boolean = false): Array<View> {
    val indexEnd = childCount - 1

    return Array(childCount) {
        val index = if (reverse) {
            indexEnd - it
        } else it

        return@Array getChildAt(index)
    }
}

internal inline fun isKitKat(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
