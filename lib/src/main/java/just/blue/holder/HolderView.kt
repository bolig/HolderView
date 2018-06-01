package just.blue.holder

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import just.blue.holder.adapter.BaseAdapter
import just.blue.holder.adapter.BaseHolder
import just.blue.holder.animation.AnimationInternal
import just.blue.holder.animation.doOnEnd
import java.util.*

/**
 * Created by JustBlue on 2018/5/4.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 分状态显示
 */
class HolderView : FrameLayout, HolderInternal {

    companion object {
        const val state_normal = -1
        const val state_empty = 1000001
        const val state_error = 1000002
        const val state_loading = 1000003
        const val state_netMiss = 1000004
        const val state_content = 1000005

        const val NULL_LAYOUT_ID = -1000000
    }

    private val mInflater: LayoutInflater by lazy(LazyThreadSafetyMode.NONE) {
        LayoutInflater.from(context)
    }

    private var mAnimEnable: Boolean
    private var mViewRecycle: Boolean
    private var mClearDetached: Boolean

    /**[state_empty] 显示的View*/
    private var mEmptyLayoutId: Int
    /**[state_error] 显示的View*/
    private var mErrorLayoutId: Int
    /**[state_loading] 显示的View*/
    private var mLoadingLayoutId: Int
    /**[state_netMiss] 显示的View*/
    private var mNoNetworkLayoutId: Int

    private var limitAddView = true
    private var interceptMotionEvent = false
    private var hasAttachedToWindow: Boolean = false

    // 当前正在显示的界面, state_normal和state_content为空
    private var mAdapt: Pair<BaseAdapter<*>, BaseHolder>? = null

    // 状态View的缓存
    private val mAdapterMap by lazy {
        WeakHashMap<BaseAdapter<*>, BaseHolder?>()
    }

