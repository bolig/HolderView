package just.blue.holder.smaple

import android.view.View
import java.util.*

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */

internal val rn = Random()

internal val sysTime
    get() = System.currentTimeMillis()

internal var lastClickTime = sysTime

internal inline infix fun View.doOnClick(crossinline block: (View) -> Unit) {
    this.setOnClickListener {
        val time = sysTime
        if (time - lastClickTime < 300)
            return@setOnClickListener

        lastClickTime = time
        block(it)
    }
}