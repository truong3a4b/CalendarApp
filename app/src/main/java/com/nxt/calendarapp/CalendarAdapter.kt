package com.nxt.calendarapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import java.text.SimpleDateFormat
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarAdapter(
    private val context: Context,
    private val days: List<Date>,
    private val today: Date,
    private val currentCalendar: Calendar = Calendar.getInstance()
) : BaseAdapter() {


    private val dateFormat = SimpleDateFormat("d", Locale.getDefault())
    private val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate: Date? = null
    private val listEvent: MutableSet<String> = mutableSetOf()//luu tru danh sach ngay co su kien

    //thuoc tinh style
    private var backgroundColor: Int = 0
    private var dayTextStyle: Int= 0
    private var todayColor: Int = 0
    private var selectedColor: Int = 0
    private var selectedIndicator: Drawable? =null


    fun setSelectedDate(date: Date?) {
        selectedDate = date
        notifyDataSetChanged() // Vẽ lại lưới
    }

    override fun getCount() = days.size

    override fun getItem(position: Int) = days[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val date = days[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.calendar_view_cell, parent, false)
        val tvDay = view.findViewById<TextView>(R.id.day)
        val eventDot = view.findViewById<View>(R.id.event)

        tvDay.text = dateFormat.format(date)





        //chuyen date sang calendar de so sanh cho de
        val cal = Calendar.getInstance().apply { time = date }
        val isToday = sameDay(today, date)
        val isCurrentMonth = cal.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
        val isSelected = selectedDate?.let { sameDay(date, selectedDate!!) } == true

        when {
            isSelected -> {
                tvDay.background = selectedIndicator
                tvDay.setTextColor(selectedColor)
            }
            isToday -> {
                tvDay.setBackgroundColor(backgroundColor)
                tvDay.setTextColor(todayColor)
            }
            !isCurrentMonth -> {
                tvDay.setBackgroundColor(backgroundColor)
                tvDay.setTextColor(Color.GRAY)
            }
            else -> {
                tvDay.setBackgroundColor(backgroundColor)
                TextViewCompat.setTextAppearance(tvDay, dayTextStyle)
            }
        }

        //kiem tra su kien
        if(listEvent.contains(format.format(date))){
            eventDot.visibility = View.VISIBLE
        }else{
            eventDot.visibility = View.INVISIBLE
        }
        return view
    }

    private fun sameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    fun setStyle(backgroundColor: Int,dayTextStyle: Int, todayColor: Int, selectedColor: Int, selectedIndicator: Drawable?){
        this.backgroundColor = backgroundColor
        this.dayTextStyle = dayTextStyle
        this.todayColor = todayColor
        this.selectedColor = selectedColor
        this.selectedIndicator = selectedIndicator
        if(selectedIndicator == null){
            this.selectedIndicator = context.getDrawable(R.drawable.bg_day_selected)
        }
    }
    fun addEvent(date: Date) {
        listEvent.add(format.format(date))
        notifyDataSetChanged()
    }
    fun removeEvent(date: Date) {
        listEvent.minus(date)
        notifyDataSetChanged()
    }
}


