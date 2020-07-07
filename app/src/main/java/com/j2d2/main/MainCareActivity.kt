package com.j2d2.main


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucoseActivity
import com.j2d2.feeding.FeedingActivity
import com.j2d2.graph.GraphActivity
import com.j2d2.insulin.InsulinActivity
import com.j2d2.pedometer.PedometerActivity
import kotlinx.android.synthetic.main.activity_maincare.*

class MainCareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportActionBar?.title = getString(R.string.com_j2d2_feeding_title)
        setContentView(R.layout.activity_maincare)

        lgraph.setOnClickListener {
            startGraphActivity()
        }

        linsulin.setOnClickListener {
            startInsulinActivity()
        }

        ltakewalk.setOnClickListener {
            startPedometerActivity()
        }

        lfeed.setOnClickListener {
            startFeedingActivity()
        }

        lglucose.setOnClickListener {
            startBloodGlucoseActivity()
        }

        lsetting.setOnClickListener {
            startSettingActivity()
        }

        btnGraph.setOnClickListener {
            startGraphActivity()
        }

        btnInsulin.setOnClickListener {
            startInsulinActivity()
        }

        btnPedometer.setOnClickListener {
            startPedometerActivity()
        }

        btnFeeding.setOnClickListener {
            startFeedingActivity()
        }

        btnBloodGlucose.setOnClickListener {
            startBloodGlucoseActivity()
        }

        btnSetting.setOnClickListener {
            startSettingActivity()
        }
    }

    private fun startGraphActivity() {
        val intent = Intent(this, GraphActivity::class.java)
        startActivity(intent)
    }
    private fun startInsulinActivity() {
        val intent = Intent(this, InsulinActivity::class.java)
        startActivity(intent)
    }
    private fun startPedometerActivity() {
        val intent = Intent(this, PedometerActivity::class.java)
        startActivity(intent)
    }
    private fun startFeedingActivity() {
        val intent = Intent(this, FeedingActivity::class.java)
        startActivity(intent)
    }
    private fun startBloodGlucoseActivity() {
        val intent = Intent(this, BloodGlucoseActivity::class.java)
        startActivity(intent)
    }
    private fun startSettingActivity() {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }
}
