package com.j2d2.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.list_inflater.view.*

class SettingActivity : AppCompatActivity() {
    val itemLists: MutableList<ItemList> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_main_setting_title)
        setContentView(R.layout.activity_setting)
        setListData()
        val layoutManager = LinearLayoutManager(this@SettingActivity)
        // 리사이클러뷰의 아이템을 역순으로 정렬하게 함
        layoutManager.reverseLayout = false
        // 리사이클려뷰의 아이템을 쌓는 순서를 끝부터 쌓게 함
        layoutManager.stackFromEnd = false
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MyAdapter(itemLists)
        // image license
        // <div>아이콘 제작자
        // 아이콘 제작자 <a href="https://www.flaticon.com/kr/authors/freepik" title="Freepik">Freepik</a>
        // from <a href="https://www.flaticon.com/kr/" title="Flaticon"> www.flaticon.com</a>
    }

    private fun setListData() {
        with(itemLists) {
            add(ItemList(0, "강아지 정보"))
            add(ItemList(1, "라이센스"))
        }
    }

    inner class MyViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 리스트 타이틀
        val textTitle: TextView = itemView.textTitle
    }

    // RecyclerView 의 어댑터 클래스
    inner class MyAdapter(itemLists: MutableList<ItemList>) : RecyclerView.Adapter<MyViewHodler>() {
        // RecyclerView 에서 각 Row(행)에서 그릴 ViewHolder 를 생성할때 불리는 메소드
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHodler {
            return MyViewHodler(
                LayoutInflater.from(this@SettingActivity).inflate(R.layout.list_inflater,
                parent, false))
        }

        // RecyclerView 에서 몇개의 행을 그릴지 기준이 되는 메소드
        override fun getItemCount(): Int {
            return itemLists.size
        }

        // 각 행의 포지션에서 그려야할 ViewHolder UI 에 데이터를 적용하는 메소드
        override fun onBindViewHolder(holder: MyViewHodler, position: Int) {
            val item = itemLists[position]
//            // 배경 이미지 설정
//            Picasso.get().load(Uri.parse(post.bgUri)).fit().centerCrop().into(holder.imageView)
//            // 카드에 글을 세팅
//            holder.contentsText.text = post.message
//            // 글이 쓰여진 시간
//            holder.timeTextView.text = getDiffTimeText(post.writeTime as Long)
//            // 댓글 개수는 현재 상태에서는 0 으로 일단 세팅
//            holder.commentCountText.text = "0"
            holder.textTitle.text = item.titleName

            // 카드가 클릭되는 경우 DetailActivity 를 실행한다.
            when(item.listId) {
                0-> {
                    holder.itemView.setOnClickListener {
                        // 상세화면을 호출할 Intent 를 생성한다.
                        val intent = Intent(this@SettingActivity, MyPetActivity::class.java)
                        // 선택된 카드의 ID 정보를 intent 에 추가한다.
                        intent.putExtra("listId", item.listId)
                        // intent 로 상세화면을 시작한다.
                        startActivity(intent)
                    }
                }

                1-> {
                    holder.itemView.setOnClickListener {
                        val dlg = PopupLicenseDialog(this@SettingActivity)
                        dlg.start(getString(R.string.delete_message))
                    }
                }
            }

        }
    }
}