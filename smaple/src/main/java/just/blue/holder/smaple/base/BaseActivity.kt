package just.blue.holder.smaple.base

import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import just.blue.holder.HolderAction
import just.blue.holder.HolderDelegate
import just.blue.holder.HolderView
import just.blue.holder.adapter.BaseAdapter
import just.blue.holder.adapter.BaseHolder
import just.blue.holder.smaple.R
import just.blue.holder.smaple.adapter.RetryAdapter

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
abstract class BaseActivity : AppCompatActivity(), HolderAction {

    protected open var mHolderView: HolderView?
            by HolderDelegate(R.id.hv_place, onChange = {
                it.addAdapter(*mHoldAdapters)
            })

    private val mHoldAdapters: Array<out BaseAdapter<BaseHolder>> by lazy {
        val emptyAdapter = createAdapter(HolderView.state_empty)
        val errorAdapter = createAdapter(HolderView.state_error)
        val netMissAdapter = createAdapter(HolderView.state_netMiss)

        return@lazy arrayOf(
                emptyAdapter,
                errorAdapter,
                netMissAdapter,
                HolderDelegate.create(
                        HolderView.state_loading,
                        R.layout.layout_loading)
        )
    }

    private fun createAdapter(state: Int): RetryAdapter = object : RetryAdapter(state) {
        override fun onRetryEvent() {
            loadData()
        }
    }

    protected open var mFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutId = getLayoutId()

        setContentView(layoutId)

        initHolderView()

        initView(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        if (mFirstLoad) {
            mFirstLoad = false

            loadData()
        }
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    open fun loadData() {}

    protected open fun initHolderView() {

    }

    override fun showByState(state: Int) {
        mHolderView?.showByState(state)
    }

    override fun showContent() {
        mHolderView?.showContent()
    }

    override fun showEmpty() {
        mHolderView?.showEmpty()
    }

    override fun showError() {
        mHolderView?.showError()
    }

    override fun showLoading() {
        mHolderView?.showLoading()
    }

    override fun showNetMiss() {
        mHolderView?.showNetMiss()
    }

}