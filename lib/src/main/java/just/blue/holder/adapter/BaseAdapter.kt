package just.blue.holder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import just.blue.holder.HolderView
import just.blue.holder.runNonNull
import just.blue.holder.validLayout
import java.lang.ref.WeakReference

/**
 * Created by JustBlue on 2018/5/18.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
abstract class BaseAdapter<T : BaseHolder>(val state: Int, var layoutId: Int = HolderView.NULL_LAYOUT_ID) {

    private var mParentView: WeakReference<HolderView>? = null
    private var mContentView: WeakReference<BaseHolder>? = null

    fun checkState(state: Int): Boolean = state === this.state

    fun setLayout(layoutId: Int) {
        this.layoutId = layoutId
    }

    fun notifyViewCreate() {
        mParentView.runNonNull {
            if (it is HolderView) {
                it.notifyViewChange(state)
            }
        }
    }

    fun notifyViewConvert() {
        mContentView.runNonNull {
            doViewConvert(it as T)
        }
    }

    internal fun onCreateHolder(layoutId: Int, root: ViewGroup, inflater: LayoutInflater): BaseHolder {
        var holder: BaseHolder? = doCreateHolder(
                layoutId, root, inflater)

        if (holder == null) {
            var view = doCreateView(
                    layoutId, root, inflater)

            holder = BaseHolder(view)
        }

        mParentView = WeakReference(root as HolderView)
        mContentView = WeakReference(holder)

        return holder
    }

    protected open fun doCreateHolder(layoutId: Int, root: ViewGroup, inflater: LayoutInflater): T? = null

    protected open fun doCreateView(layoutId: Int,
                                    root: ViewGroup,
                                    inflater: LayoutInflater): View {

        if (!layoutId.validLayout()) {
            throw IllegalArgumentException("when not specified layoutId, ")
        }

        return inflater.inflate(layoutId, root)
    }

    internal fun onViewConvert(holder: BaseHolder) {
        doViewConvert(holder as T)
    }

    internal fun onViewRecycle(holder: BaseHolder) {
        // TODO: 释放资源

        doViewRecycle(holder as T)
    }

    abstract fun doViewConvert(holder: T)

    abstract fun doViewRecycle(holder: T)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseAdapter<*>

        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        return state
    }

}
