package just.blue.holder.adapter

import android.animation.Animator
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import just.blue.holder.*
import just.blue.holder.animation.*
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

    internal fun getHolderTransformAnim(holder: BaseHolder, root: ViewGroup, isEnter: Boolean): AnimAction {
        val transFormAnim = if (isEnter)
            createViewEnterAnim(holder as T, root)
                    ?: AnimationUtils.loadAnimation(root.context, android.R.anim.fade_in)
        else
            createViewExitAnim(holder as T, root)
                    ?: AnimationUtils.loadAnimation(root.context, android.R.anim.fade_out)

        return wrapInternalAnimation(transFormAnim,
                root, holder.contentView, isEnter)
    }

    internal fun getExitHolderAnim(holder: BaseHolder, root: ViewGroup): AnimAction {
        val contentAnim = createEnterContentAnim(holder as T, root)
                ?: AnimationUtils.loadAnimation(root.context, R.anim.jb_holder_show_content)

        return wrapInternalAnimation(contentAnim,
                root, holder.contentView, false)
    }

    internal fun getEnterHolderAnim(holder: BaseHolder, root: ViewGroup): AnimAction {
        val contentAnim = createEnterContentAnim(holder as T, root)
                ?: AnimationUtils.loadAnimation(root.context, R.anim.jb_holder_from_content)

        return wrapInternalAnimation(contentAnim,
                root, holder.contentView, false)
    }

    private fun wrapInternalAnimation(anim: Any, root: ViewGroup, target: View, isEnter: Boolean): AnimAction {
        if (isKitKat() && anim is Transition) {
            return createTransitionAction(
                    root, target, anim, isEnter)
        }

        return when (anim) {
            is Animator -> createAnimatorAction(anim)
            is Animation -> createAnimationAction(target, anim)

            else -> throw IllegalArgumentException("only support " +
                    "(Animator, Animation, Transition) anim ")
        }
    }

    /**
     * 创建一个由当前[BaseHolder.contentView] -> 显示主界面的动画
     * (即调用[HolderView.showContent], 控制所有holder view不可见,
     * 显示HolderView覆盖的内容)
     *
     *
     * @param holder
     * @param root 只能是[HolderView]
     * @return 返回一个动画执行类(可选: [Animation]、[Animator]、[Transition], 以及他们的子类型)
     *         返回null时,
     */
    protected fun createEnterContentAnim(holder: T, root: ViewGroup): Any? = null

    protected fun createViewEnterAnim(holder: T, root: ViewGroup): Any? = null

    protected fun createViewExitAnim(holder: T, root: ViewGroup): Any? = null

    internal fun onViewConvert(holder: BaseHolder) {
        doViewConvert(holder as T)
    }

    internal fun onViewRecycle(holder: BaseHolder) {
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
