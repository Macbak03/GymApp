package com.example.gymapp.animation

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup

class Animations {

    private val animator = ValueAnimator()

    fun translateY(currentTranslationY: Float, targetTranslationY: Float, target: View)
    {
        animator.setFloatValues(currentTranslationY, targetTranslationY)
        animator.duration = 300
        animator.start()

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            target.translationY = value
        }

        animator.start()
    }

    fun translateX(currentTranslationX: Float, targetTranslationX: Float, target: View)
    {
        animator.setFloatValues(currentTranslationX, targetTranslationX)
        animator.duration = 300
        animator.start()

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            target.translationX = value
        }

        animator.start()
    }

}