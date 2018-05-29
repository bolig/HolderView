package just.blue.holder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
class JBViewAdapter(state: Int, val view: View) : BaseAdapter<BaseHolder>(state) {

    override fun doCreateView(layoutId: Int, root: ViewGroup, inflater: LayoutInflater): View = view

    override fun doViewConvert(holder: BaseHolder) {}

    override fun doViewRecycle(holder: BaseHolder) {}

}
