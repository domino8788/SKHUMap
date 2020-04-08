package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ScrollView
import com.domino.skhumap.R
import kotlinx.android.synthetic.main.custom_map_view.view.*

class CustomMapView(context:Context, private val attrs: AttributeSet?, private val defStyleAttr:Int): ScrollView(context, attrs, defStyleAttr) {

    private var mx = 0f
    private var my = 0f
    private val mapView: View = inflate(
        context,
        R.layout.custom_map_view, this
    )
    private val vScroll: ScrollView by lazy { mapView.scroll_Vertical }
    private val hScroll: HorizontalScrollView by lazy { mapView.scroll_Horizontal }
    private val imageView: ImageView by lazy { mapView.img_map }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        attrs?.let { adjustAttrs(attrs!!, defStyleAttr) }
//        hScroll.setOnTouchListener { v, event ->
//            var curX: Float
//            var curY: Float
//
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    mx = event.x
//                    my = event.y
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    curX = event.x
//                    curY = event.y
//                    img_map.scrollBy(((mx - curX)*1.5).toInt(), ((my - curY)*1.5).toInt())
//                    mx = curX
//                    my = curY
//                }
//                MotionEvent.ACTION_UP -> {
//
//                }
//            }
//            true
//        }
//        vScroll.setOnTouchListener { v, event ->
//            var curX: Float
//            var curY: Float
//
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    mx = event.x
//                    my = event.y
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    curX = event.x
//                    curY = event.y
//                    img_map.scrollBy(((mx - curX)*1.5).toInt(), ((my - curY)*1.5).toInt())
//                    mx = curX
//                    my = curY
//                }
//                MotionEvent.ACTION_UP -> {
//
//                }
//            }
//            true
//        }
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
            imageView.setImageResource(it.getResourceId(
                R.styleable.CustomMapView_mapSrc,
                R.drawable.temp_campus_map
            ))

            it.recycle()
        }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        var curX: Float
        var curY: Float

            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    mx = event.x
                    my = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    curX = event.x
                    curY = event.y
                    img_map.scrollBy(((mx - curX)*1.5).toInt(), ((my - curY)*1.5).toInt())
                    mx = curX
                    my = curY
                }
                MotionEvent.ACTION_UP -> {

                }
            }
        return super.onInterceptTouchEvent(event)
    }

}