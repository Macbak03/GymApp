package com.pl.Maciejbak.layout

import android.content.Context
import android.util.AttributeSet

class DynamicSizeSpinner : androidx.appcompat.widget.AppCompatSpinner {

    private var inOnMeasure = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        inOnMeasure = true
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        inOnMeasure = false
    }
}