package just.blue.holder.adapter

import android.animation.Animator
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import just.blue.holder.HolderView
import just.blue.holder.anime.AnimAction
import just.blue.holder.anime.AnimDelegate
import just.blue.holder.isKitKat
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
    private var mViewHolder: WeakReference<BaseHolder>? = null

    fun checkState(state: Int): Boolean = state === this.state

    fun setLayout(layoutId: Int) {
        this.layoutId = layoutId

        notifyCreateView()
    }

    fun notifyCreateView() {
        mParentView.runNonNull {
            if (it is HolderView) {
                it.notifyViewChange(state)
            }
        }
    }

    fun notifyChangeView() {
        mViewHolder.runNonNull {
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

        mViewHolder = WeakReference(holder)
        mParentView = WeakReference(root as HolderView)

        return holder
    }

    protected open fun doCreateHolder(layoutId: Int, root: ViewGroup, inflater: LayoutInflater): T? = null

    protected open fun doCreateView(layoutId: Int,
                                    root: ViewGroup,
                                    inflater: LayoutInflater): View {

        if (!layoutId.validLayout()) {
            throw IllegalArgumentException("when not specified layoutId, ")
        }

        return inflater.inflate(layoutId, root, false)
    }

    internal fun getViewAnim(holder: BaseHolder, root: ViewGroup, isEnter: Boolean): AnimAction {
        val vea = if (isEnter)
            getViewEnterAnim(holder as T, root)
                    ?: AnimationUtils.loadAnimation(root.context, android.R.anim.fade_in)
        else
            getViewExitAnim(holder as T, root)
                    ?: AnimationUtils.loadAnimation(root.context, android.R.anim.fade_out)

        if (isKitKat() && vea is Transition) {
            return AnimDelegate.createTransitionAction(root, vea)
        }

        return when (vea) {
            is Animator -> AnimDelegate.createAnimatorAction(vea)
            is Animation -> AnimDelegate.createAnimationAction(holder.contentView, vea)

            else -> throw IllegalArgumentException("only support " +
                    "(Animator, Animation, Transition) anim ")
        }
    }

    protected fun getViewEnterAnim(holder: T, root: ViewGroup): Any? = null

    protected fun getViewExitAnim(holder: T, root: ViewGroup): Any? = null

    internal fun onViewConvert(holder: BaseHolder) {
        doViewConvert(holder as T)
    }

    internal fun onViewRecycle(holder: BaseHolder) {
        // TODO: 释放资源

        doViewRecycle(holder as T)
    }

    internal fun interceptMotionEvent() = true

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
