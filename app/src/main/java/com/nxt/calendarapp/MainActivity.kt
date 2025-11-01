package com.nxt.calendarapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.nxt.calendarview.CalendarView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val calendar = findViewById<CalendarView>(R.id.calendar)
        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)

        calendar.setOnDateSelectedListener { date ->
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            Toast.makeText(this, format.format(date), Toast.LENGTH_SHORT).show()

        }

        calendar.addEvent(Date())
        button2.setOnClickListener {
            calendar.setSelectedDate(null)
        }
    }


}