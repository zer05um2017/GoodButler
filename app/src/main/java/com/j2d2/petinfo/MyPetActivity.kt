package com.j2d2.petinfo

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import com.j2d2.main.AppDatabase
import com.j2d2.main.MainApp
import com.j2d2.main.SharedPref
import kotlinx.android.synthetic.main.activity_my_pet.*
import kotlinx.android.synthetic.main.activity_my_pet.btnSave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MyPetActivity : AppCompatActivity(), OnBreedListClickListener {
    var selectedBreed: BreedList? = null
    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_petinfo_my_pat_title)
        setContentView(R.layout.activity_my_pet)
        selectedBreed = BreedList(-1, "")
        appDatabase = AppDatabase.getInstance(this)
        textBreedSelection.setOnClickListener {
            val dlg = PopupBreedSelectionDialog(
                this@MyPetActivity,
                this
            )
            dlg.start()
        }

        setDateTimeListener()

        with(SharedPref.prefs) {
            if(!contains(R.string.com_j2d2_petinfo_is_breed_saved.toString())) {
                val itemLists: MutableList<Breed> = mutableListOf()
                val filename = "breed.txt"
                val fileInString: String = applicationContext.assets.open(filename).bufferedReader().use { it.readText() }
                val strList = fileInString.split("\n")

                for((index, name) in strList.withIndex()) {
                    itemLists.add(Breed(id = index, name = name ))
                }

                CoroutineScope(Dispatchers.IO).launch {
                    appDatabase?.breedDao()?.insert(itemLists)
                }
                with(edit()) {
                    putBoolean(R.string.com_j2d2_petinfo_is_breed_saved.toString(), true)
                    commit()
                }
            }

            if(contains(R.string.com_j2d2_petinfo_is_petinfo_saved.toString())) {
                val selectedPetId = MainApp.getSelectedPetId()
                val birth = GregorianCalendar.getInstance()
                val occur = GregorianCalendar.getInstance()
                CoroutineScope(Dispatchers.IO).launch {
                    val pet = appDatabase?.petDao()?.findPetById(selectedPetId)
                    selectedBreed?.breedCode = pet?.breedType!!
                    selectedBreed?.breedName = pet?.breedName!!
                    birth.timeInMillis = pet?.birth!!
                    occur.timeInMillis = pet?.occurDate!!
                    CoroutineScope(Dispatchers.Main).launch {
                        editName.setText(pet.name)
                        textBirthDate.setText(
                            "%02d-%02d-%02d".format(
                                birth.get(Calendar.YEAR),
                                birth.get(Calendar.MONTH),
                                birth.get(Calendar.DAY_OF_MONTH)
                            )
                        )
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        textOccuredDate.setText(
                            "%02d-%02d-%02d".format(
                                occur.get(Calendar.YEAR),
                                occur.get(Calendar.MONTH),
                                occur.get(Calendar.DAY_OF_MONTH)
                            )
                        )
                        editWeight.setText(pet?.weight.toString())
                        when (pet?.sex) {
                            0 -> {
                                rdoFemale.isChecked = true
                            }
                            1 -> {
                                rdoMale.isChecked = true
                            }
                        }
                        textBreedSelection.text = pet.breedName
                        selectedBreed?.breedCode = pet.breedType
                        selectedBreed?.breedName = pet.breedName
                        editComplication.setText(pet?.remark.toString())
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            if (editName.text.trim().isEmpty()) {
                Toast.makeText(this@MyPetActivity, getString(R.string.com_j2d2_petinfo_input_error_message_petname), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (textBirthDate.text.trim().isEmpty()) {
                Toast.makeText(this@MyPetActivity, getString(R.string.com_j2d2_petinfo_input_error_message_birth), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (textOccuredDate.text.trim().isEmpty()) {
                Toast.makeText(this@MyPetActivity, getString(R.string.com_j2d2_petinfo_input_error_message_occur), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (textBreedSelection.text.trim().isEmpty()) {
                Toast.makeText(this@MyPetActivity, getString(R.string.com_j2d2_petinfo_input_error_message_breed), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (editWeight.text.trim().isEmpty()) {
                Toast.makeText(this@MyPetActivity, getString(R.string.com_j2d2_petinfo_input_error_message_weight), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                var id:Long = 0
                if(MainApp.getSelectedPetId() > 0) {
                    id = MainApp.getSelectedPetId()
                    appDatabase?.petDao()?.update(
                        Pet(
                            id = id,
                            name = editName.text.toString(),
                            birth = getBirthDate(),
                            occurDate = getOccuredDate(),
                            breedType = selectedBreed?.breedCode!!,
                            breedName = selectedBreed?.breedName!!,
                            weight = editWeight.text.toString().toFloat(),
                            sex = isFemale(),
                            remark = editComplication.text.toString()
                        )
                    )
                } else {
                    id = GregorianCalendar.getInstance().timeInMillis
                    appDatabase?.petDao()?.insert(
                        Pet(
                            id = id,
                            name = editName.text.toString(),
                            birth = getBirthDate(),
                            occurDate = getOccuredDate(),
                            breedType = selectedBreed?.breedCode!!,
                            breedName = selectedBreed?.breedName!!,
                            weight = editWeight.text.toString().toFloat(),
                            sex = isFemale(),
                            remark = editComplication.text.toString()
                        )
                    )
                    MainApp.setSelectedPetId(id)
                }

                with(SharedPref.prefs.edit()) {
                    putBoolean(R.string.com_j2d2_petinfo_is_petinfo_saved.toString(), true)
                    putLong(R.string.com_j2d2_petinfo_pet_selected_id .toString(), id)
                    commit()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        this@MyPetActivity,
                        getString(R.string.com_j2d2_petinfo_ins_message_input_complete),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }
        }
    }

    private fun isFemale(): Int {
        return if(rdoFemale.isChecked) 0 else 1
    }

    override fun onSelected(selected: BreedList) {
        textBreedSelection.text = selected.breedName
        selectedBreed = selected
    }

    private fun getBirthDate(): Long {
        val date = textBirthDate.text.split("-")
        return GregorianCalendar(date[0].toInt(), date[1].toInt(), date[2].toInt()).timeInMillis
    }

    private fun getOccuredDate(): Long {
        val date = textOccuredDate.text.split("-")
        return GregorianCalendar(date[0].toInt(), date[1].toInt(), date[2].toInt()).timeInMillis
    }

    private fun setDateTimeListener() {
        textBirthDate.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var y: Int
                    var m: Int
                    var d: Int
                    val cal = GregorianCalendar.getInstance()
                    y = cal.get(Calendar.YEAR)
                    m = cal.get(Calendar.MONTH)
                    d = cal.get(Calendar.DAY_OF_MONTH)

                    val datepickerdialog: DatePickerDialog = DatePickerDialog(
                        this@MyPetActivity,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            val myFormat = "yyyy-MM-dd" // mention the format you need
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            cal.set(year, monthOfYear, dayOfMonth)
                            textBirthDate!!.setText(sdf.format(cal.time))
                        },
                        y,
                        m,
                        d
                    )

                    datepickerdialog.show()
                    false
                }
            }
            true
        }

        textOccuredDate.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var y: Int
                    var m: Int
                    var d: Int
                    val cal = GregorianCalendar.getInstance()
                    y = cal.get(Calendar.YEAR)
                    m = cal.get(Calendar.MONTH)
                    d = cal.get(Calendar.DAY_OF_MONTH)

                    val datepickerdialog: DatePickerDialog = DatePickerDialog(
                        this@MyPetActivity,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            val myFormat = "yyyy-MM-dd" // mention the format you need
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            cal.set(year, monthOfYear, dayOfMonth)
                            textOccuredDate!!.setText(sdf.format(cal.time))
                        },
                        y,
                        m,
                        d
                    )

                    datepickerdialog.show()
                    false
                }
            }
            true
        }
    }
}