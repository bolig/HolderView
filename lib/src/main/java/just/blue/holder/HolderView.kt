package just.blue.holder

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.util.Xml
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import just.blue.holder.adapter.BaseAdapter
import just.blue.holder.adapter.BaseHolder
import java.util.*

/**
 * Created by JustBlue on 2018/5/4.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 分状态显示
 */
class HolderView : FrameLayout, IHolderView, ViewTreeObserver.OnGlobalLayoutListener {

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

    private var interceptMotionEvent = false
    private var hasAttachedToWindow: Boolean = false

    // 当前正在显示的界面, state_normal和state_content为空
    private var mShowHolder: BaseHolder? = null

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

//            if (!hasAttachedToWindow) {
//                adapter.layoutId = layoutId
//                mState = state
//                return
//            }

            holder = createHolderByAdapter(adapter, layoutId)

            checkHolderViewValid(holder)

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

        mShowHolder.gone()
        mShowHolder = holder
        mShowHolder.show()
    }

    private fun checkHolderViewValid(holder: BaseHolder) {
        val view = holder.contentView

        if (view == this) {

            for (entry in mAdapterMap) {

            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

//        if (!hasAttachedToWindow) {
//            hasAttachedToWindow = true
//
//            if (mState != state_normal) {
//                switchLayout(mState, NULL_LAYOUT_ID, true)
//            }
//        }
    }

    override fun onDetachedFromWindow() {

//        rmOnParentLayout(this)

        if (mClearDetached) {
            onDestroyView()
        }

        super.onDetachedFromWindow()
    }

    override fun onGlobalLayout() {
//        val p = this.parent
//        if (p is ViewGroup) {
//            val count = p.childCount
//
//            if (mLastParentChildCount != count) {
//                mLastParentChildCount = count
//
//                bringToFront()
//            }
//        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // TODO: 添加
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
//        holder.contentView == this.if
        val parent = holder.getParent()

//        if (parent == null) {
//            addView(child, ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT))
//        } else {
//            if (parent.id !== id && child.id !== id) {
//                throw IllegalArgumentException(
//                        "Child View, The wrong binding relationship.")
//            }
//        }
    }

    override fun showByState(state: Int) {
        when (state) {
            state_empty -> switchLayout(state_empty, mEmptyLayoutId)
            state_error -> switchLayout(state_error, mErrorLayoutId)
            state_loading -> switchLayout(state_loading, mLoadingLayoutId)
            state_netMiss -> switchLayout(state_netMiss, mNoNetworkLayoutId)
            state_content -> showContent()
            state_normal -> {
                mShowHolder.gone()
                mShowHolder = null

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
        mShowHolder.gone()
        mShowHolder = null

        mState = state_content
        interceptMotionEvent = false
    }

    override fun addAdapter(adapter: BaseAdapter<*>) {
        mAdapterMap[adapter] = null
    }

    override fun addAdapter(vararg adapters: BaseAdapter<*>) {
        for (adapter in adapters) {
            mAdapterMap[adapter] = null
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