package just.blue.holder.animation

import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionListenerAdapter
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import just.blue.holder.isM
import just.blue.holder.isO
import just.blue.holder.limit
import just.blue.holder.setVisible

/**
 * Created by JustBlue on 2018/6/1.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class TransitionInternal(private val root: ViewGroup, private val target: View, val anim: Transition, val isEnter: Boolean) : AnimAction {

    override var duration: Long
        get() = anim.duration
        set(value) {
            anim.duration = value.limit(
                    min = MIN_DURATION,
                    max = MAX_DURATION
            )
        }

    private var isRunning = false

    override val running: Boolean
        get() = isRunning

    override val startDelay: Long
        get() = anim.startDelay

    override fun cancel() {
        if (isM()) {
            TransitionManager.endTransitions(root)
        } else {
            // TODO: 版本适配问题, 待适配
        }
    }

    override fun start() {
        TransitionManager.beginDelayedTransition(root, anim)

        target.setVisible(isEnter)

        /**
         * 简单适配一下8.0以下无法监听动画执行过程的问题
         */
        if (!isO()) {
            isRunning = true

            Handler().postDelayed({
                isRunning = false
            }, startDelay + duration)
        }
    }

    override fun setListener(listener: AnimListener) {
        if (isO()) {
            anim.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionStart(transition: Transition?) {
                    listener.onStart()
                }

                override fun onTransitionEnd(transition: Transition?) {
                    isRunning = false
                    anim.removeListener(this)
                    listener.onComplete()
                }

                override fun onTransitionCancel(transition: Transition?) {
                    isRunning = false
                    anim.removeListener(this)
                    listener.onComplete()
                }
            })
        } else {
            // TODO: 版本适配问题, 待适配
        }
    }

}