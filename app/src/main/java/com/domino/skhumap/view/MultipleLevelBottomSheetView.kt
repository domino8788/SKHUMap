package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.reflect.Field


class MultipleLevelBottomSheetView(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
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

    private val positionField: Field =
        ViewPager.LayoutParams::class.java.getDeclaredField("position").also {
            it.isAccessible = true
        }

    enum class State(val state:Int) {
        COLLAPSED(BottomSheetBehavior.STATE_COLLAPSED),
        EXPANDED(BottomSheetBehavior.STATE_EXPANDED),
        HALF_EXPANDED(BottomSheetBehavior.STATE_HALF_EXPANDED),
        HIDDEN(BottomSheetBehavior.STATE_HIDDEN)
    }

    init {
        addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                // Need to call requestLayout() when selected page is changed so that
                // `BottomSheetBehavior` calls `findScrollingChild()` and recognizes the new page
                // as the "scrollable child".
                requestLayout()
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean = true

    override fun getChildAt(index: Int): View {
        val stackTrace = Throwable().stackTrace
        val calledFromFindScrollingChild = stackTrace.getOrNull(1)?.let {
            it.className == "com.google.android.material.bottomsheet.BottomSheetBehavior" &&
                    it.methodName == "findScrollingChild"
        }
        if (calledFromFindScrollingChild != true) {
            return super.getChildAt(index)
        }

        // Swap index 0 and `currentItem`
        val currentView = getCurrentView() ?: return super.getChildAt(index)
        return if (index == 0) {
            currentView
        } else {
            var view = super.getChildAt(index)
            if (view == currentView) {
                view = super.getChildAt(0)
            }
            return view
        }
    }

    private fun getCurrentView(): View? {
        for (i in 0 until childCount) {
            val child = super.getChildAt(i)
            val lp = child.layoutParams as? ViewPager.LayoutParams
            if (lp != null) {
                val position = positionField.getInt(lp)
                if (!lp.isDecor && currentItem == position) {
                    return child
                }
            }
        }
        return null
    }

    fun initView(){
        bottomSheetView.run {
            halfExpandedRatio = 0.45f
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