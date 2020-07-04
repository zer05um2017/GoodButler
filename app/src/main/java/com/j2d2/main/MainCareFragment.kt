package com.j2d2.main


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucoseActivity
import com.j2d2.feeding.FeedingActivity
import com.j2d2.graph.GraphFragment
import com.j2d2.insulin.InsulinActivity
import com.j2d2.pedometer.PedometerActivity
import kotlinx.android.synthetic.main.maincare_fragment.*

class MainCareFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.maincare_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnInsulin.setOnClickListener {
            val intent = Intent(requireContext(), InsulinActivity::class.java)
            startActivity(intent)
        }

        btnPedometer.setOnClickListener {
            val intent = Intent(requireContext(), PedometerActivity::class.java)
            startActivity(intent)
        }

        btnFeeding.setOnClickListener {
            val intent = Intent(requireContext(), FeedingActivity::class.java)
            startActivity(intent)
        }

        btnBloodGlucose.setOnClickListener {
            val intent = Intent(requireContext(), BloodGlucoseActivity::class.java)
            startActivity(intent)
        }
    }
}