    private var mState: Int = state_normal

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context.applicationContext, attrs, defStyleAttr) {

        val a = getContext().obtainStyledAttributes(attrs,
                R.styleable.jb_holder_view, defStyleAttr, 0)

        mViewRecycle = a.getBoolean(R.styleable.jb_holder_view_jb_holder_recycle, false)
        mClearDetached = a.getBoolean(R.styleable.jb_holder_view_jb_holder_clear_detached, false)
        mEmptyLayoutId = a.getResourceId(R.styleable.jb_holder_view_jb_holder_empty, NULL_LAYOUT_ID)
        mErrorLayoutId = a.getResourceId(R.styleable.jb_holder_view_jb_holder_error, NULL_LAYOUT_ID)
        mLoadingLayoutId = a.getResourceId(R.styleable.jb_holder_view_jb_holder_loading, NULL_LAYOUT_ID)
        mNoNetworkLayoutId = a.getResourceId(R.styleable.jb_holder_view_jb_holder_noNet, NULL_LAYOUT_ID)
        mAnimEnable = a.getBoolean(R.styleable.jb_holder_view_jb_holder_animation_enable, true)

        a.recycle()

        // 防止用户设置View背景导致覆盖下面的内容
        this.setBackgroundColor(Color.parseColor("#00000000"))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /**
         * 当状态为state_normal并childCount==0时不显示控件, 宽高都为0, 只要
         * 在正在需要的时候才初始化控件尺寸, 实现原理参考ViewStub
         */
        if (mState == state_normal && childCount == 0) {
            setMeasuredDimension(0, 0)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDestroyView() {
        mAdapterMap.clear()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev) || interceptMotionEvent
    }

    override fun getCurrentState(): Int = mState

    /**
     * 切换不同状态控件, 若在缓存中未找到指定状态的View
     * 通过[BaseAdapter]创建新的状态View, 并切换View
     * 显示
     *
     * @param state
     * @param lRes
     */
    private fun switchLayout(state: Int, lRes: Int, force: Boolean = false) {
        // 非强制时, 状态与当前状态相同时, 无操作
        if (mState == state && !force) return

        var (adapter, holder) = findHAByState(state)

        if (holder == null) {
            val layoutId = getLayoutId(adapter, lRes)

            if (!hasAttachedToWindow) {
                adapter.layoutId = layoutId
                mState = state
                return
            }

            limitAddView = false

            holder = createHolderByAdapter(adapter, layoutId)
            holder.bindState(state)

            limitAddView = true

            tryBindActualChildView(holder)

            // 检查是否被添加到HolderView
            addChildToParent(holder, lRes)
            // 已adapter为key绑定BaseHolder
            mAdapterMap[adapter] = holder
            // 获取是否拦截当前显示界面的触摸事件
            interceptMotionEvent =
                    adapter.interceptMotionEvent()

            // 将验证通过的layoutId绑定到adapter
            adapter.layoutId = layoutId
            // 当创建完成后执行一个View活动(修改View展示效果)
            adapter.onViewConvert(holder)
        }

        mState = state

        onPerformTransition(adapter, holder, !force)
    }

    private fun onPerformTransition(adapter: BaseAdapter<*>, holder: BaseHolder, runAnim: Boolean = true) {
        if (!mAnimEnable || !runAnim) {
            mAdapt.gone()
            mAdapt = adapter to holder
            mAdapt.show()
            return
        }

        if (mAdapt != null) {
            val (la, lh) = mAdapt!!

            mAdapt = adapter to holder

            val (na, nh) = mAdapt!!

            var exitAnim = la.getHolderTransformAnim(lh, this, false)
            var enterAnim = na.getHolderTransformAnim(nh, this, true)

            lh.contentView.setVisible(false)
            nh.contentView.setVisible(true)

            exitAnim.start()
            enterAnim.start()
        } else {
            mAdapt = adapter to holder
            val (na, nh) = mAdapt!!

//            var enterAnim = na.getHolderTransformAnim(
//                    nh, this, true)
            var enterAnim = na.getEnterHolderAnim(holder, this)

            nh.contentView.setVisible(true)
            enterAnim.start()
            enterAnim.doOnEnd {

            }
        }
    }

    private fun showContentTransition() {
        if (!mAnimEnable) {
            mAdapt.gone()
            mAdapt = null
            return
        }

        if (mAdapt != null) {
            val (adapter, holder) = mAdapt!!
            mAdapt = null

            val exitAnim = adapter.getExitHolderAnim(holder, this)

            holder.contentView.setVisible(false)
            exitAnim.start()
        }
    }

    /**
     * 尝试判断[LayoutInflater.inflate]中是否指定attachToRoot=true, 如为true
     * 这会使inflate返回inflate中指定的root View(这里即[HolderView]), 从而导致
     * 无法获取到真实的child View...
     *
     * (注: 此方法旨在解决[LayoutInflater.inflate]指定attachToRoot=true的问题)
     *
     * @param holder
     */
    private fun tryBindActualChildView(holder: BaseHolder) {
        var view = holder.contentView

        if (view !== this) return

        val childViews = tryGetAllChildView(true)

        for (child in childViews) {
            if (!checkViewInAdapter(child)) {
                if (view === this) {
                    view = child
                    break
                }
            }
        }

        if (view !== this) {
            holder.contentView = view
            return
        }

        throw IllegalAccessException("Cannot" +
                " confirm the actual Child View, Ensure that " +
                "you are not passing 'true' to the attachToRoot " +
                "parameter of LayoutInflater.inflate(..., boolean attachToRoot)")
    }

    /**
     * 检查child view是否已经绑定到[BaseHolder]中
     *
     * @param child
     * @return
     */
    private fun checkViewInAdapter(child: View): Boolean {
        val holders = mAdapterMap.values
        for (holder in holders) {
            holder?.let {
                val cv = it.contentView
                if (cv === child) {
                    return true
                }
            }
        }
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!hasAttachedToWindow) {
            hasAttachedToWindow = true

            if (mState != state_normal) {
                switchLayout(mState, NULL_LAYOUT_ID, true)
            }
        }
    }

    override fun onDetachedFromWindow() {
        if (mClearDetached) {
            onDestroyView()
        }

        super.onDetachedFromWindow()
    }

    /**
     * 检查子布局是否被添加到当前布局中, 如未添加通过默认规则添加
     * 规则为:[lRes]==[NULL_LAYOUT_ID], 通过默认LayoutParams添加
     * 如[lRes]指定布局, 则取出布局的根目录LayoutParams添加
     *
     * @param lRes
     * @param holder
     */
    private fun addChildToParent(holder: BaseHolder, lRes: Int) {
        var child = holder.contentView
        val parent = holder.getParent()

        if (parent == null) {
            var lp = child.layoutParams

            unlockAndAddView {
                if (lp != null) {
                    addView(child)
                } else {
                    addView(child, ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT))
                }
            }
        } else {
            if (parent.id !== id) {
                throw IllegalArgumentException(
                        "Child View, The wrong binding relationship.")
            }
        }
    }

    private inline fun unlockAndAddView(block: () -> Unit) {
        limitAddView = false

        block()

        limitAddView = true
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (limitAddView)
            throw IllegalAccessException("HolderView. " +
                    "Active addView is not currently supported")

        super.addView(child, index, params)
    }

    override fun showByState(state: Int) {
        when (state) {
            state_empty -> switchLayout(state_empty, mEmptyLayoutId)
            state_error -> switchLayout(state_error, mErrorLayoutId)
            state_loading -> switchLayout(state_loading, mLoadingLayoutId)
            state_netMiss -> switchLayout(state_netMiss, mNoNetworkLayoutId)
            state_content -> showContent()
            state_normal -> {
                mAdapt.gone()
                mAdapt = null

                mState = state
                interceptMotionEvent = false
            }
            else -> {
                switchLayout(state, NULL_LAYOUT_ID)
            }
        }
    }

    override fun showEmpty() = switchLayout(state_empty, mEmptyLayoutId)

    override fun showError() = switchLayout(state_error, mErrorLayoutId)

    override fun showLoading() = switchLayout(state_loading, mLoadingLayoutId)

    override fun showNetMiss() = switchLayout(state_netMiss, mNoNetworkLayoutId)

    override fun showContent() {
        mState = state_content
        interceptMotionEvent = false

        showContentTransition()
    }

    override fun addAdapter(adapter: BaseAdapter<*>) {
        removeAdapter(adapter)

        mAdapterMap[adapter] = null

        if (adapter.checkState(mState)) {
            notifyViewCreate()
        }
    }

    override fun addAdapter(vararg adapters: BaseAdapter<*>) {
        for (adapter in adapters) {
            addAdapter(adapter)
        }
    }

    override fun removeAdapter(adapter: BaseAdapter<*>) {
        mAdapterMap.remove(adapter)?.clear()
    }

    private fun findHAByState(state: Int): Pair<BaseAdapter<*>, BaseHolder?> {
        val entries = mAdapterMap.entries

        for (entry in entries) {
            if (entry.key.checkState(state)) {
                return entry.key to entry.value
            }
        }

        throw UnknownStateException()
    }

    override fun notifyViewChange(state: Int) {
        if (state == state_normal
                || state == state_content) {
            return
        }

        val (adapter, holder) = findHAByState(state)

        if (holder != null) {
            adapter.onViewConvert(holder)
        }
    }

    override fun notifyViewCreate(state: Int) {
        if (state == state_normal
                || state == state_content) {
            return
        }

        val (adapter, _) = findHAByState(state)

        val holder = createHolderByAdapter(adapter)

        mAdapterMap[adapter] = holder

        adapter.onViewConvert(holder)
    }

    private fun getLayoutId(adapter: BaseAdapter<*>, @LayoutRes layoutRes: Int) =
            if (adapter.layoutId == NULL_LAYOUT_ID) layoutRes else adapter.layoutId

    private fun createHolderByAdapter(adapter: BaseAdapter<*>,
                                      @LayoutRes layoutRes: Int = NULL_LAYOUT_ID): BaseHolder {
        return adapter.onCreateHolder(layoutRes, this, mInflater)
    }

}