# CalendarView

A customizable and lightweight Calendar View library for Android written in Kotlin.  
Allows easy date selection and event marking with a clean UI.

---

## Features

- Select a single date with callback listener
- Add and highlight events on specific dates
- Customizable colors and styles
- Easy integration with ViewBinding or XML


---

## Installation

Add JitPack to your project:

```gradle
// settings.gradle (or top-level build.gradle in older projects)
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

Add the library dependency:

dependencies {
    implementation 'com.github.truong3a4b:CalendarApp:${version}'
}
## Usage
### XML
<com.nxt.calendarview.CalendarView
    android:id="@+id/calendarView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
//cuttom
    app:backgroundColor="@color/black"
    app:dayTextStyle="@style/DayTextStyle"
    app:monthTextStyle="@style/MonthTextStyle"
    app:weekTextStyle="@style/WeekTextStyle"
    app:prevIcon="@drawable/baseline_arrow_back_24"
    app:nextIcon="@drawable/outline_arrow_right_alt_24"
    app:todayColor="#FF148B"
    app:selectedColor="#000000"
    app:selectedIndicator="@drawable/bg_day_selected2"
 />

### Kotlin
val calendarView = findViewById<CalendarView>(R.id.calendarView)

// Listen for date selection
calendarView.setOnDateSelectedListener { date ->
    Toast.makeText(this, "Selected: $date", Toast.LENGTH_SHORT).show()
}

// Add event highlight
calendarView.addEvent(Date())

//Other func
getSelectedDate()
getDayOfYear()
setToday(date: Date)
getToday()
removeEvent(date: Date)

## Result
<img width="369" height="447" alt="image" src="https://github.com/user-attachments/assets/6f5b0e47-8543-4f62-bc62-8f1b4d2d08bd" />
