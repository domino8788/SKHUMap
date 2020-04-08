package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.domino.skhumap.R
import kotlin.math.abs

class CustomMapView(context:Context, private val attrs: AttributeSet?, private val defStyleAttr:Int): AppCompatImageView(context, attrs, defStyleAttr) {

    private var downX = 0f
    private var downY = 0f

    private var imageWidth = 0
    private var imageHeight = 0

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
            /* 터치 시 발생하는 액션 */
            MotionEvent.ACTION_DOWN -> {
                downX = event.x //터치할때 x좌표를 저장한다.
                downY = event.y //터치할때 y좌표를 저장한다.
            }
            /* 터치 후 드래그 시 발생하는 액션 */
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x //드래그할때 x좌표를 저장한다,
                val moveY = event.y //드래그할때 y좌표를 저장한다.
                val distanceX = downX-moveX //터치할때의 x좌표와 드래그할때 x좌표 사이 거리를 계산한다.
                val distanceY = downY-moveY //터치할때의 y좌표와 드래그할때 y좌표 사이 거리를 계산한다.

                this.scrollBy(distanceX.toInt(), distanceY.toInt()) //드래그할때 x, y축으로 이동한 만큼 스크롤을 이동시킨다.

                downX = moveX //기존 드래그할때 x좌표의 값을 보관한다.
                downY = moveY //기존 드래그할때 y좌표의 값을 보관한다.
            }
            /* 드래그가 끝나고 터치를 떼면 발생하는 액션 */
            MotionEvent.ACTION_UP -> {
                var overX = 0f //이미지 범위를 x축으로 오버한 거리
                var overY = 0f //이미지 범위를 y축으로 오버한 거리

                if(abs(scrollX) > imageWidth/3) { //스크롤이 이미지 width의 1/3 을 오버하면
                    if (scrollX > 0) //x>0 방향으로 스크롤 햇을때 x축 오버거리를 계산한다.
                        overX = -(scrollX - imageWidth / 3 + 1).toFloat()
                    else //x<0 방향으로 스크롤 했을때 y축 오버거리를 계산한다.
                        overX = -(scrollX + imageWidth / 3 - 1).toFloat()
                }
                if(abs(scrollY) > imageHeight/3) { //스크롤이 이미지 height의 1/3을 오버하면
                    if (scrollY > 0) //y>0 방향으로 스크롤 했을때 y축 오버거리를 계산한다.
                        overY = -(scrollY - imageHeight / 3 + 1).toFloat()
                    else //y<0 방향으로 스크롤 했을때 y축 오버거리를 계산한다.
                        overY = -(scrollY + imageHeight / 3 - 1).toFloat()
                }
                scrollBy(overX.toInt(), overY.toInt()) //오버한 거리 만큼 스크롤을 되돌린다.
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
            val resId = it.getResourceId(
                R.styleable.CustomMapView_mapSrc,
                R.drawable.temp_campus_map
            )
            setImageResource(resId)
            resources.getDrawable(resId).run {
                imageWidth = intrinsicWidth
                imageHeight = intrinsicWidth
            }
            scaleType = ScaleType.CENTER
            it.recycle()
        }

}