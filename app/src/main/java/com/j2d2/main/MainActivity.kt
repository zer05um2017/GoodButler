package com.j2d2.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.j2d2.R
import com.j2d2.graph.GraphActivity
import com.j2d2.insulin.InsulinActivity
import com.j2d2.pedometer.PedometerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGraph.setOnClickListener {
            val intent = Intent(this@MainActivity, GraphActivity::class.java)
            startActivity(intent)
        }

        btnInsulin.setOnClickListener {
            val intent = Intent(this@MainActivity, InsulinActivity::class.java)
            startActivity(intent)
        }

        btnPedometer.setOnClickListener {
            val intent = Intent(this@MainActivity, PedometerActivity::class.java)
            startActivity(intent)
        }
    }
}
