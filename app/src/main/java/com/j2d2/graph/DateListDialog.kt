package com.j2d2.graph

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.j2d2.R
import com.j2d2.petinfo.OnBreedListClickListener
import kotlinx.android.synthetic.main.breed_row.view.*
import kotlinx.android.synthetic.main.popup_breedlist_dialog.*

class DateListDialog (context : Context, private val listener: OnDayClickListener, dateList : ArrayList<ArrayList<String>>) {
    private val dlg = Dialog(context, R.style.AppBaseTheme)   //부모 액티비티의 context 가 들어감
    private var dateList = dateList

    fun start() {
        val dayList: MutableList<String> = mutableListOf()
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.popup_breedlist_dialog)
        dlg.setCancelable(false)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        val context = dlg.context
//        val appDatabase = AppDatabase.getInstance(context)
//        val filename = "breed.txt"
//        val fileInString: String =
//            context.applicationContext.assets.open(filename).bufferedReader().use { it.readText() }
//
//        val strList = fileInString.split("\n")
//        for((index, line) in strList.withIndex()) {
//            breedLists.add(BreedList(index, line))
//        }

//        CoroutineScope(Dispatchers.IO).launch {
//            val list = appDatabase?.breedDao()?.getBreedList()
//            for(breed in list) {
//                breedLists.add(BreedList(breed.id, breed.name))
//            }
//        }
        for(days in dateList) {
            for(day in days) {
                dayList.add(day)
            }
        }

        val layoutManager = LinearLayoutManager(dlg.context)
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
        dlg.breedRecyclerView.layoutManager = layoutManager
        dlg.breedRecyclerView.adapter = MyAdapter(dlg.context, dayList, dlg)
        dlg?.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog?.dismiss()
                    return true
                }
                return false
            }
        })
        dlg.show()
    }

    inner class MyViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.textBreedTitle
    }

    inner class MyAdapter(val context: Context,
                          private val dayList: MutableList<String>,
                          val dialog: Dialog
    ) : RecyclerView.Adapter<MyViewHodler>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHodler {
            return MyViewHodler(

                LayoutInflater.from(context).inflate(
                    R.layout.breed_row,
                    parent, false))
        }

        override fun getItemCount(): Int {
            return dayList.size
        }

        override fun onBindViewHolder(holder: MyViewHodler, position: Int) {
            val item = dayList[position]
            holder.textTitle.text = item
            holder.itemView.setOnClickListener {
                listener.onSelected(item)
                dialog.dismiss()
            }
        }
    }
}