package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class ScrollunableViewPager(context: Context, attrs: AttributeSet?): ViewPager(context, attrs) {

    constructor(context: Context) : this(context, null)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean = true

}