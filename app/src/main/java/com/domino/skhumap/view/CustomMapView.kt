package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.domino.skhumap.R

class CustomMapView(context:Context, private val attrs: AttributeSet?, private val defStyleAttr:Int): AppCompatImageView(context, attrs, defStyleAttr) {

    private var downX = 0f
    private var downY = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        attrs?.let { adjustAttrs(attrs!!, defStyleAttr) }
    }

    /**
     * Touch 이벤트 발생
     * @param event 모션 이벤트 정보가 담긴 객체
     * @author domino
     * @since 2020.4.8.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x
                val moveY = event.y
                val distanceX = downX-moveX
                val distanceY = downY-moveY

                this.scrollBy(distanceX.toInt(), distanceY.toInt())

                downX = moveX
                downY = moveY
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

    /**
     * layout에 명시된 속성값을 뷰에 적용
     * @param attrs 속성값이 담긴 set
     * @param defStyle 기본 속성값
     * @author domino
     * @since 2020.4.6.
     */
    fun adjustAttrs(attrs:AttributeSet, defStyle:Int) =
        context.obtainStyledAttributes(attrs,
            R.styleable.CustomMapView, defStyleAttr, 0).let {
            setImageResource(it.getResourceId(
                R.styleable.CustomMapView_mapSrc,
                R.drawable.temp_campus_map
            ))
            scaleType = ScaleType.CENTER
            it.recycle()
        }

}