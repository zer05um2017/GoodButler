package com.j2d2.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_pedometer.*
import java.lang.Exception

class PedometerActivity : AppCompatActivity(), SensorEventListener {
    private var mSteps = 0
    private var mCounterSteps = 0
    private lateinit var sensorManager: SensorManager
    private lateinit var stepCountSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_pedometer_title)
        setContentView(R.layout.activity_pedometer)

        try {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        } catch (e: Exception) {
            Toast.makeText(this@PedometerActivity, getString(R.string.com_j2d2_pedometer_pedometer_message_unsupported_pedometer), Toast.LENGTH_LONG).show()
            finish()
            return
        }

//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
//            //Toast.makeText(this,"Success! There's a pressure sensor.", Toast.LENGTH_LONG)
//        } else {
//            Toast.makeText(this,"Failure! No pressure sensor.", Toast.LENGTH_LONG)
//            return
//        }

        btnStart.setOnClickListener {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }

        btnFinish.setOnClickListener {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (mCounterSteps < 1) {
            // initial value
            mCounterSteps = event!!.values[0].toInt()
        }

        if(event!!.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            mSteps = (event!!.values[0] - mCounterSteps.toFloat()).toInt()
            txtStepCount.text = mSteps.toString()
        }
    }

    override fun onAccuracyChanged(event: Sensor?, p1: Int) {

    }
}