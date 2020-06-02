package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MultipleLevelBottomSheetView : ScrollView {
    private val bottomSheetView by lazy { BottomSheetBehavior.from(this) }
    var level: State = State.COLLAPSED
    set(value) {
        if(value == State.HIDDEN){
            visibility = View.GONE
        } else {
            bottomSheetView.state = value.state
            visibility = View.VISIBLE
        }
        field = value
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr)

    enum class State(val state:Int) {
        COLLAPSED(BottomSheetBehavior.STATE_COLLAPSED),
        EXPANDED(BottomSheetBehavior.STATE_EXPANDED),
        HALF_EXPANDED(BottomSheetBehavior.STATE_HALF_EXPANDED),
        HIDDEN(BottomSheetBehavior.STATE_HIDDEN)
    }

    fun initView(){
        bottomSheetView.run {
            halfExpandedRatio = 0.40f
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if(slideOffset>0.25 && slideOffset<0.5){
                        if(state != BottomSheetBehavior.STATE_DRAGGING)
                            bottomSheetView.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }
            })
        }
    }
}