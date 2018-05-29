package just.blue.holder.adapter

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
class JBLayoutAdapter(state: Int, layoutId: Int) : BaseAdapter<BaseHolder>(state, layoutId) {

    override fun doViewConvert(holder: BaseHolder) {}

    override fun doViewRecycle(holder: BaseHolder) {}
}