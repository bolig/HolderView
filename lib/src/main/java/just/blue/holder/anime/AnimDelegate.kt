package just.blue.holder.anime

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.Transition
import android.transition.TransitionListenerAdapter
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation

/**
 * Created by JustBlue on 2018/5/31.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
internal object AnimDelegate {

    fun createAnimationAction(view: View, anim: Animation): AnimAction {
        view.animation = anim

        return object : AnimAction {
            override fun setListener(l: AnimListener) {
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        l.onStart()
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        anim.setAnimationListener(null)
                        l.onComplete()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        animation?.cancel()
                    }
                })
            }

            override fun cancel() {
                anim.cancel()
            }

            override fun start() {
                anim.start()
            }

            override fun end() {
                anim.cancel()
            }
        }
    }

    fun createAnimatorAction(anim: Animator): AnimAction =
            object : AnimAction {

                override fun setListener(l: AnimListener) {
                    anim.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationCancel(animation: Animator?) {
                            anim.removeListener(this)
                            animation?.end()
                            l.onComplete()
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            anim.removeListener(this)
                            animation?.end()
                            l.onComplete()
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                            // 默认取消重复动画
                            animation?.end()
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            anim.removeListener(this)
                            l.onStart()
                        }
                    })
                }

                override fun cancel() {
                    anim.cancel()
                }

                override fun start() {
                    anim.start()
                }

                override fun end() {
                    anim.end()
                }
            }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun createTransitionAction(root: ViewGroup, anim: Transition): AnimAction =
            object : AnimAction {
                override fun setListener(l: AnimListener) {
                    anim.addListener(@TargetApi(Build.VERSION_CODES.O)
                    object : TransitionListenerAdapter() {
                        override fun onTransitionStart(transition: Transition?) {
                            l.onStart()
                        }

                        override fun onTransitionEnd(transition: Transition?) {
                            anim.removeListener(this)
                            l.onComplete()
                        }

                        override fun onTransitionCancel(transition: Transition?) {
                            anim.removeListener(this)
                            l.onComplete()
                        }

                    })
                }

                override fun cancel() {
                    TransitionManager.beginDelayedTransition(root, anim)
                }

                override fun start() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        TransitionManager.endTransitions(root)
                    }
                }

                override fun end() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        TransitionManager.endTransitions(root)
                    }
                }
            }

}


