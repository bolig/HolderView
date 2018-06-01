package just.blue.holder.animation

import android.animation.Animator
import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation


internal const val MAX_DURATION = 600L
internal const val MIN_DURATION = 300L

internal fun createAnimationAction(view: View, anim: Animation): AnimAction {
    view.animation = anim

    return AnimationInternal(anim)
}

internal fun createAnimatorAction(anim: Animator): AnimAction =
        AnimatorInternal(anim)

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun createTransitionAction(root: ViewGroup, target: View, anim: Transition, isEnter: Boolean): AnimAction =
        TransitionInternal(root, target, anim, isEnter)

internal inline fun AnimAction.doOnEnd(crossinline block: () -> Unit) {
    this.setListener(object : AnimListener {
        override fun onStart() {

        }

        override fun onComplete() {
            block()
        }
    })
}