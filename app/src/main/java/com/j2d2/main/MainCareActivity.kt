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

        btnGraph.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        btnInsulin.setOnClickListener {
            val intent = Intent(this, InsulinActivity::class.java)
            startActivity(intent)
        }

        btnPedometer.setOnClickListener {
            val intent = Intent(this, PedometerActivity::class.java)
            startActivity(intent)
        }

        btnFeeding.setOnClickListener {
            val intent = Intent(this, FeedingActivity::class.java)
            startActivity(intent)
        }

        btnBloodGlucose.setOnClickListener {
            val intent = Intent(this, BloodGlucoseActivity::class.java)
            startActivity(intent)
        }
    }
}
