package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MultipleLevelBottomSheetView : LinearLayout {
    private val bottomSheetView by lazy { BottomSheetBehavior.from(this) }
    var level: STATE = STATE.COLLAPSED
    set(value) {
        bottomSheetView.state = value.state
        field = value
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr)

    enum class STATE(val state:Int) {
        COLLAPSED(BottomSheetBehavior.STATE_COLLAPSED), EXPANDED(BottomSheetBehavior.STATE_EXPANDED), HALF_EXPANDED(BottomSheetBehavior.STATE_HALF_EXPANDED)
    }

    fun initView(){
        bottomSheetView.run {
            halfExpandedRatio = 0.35f
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if(slideOffset>0.25 && slideOffset<0.4)
                        bottomSheetView.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }
            })
        }
    }
}