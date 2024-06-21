package com.lifthub.lifthubandroid.animation

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation

class Animations: Animation(){

    private val animator = ValueAnimator()

    fun translateY(currentTranslationY: Float, targetTranslationY: Float, target: View, duration: Long)
    {
        animator.setFloatValues(currentTranslationY, targetTranslationY)
        animator.duration = duration

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            target.translationY = value
        }

        animator.start()
    }

    fun translateX(currentTranslationX: Float, targetTranslationX: Float, target: View, duration: Long)
    {
        animator.setFloatValues(currentTranslationX, targetTranslationX)
        animator.duration = duration

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            target.translationX = value
        }

        animator.start()
    }

    fun rotate(currentRotation: Float, targetRotation: Float, target: View, duration: Long): ValueAnimator{
        animator.setFloatValues(currentRotation, targetRotation)
        animator.duration = duration

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            target.rotation = value
        }

        return animator
    }

    fun moveItemsY(currentTranslationY: Int, targetTranslationY: Int, target: View, duration: Long){

        val animator = ValueAnimator.ofInt(currentTranslationY, targetTranslationY)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            target.layoutParams.height = if (value == 0) ViewGroup.LayoutParams.WRAP_CONTENT else value
            target.requestLayout()
        }
        animator.duration = duration
        animator.start()
    }
}