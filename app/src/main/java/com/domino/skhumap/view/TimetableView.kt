package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.widget.*

class TimetableView(@get:JvmName("getContext_")val context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context):this(context, null)
    constructor(context:Context, attrs:AttributeSet?):this(context, attrs, 0)
}