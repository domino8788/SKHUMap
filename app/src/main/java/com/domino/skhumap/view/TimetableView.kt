package com.domino.skhumap.view

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.domino.skhumap.R
import com.domino.skhumap.dto.Sticker
import com.domino.skhumap.dto.Time
import com.domino.skhumap.dto.TimetableSchedule
import com.domino.skhumap.vo.Lecture
import kotlinx.android.synthetic.main.inner_table.view.*
import kotlinx.android.synthetic.main.table_header.view.*
import kotlinx.android.synthetic.main.view_timetable.view.*
import java.util.*


class TimetableView(@get:JvmName("getContext_")val context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context):this(context, null)
    constructor(context:Context, attrs:AttributeSet?):this(context, attrs, 0)

    private var rowCount = 0
    private var columnCount = 0
    private var cellHeight = 0
    private var headerCellHeight = 0
    private var sideCellWidth = 0
    private lateinit var headerTitle: Array<String>
    private lateinit var stickerColors: Array<String>
    private var startTime = 0
    private var stickerBox: RelativeLayout? = null
    var tableHeader: TableLayout? = null
    var tableBox: TableLayout? = null
    var stickers: HashMap<Int, Sticker> = HashMap<Int, Sticker>()
    private var stickerCount = -1

    init {
        getAttrs(attrs)
        init()
    }

    private fun init() {
        val view: View = View.inflate(context, R.layout.view_timetable, this)
        stickerBox = view.sticker_box
        tableHeader = view.table_header
        tableBox = view.table_box
        createTable()
    }

    fun add(schedule: TimetableSchedule) {
        add(schedule, -1)
    }

    private fun add(schedule: TimetableSchedule, specIdx: Int) {
        val count = if (specIdx < 0) ++stickerCount else specIdx
        val sticker = Sticker()
        val tv = TextView(context).apply { ellipsize = TextUtils.TruncateAt.END }
        val param = createStickerParam(schedule)
        tv.layoutParams = param
        tv.setPadding(10, 0, 10, 0)
        tv.text = schedule.toString()
        tv.setTextColor(Color.parseColor("#FFFFFF"))
        tv.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_STICKER_FONT_SIZE_DP.toFloat()
        )
        tv.setTypeface(null, Typeface.BOLD)
        sticker.addTextView(tv)
        sticker.addSchedule(schedule)
        stickers[count] = sticker
        stickerBox!!.addView(tv)
        setStickerColor()
    }

    fun load(schedules: List<Lecture>) {
        var maxKey = 0
        schedules.groupBy(Lecture::times).values.forEachIndexed { key, sameSchedules->
            add(TimetableSchedule(*sameSchedules.toTypedArray()), key)
            if (maxKey < key) maxKey = key
        }
        stickerCount = maxKey + 1
        setStickerColor()
    }

    private fun setStickerColor() {
        val size = stickers.size
        val orders = IntArray(size)
        var i = 0
        for (key in stickers.keys) {
            orders[i++] = key
        }
        Arrays.sort(orders)
        val colorSize = stickerColors.size
        i = 0
        while (i < size) {
            for (v in stickers[orders[i]]!!.view) {
                v.setBackgroundColor(Color.parseColor(stickerColors[i % colorSize]))
            }
            i++
        }
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TimetableView)
        rowCount = a.getInt(
            R.styleable.TimetableView_row_count,
            DEFAULT_ROW_COUNT
        ) - 1
        columnCount = a.getInt(
            R.styleable.TimetableView_column_count,
            DEFAULT_COLUMN_COUNT
        )
        cellHeight = a.getDimensionPixelSize(
            R.styleable.TimetableView_cell_height,
            dp2Px(DEFAULT_CELL_HEIGHT_DP)
        )
        headerCellHeight = a.getDimensionPixelSize(
            R.styleable.TimetableView_cell_height,
            dp2Px(DEFAULT_HEADER_CELL_HEIGHT_DP)
        )
        sideCellWidth = a.getDimensionPixelSize(
            R.styleable.TimetableView_side_cell_width,
            dp2Px(DEFAULT_SIDE_CELL_WIDTH_DP)
        )
        val titlesId = a.getResourceId(R.styleable.TimetableView_header_title, R.array.header_title)
        headerTitle = a.resources.getStringArray(titlesId)
        val colorsId =
            a.getResourceId(R.styleable.TimetableView_sticker_colors, R.array.default_sticker_color)
        stickerColors = a.resources.getStringArray(colorsId)
        startTime = a.getInt(
            R.styleable.TimetableView_start_time,
            DEFAULT_START_TIME
        )
        a.recycle()
    }

    private fun createTable() {
        createTableHeader()
        for (i in 0 until rowCount) {
            val tableRow = TableRow(context)
            tableRow.layoutParams = createTableLayoutParam()
            for (k in 0 until columnCount) {
                val tv = TextView(context)
                tv.layoutParams = createTableRowParam(cellHeight)
                if (k == 0) {
                    tv.text = getHeaderTime(i)
                    tv.setTextColor(resources.getColor(R.color.colorHeaderText))
                    tv.setTextSize(
                        TypedValue.COMPLEX_UNIT_DIP,
                        DEFAULT_SIDE_HEADER_FONT_SIZE_DP.toFloat()
                    )
                    tv.setBackgroundColor(resources.getColor(R.color.colorHeader))
                    tv.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    tv.layoutParams = createTableRowParam(sideCellWidth, cellHeight)
                } else {
                    tv.text = ""
                    tv.background = resources.getDrawable(R.drawable.bg_timetable_border)
                    tv.gravity = Gravity.RIGHT
                }
                tableRow.addView(tv)
            }
            tableBox!!.addView(tableRow)
        }
    }

    private fun createTableHeader() {
        val tableRow = TableRow(context)
        tableRow.layoutParams = createTableLayoutParam()
        for (i in 0 until columnCount) {
            val tv = TextView(context)
            if (i == 0) {
                tv.layoutParams = createTableRowParam(sideCellWidth, headerCellHeight)
            } else {
                tv.layoutParams = createTableRowParam(headerCellHeight)
            }
            tv.setTextColor(resources.getColor(R.color.colorHeaderText))
            tv.setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_HEADER_FONT_SIZE_DP.toFloat()
            )
            tv.text = headerTitle[i]
            tv.gravity = Gravity.CENTER
            tableRow.addView(tv)
        }
        tableHeader!!.addView(tableRow)
    }

    private fun calCellWidth(): Int {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return (size.x - paddingLeft - paddingRight - sideCellWidth) / (columnCount - 1)
    }

    private fun calStickerHeightPx(schedule: TimetableSchedule): Int {
        val startTopPx = calStickerTopPxByTime(schedule.startTime)
        val endTopPx = calStickerTopPxByTime(schedule.endTime)
        return endTopPx - startTopPx
    }

    private fun calStickerTopPxByTime(time: Time): Int = (time.hour - startTime) * cellHeight + (time.minute / 60.0f * cellHeight).toInt()

    private fun createStickerParam(schedule: TimetableSchedule): RelativeLayout.LayoutParams {
        val cellW = calCellWidth()
        val param =
            RelativeLayout.LayoutParams(cellW, calStickerHeightPx(schedule))
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        param.setMargins(
            sideCellWidth + cellW * schedule.day,
            calStickerTopPxByTime(schedule.startTime),
            0,
            0
        )
        return param
    }

    private fun createTableLayoutParam(): TableLayout.LayoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT)

    private fun createTableRowParam(h_px: Int): TableRow.LayoutParams = TableRow.LayoutParams(calCellWidth(), h_px)

    private fun createTableRowParam(w_px: Int, h_px: Int): TableRow.LayoutParams = TableRow.LayoutParams(w_px, h_px)

    private fun getHeaderTime(i: Int): String = ((startTime + i) % 25).toString()

    private fun onCreateByBuilder(builder: Builder) {
        rowCount = builder.rowCount
        columnCount = builder.columnCount
        cellHeight = builder.cellHeight
        headerCellHeight = builder.headerCellHeight
        sideCellWidth = builder.sideCellWidth
        headerTitle = builder.headerTitle
        stickerColors = builder.stickerColors
        startTime = builder.startTime
        init()
    }

    internal class Builder(private val context: Context) {
        var rowCount: Int = DEFAULT_ROW_COUNT
        var columnCount: Int = DEFAULT_COLUMN_COUNT
        var cellHeight: Int = dp2Px(DEFAULT_CELL_HEIGHT_DP)
            set(dp) {
                field = dp2Px(dp)
            }
        var headerCellHeight:Int = dp2Px(DEFAULT_HEADER_CELL_HEIGHT_DP)
            set(dp) {
                field = dp2Px(dp)
            }
        var sideCellWidth: Int = dp2Px(DEFAULT_SIDE_CELL_WIDTH_DP)
            set(dp){
                field = dp2Px(dp)
            }
        var headerTitle: Array<String> = context.resources.getStringArray(R.array.header_title)
        var stickerColors: Array<String> = context.resources.getStringArray(R.array.default_sticker_color)
        var startTime: Int = DEFAULT_START_TIME

        fun build(): TimetableView = TimetableView(context).also { it.onCreateByBuilder(this) }

    }

    companion object {
        private const val DEFAULT_ROW_COUNT = 17
        private const val DEFAULT_COLUMN_COUNT = 6
        private const val DEFAULT_CELL_HEIGHT_DP = 150
        private const val DEFAULT_HEADER_CELL_HEIGHT_DP = 50
        private const val DEFAULT_SIDE_CELL_WIDTH_DP = 30
        private const val DEFAULT_START_TIME = 9
        private const val DEFAULT_SIDE_HEADER_FONT_SIZE_DP = 13
        private const val DEFAULT_HEADER_FONT_SIZE_DP = 15
        private const val DEFAULT_STICKER_FONT_SIZE_DP = 13
        private fun dp2Px(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}