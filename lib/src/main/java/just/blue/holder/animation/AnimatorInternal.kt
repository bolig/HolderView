package just.blue.holder.animation

import android.animation.Animator
import just.blue.holder.limit

/**
 * Created by JustBlue on 2018/6/1.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
class AnimatorInternal(val anim: Animator) : AnimAction {

    override var duration: Long
        get() = anim.duration
        set(value) {
            anim.duration = value.limit(
                    min = MIN_DURATION,
                    max = MAX_DURATION
            )
        }

    override val running: Boolean
        get() = anim.isRunning

    override val startDelay: Long
        get() = anim.startDelay

    override fun start() {
        anim.start()
    }

    override fun cancel() {
        anim.end()
    }

    override fun setListener(listener: AnimListener) {
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animation: Animator?) {
                anim.removeListener(this)
                animation?.end()
                listener.onComplete()
            }

            override fun onAnimationEnd(animation: Animator?) {
                anim.removeListener(this)
                animation?.end()
                listener.onComplete()
            }

            override fun onAnimationRepeat(animation: Animator?) {
                // 默认取消重复动画
                animation?.end()
            }

            override fun onAnimationStart(animation: Animator?) {
                anim.removeListener(this)
                listener.onStart()
            }
        })
    }
}