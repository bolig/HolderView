package just.blue.holder.anime

/**
 * Created by JustBlue on 2018/5/31.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */

interface AnimAction {

    fun end()

    fun start()

    fun cancel()

    fun setListener(l: AnimListener)
}