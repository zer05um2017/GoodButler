package com.j2d2.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_pedometer.*

class PedometerActivity : AppCompatActivity(), SensorEventListener {
    private var mSteps = 0
    private var mCounterSteps = 0
    private lateinit var sensorManager: SensorManager
    private lateinit var stepCountSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
//            Toast.makeText(this,"Success! There's a pressure sensor.", Toast.LENGTH_LONG)
//        } else {
//            Toast.makeText(this,"Failure! No pressure sensor.", Toast.LENGTH_LONG)
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