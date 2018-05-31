package just.blue.holder.anime

import android.animation.Animator
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.animation.Animation

/**
 * Created by JustBlue on 2018/5/31.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
abstract class HolderAnimator {

    private val callbacks by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<Animator.AnimatorListener>()
    }

    fun start() {

    }

    fun cancel() {

    }

}