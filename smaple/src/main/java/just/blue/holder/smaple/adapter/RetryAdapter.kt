package just.blue.holder.smaple.adapter

import android.support.v7.widget.RecyclerView
import android.widget.TextView
import just.blue.holder.HolderView
import just.blue.holder.adapter.BaseAdapter
import just.blue.holder.adapter.BaseHolder
import just.blue.holder.smaple.R

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
abstract class RetryAdapter(state: Int) : BaseAdapter<BaseHolder>(state) {

    override fun doViewConvert(holder: BaseHolder) {
        var btnRetry = holder.getView<TextView>(R.id.btn_retry)

        btnRetry.setOnClickListener {
            onRetryEvent()
        }
    }

    abstract fun onRetryEvent()

    override fun doViewRecycle(holder: BaseHolder) {
//        RecyclerView.ItemAnimator
    }
}
