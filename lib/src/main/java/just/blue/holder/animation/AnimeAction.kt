package just.blue.holder.animation

/**
 * Created by JustBlue on 2018/5/31.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
internal interface AnimAction {

    var duration: Long

    val running: Boolean

    val startDelay: Long

//    fun end(anim: Anim)

    fun start()

    fun cancel()

    fun setListener(listener: AnimListener)

}