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

        btnStart.setOnClickListener {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST)
            Toast.makeText(this, getString(R.string.com_j2d2_pedometer_start_message), Toast.LENGTH_LONG).show()
        }

        btnFinish.setOnClickListener {
            sensorManager.unregisterListener(this)
            Toast.makeText(this, getString(R.string.com_j2d2_pedometer_stop_message), Toast.LENGTH_SHORT).show()
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