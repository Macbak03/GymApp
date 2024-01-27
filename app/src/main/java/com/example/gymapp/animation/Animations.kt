package com.example.gymapp.animation

import android.animation.ValueAnimator
import android.view.View

class Animations{

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
}