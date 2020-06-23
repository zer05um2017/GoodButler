package com.j2d2.graph

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @ExperimentalTime
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.j2d2.goodbutler", appContext.packageName)

        val grecal = GregorianCalendar(2020, 6, 23, 9, 45, 33).toString()
        val milli = GregorianCalendar(2020, 6, 23, 9, 45).timeInMillis
        Log.v("#####", System.currentTimeMillis().toString())
        Log.v("#####", grecal)
        Log.v("#####", milli.toString())

        val calendar = GregorianCalendar.getInstance()
        calendar.timeInMillis = milli
        Log.v("#####", calendar.get(Calendar.YEAR).toString())
        Log.v("#####", calendar.get(Calendar.MONTH).toString())
        Log.v("#####", calendar.get(Calendar.DAY_OF_MONTH).toString())
        Log.v("#####", calendar.get(Calendar.HOUR_OF_DAY).toString())
        Log.v("#####", calendar.get(Calendar.MINUTE).toString())
    }
}