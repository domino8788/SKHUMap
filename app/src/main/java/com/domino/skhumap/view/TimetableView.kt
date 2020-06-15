package com.domino.skhumap.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.*
import com.domino.skhumap.R
import com.domino.skhumap.dto.Sticker
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