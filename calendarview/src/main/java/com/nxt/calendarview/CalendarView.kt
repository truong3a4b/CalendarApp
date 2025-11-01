package com.nxt.calendarview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var root: LinearLayout
    private lateinit var btnPrevMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var displayMonth: TextView
    private lateinit var gridDays: GridView
    private val weekLabels = mutableListOf<TextView>()

    private var weekDays: Array<String>? = null
    private var onDateSelected: ((Date) -> Unit)? = null // ham callback khi chon ngay
    private var selectedDate: Date? = null
    private var today: Date = calendar.time

    //custom style
    private var backgroundColor: Int = 0
    private var dayTextStyle: Int = 0
    private var todayColor: Int = 0
    private var selectedColor: Int = 0
    private var selectedIndicator: Drawable? = null
    private var showEventIndicator: Boolean = false
    private var eventIndicatorColor: Int = Color.RED

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.calendar_view, this, true)
        root = findViewById(R.id.root_view)
        btnPrevMonth = findViewById(R.id.btn_pre_month)
        btnNextMonth = findViewById(R.id.btn_next_month)
        displayMonth = findViewById(R.id.month_year)
        gridDays = findViewById(R.id.calendar_grid)
        weekLabels.addAll(
            listOf(
                findViewById(R.id.sun),
                findViewById(R.id.mon),
                findViewById(R.id.tue),
                findViewById(R.id.wed),
                findViewById(R.id.thu),
                findViewById(R.id.fri),
                findViewById(R.id.sat),
            )
        )



        // đọc attrs
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CalendarView,
            0, 0
        ).apply {
            try {
                backgroundColor = getColor(R.styleable.CalendarView_backgroundColor, Color.WHITE)
                dayTextStyle = getResourceId(R.styleable.CalendarView_dayTextStyle, 0)
                todayColor = getColor(R.styleable.CalendarView_todayColor, Color.RED)
                selectedColor = getColor(R.styleable.CalendarView_selectedColor, backgroundColor)
                selectedIndicator = getDrawable(R.styleable.CalendarView_selectedIndicator)
                showEventIndicator = getBoolean(R.styleable.CalendarView_showEventIndicator, false)
                eventIndicatorColor = getColor(R.styleable.CalendarView_eventIndicatorColor, Color.RED)
                //background
                getColor(R.styleable.CalendarView_backgroundColor, 0)
                    .takeIf { it != 0 }?.let { root.setBackgroundColor(it) }

                // icons
                getResourceId(R.styleable.CalendarView_prevIcon, 0)
                    .takeIf { it != 0 }?.let { btnPrevMonth.setImageResource(it) }

                getResourceId(R.styleable.CalendarView_nextIcon, 0)
                    .takeIf { it != 0 }?.let { btnNextMonth.setImageResource(it) }

                // styles
                getResourceId(R.styleable.CalendarView_monthTextStyle, 0)
                    .takeIf { it != 0 }?.let { TextViewCompat.setTextAppearance(displayMonth, it) }

                getResourceId(R.styleable.CalendarView_weekTextStyle, 0)
                    .takeIf { it != 0 }?.let { styleId ->
                        weekLabels.forEach { TextViewCompat.setTextAppearance(it, styleId) }
                    }

                // weekdays
                val weekDaysResId = getResourceId(R.styleable.CalendarView_weekDays, 0)
                if (weekDaysResId != 0) {
                    weekDays = resources.getStringArray(weekDaysResId)
                } else {
                    val weekText = getString(R.styleable.CalendarView_weekDaysText)
                    if (!weekText.isNullOrEmpty()) {
                        weekDays = weekText.split(",").map { it.trim() }.toTypedArray()
                    }
                }
            } finally {
                recycle()
            }
        }
        setupWeekLabels()
        setupCalendar()

        btnPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            setupCalendar()
        }

        btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            setupCalendar()
        }
        displayMonth.setOnClickListener {

        }
        gridDays.setOnItemClickListener { _, _, position, _ ->
            val selectedDate = (gridDays.adapter.getItem(position) as Date)
            setSelectedDate(selectedDate)
        }
    }

    private fun setupWeekLabels() {
        val labels = weekDays ?: arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (i in labels.indices) {
            weekLabels[i].text = labels[i]
        }
    }

    //ham thiet lap lich
    private fun setupCalendar() {
        //Cap nhat thang va nam hien thi
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        displayMonth.text = dateFormat.format(calendar.time)

        //Tao danh sach ngay trong thang
        val cells = ArrayList<Date>()
        val monthCalendar =
            calendar.clone() as Calendar //Sao chep de khong anh huong den calendar chinh
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1) //Dat ve ngay dau tien cua thang
        val firstDayOfMonth =
            monthCalendar.get(Calendar.DAY_OF_WEEK) - 1 //Lay thu cua ngay dau tien trong thang

        monthCalendar.add(
            Calendar.DAY_OF_MONTH,
            -firstDayOfMonth
        ) // Di chuyen ve ngay dau tien trong tuan
        // Them cac ngay vao danh sach den khi du so ngay can thiet de hien thi
        while (cells.size < 42) { //42 vi 6 hang x 7 cot
            cells.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1) // Tang ngay len 1
        }

        gridDays.adapter = CalendarAdapter(context, cells, today, calendar).apply {
            setStyle(backgroundColor, dayTextStyle, todayColor, selectedColor, selectedIndicator, showEventIndicator, eventIndicatorColor)
        }
    }


    // ham dat listener khi chon ngay
    fun setOnDateSelectedListener(listener: (Date) -> Unit) {
        onDateSelected = listener
    }

    fun setSelectedDate(date: Date?) {
        this.selectedDate = date
        if (selectedDate != null) {
            val cal = Calendar.getInstance().apply { time = selectedDate!! }
            val isCurrentMonth = cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
            //Neu ko phai thang hien tai chuyen sang thang do
            if (!isCurrentMonth) {
                calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR))
                calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH))
                setupCalendar()
            }


            // Cập nhật màu trong adapter
            (gridDays.adapter as? CalendarAdapter)?.setSelectedDate(selectedDate)
        }

    }

    fun getSelectedDate(): Date? {
        return selectedDate
    }

    fun getDayOfYear(): Int {
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

    fun setToday(date: Date) {
        today = date
        calendar.time = date
        setupCalendar()
    }

    fun getToday(): Date {
        return today
    }

    fun getStyle(): String {
        return "backgroundColor: $backgroundColor, dayTextStyle: $dayTextStyle, todayColor: $todayColor, selectedColor: $selectedColor"
    }
    fun addEvent(date: Date){
        (gridDays.adapter as? CalendarAdapter)?.addEvent(date)
    }
    fun removeEvent(date: Date){
        (gridDays.adapter as? CalendarAdapter)?.removeEvent(date)
    }
}