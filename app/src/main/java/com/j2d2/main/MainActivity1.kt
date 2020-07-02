package com.j2d2.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucoseActivity
import com.j2d2.feeding.FeedingActivity
import com.j2d2.graph.GraphFragment
import com.j2d2.insulin.InsulinActivity
import com.j2d2.pedometer.PedometerActivity
import kotlinx.android.synthetic.main.activity_main1.*

class MainActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        btnGraph.setOnClickListener {
            val intent = Intent(this@MainActivity1, GraphFragment::class.java)
            startActivity(intent)
        }

        btnInsulin.setOnClickListener {
            val intent = Intent(this@MainActivity1, InsulinActivity::class.java)
            startActivity(intent)
        }

        btnPedometer.setOnClickListener {
            val intent = Intent(this@MainActivity1, PedometerActivity::class.java)
            startActivity(intent)
        }

        btnFeeding.setOnClickListener {
            val intent = Intent(this@MainActivity1, FeedingActivity::class.java)
            startActivity(intent)
        }

        btnBloodGlucose.setOnClickListener {
            val intent = Intent(this@MainActivity1, BloodGlucoseActivity::class.java)
            startActivity(intent)
        }
    }
}
