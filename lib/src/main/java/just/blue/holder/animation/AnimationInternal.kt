package just.blue.holder.animation

import android.view.animation.Animation
import android.view.animation.AnimationSet
import just.blue.holder.limit

/**
 * Created by JustBlue on 2018/6/1.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
class AnimationInternal(var anim: Animation) : AnimAction {

    init {
        if (anim !is AnimationSet) {
            val set = AnimationSet(true)

            set.addAnimation(anim)

            anim = set
        }

        initAnim(set = anim as AnimationSet)
    }

    private fun initAnim(set: AnimationSet) {
        set.isFillEnabled = true
        set.fillAfter = false
        set.fillBefore = true

        duration = 500
    }

    override var duration: Long
        get() = anim.duration
        set(value) {
            anim.duration = value.limit(
                    min = MIN_DURATION,
                    max = MAX_DURATION
            )
        }

    override val running: Boolean
        get() = anim.hasStarted() && !anim.hasEnded()

    override val startDelay: Long
        get() = anim.startOffset

    override fun start() {
        if (!anim.hasStarted()) {
            anim.start()
        }
    }

    override fun cancel() {
        if (!anim.hasEnded()) {
            anim.cancel()
        }
    }

    override fun setListener(listener: AnimListener) {
        anim.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {
                listener.onStart()
            }

            override fun onAnimationEnd(animation: Animation?) {
                anim.setAnimationListener(null)
                listener.onComplete()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 默认不支持重复动画
                animation?.cancel()
            }
        })
    }
}