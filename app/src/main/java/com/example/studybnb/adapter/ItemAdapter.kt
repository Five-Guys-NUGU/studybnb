package com.example.studybnb.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studybnb.NoteViewActivity
import com.example.studybnb.R
import com.example.studybnb.item.item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_view.view.*
import java.text.SimpleDateFormat

class ItemAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var firestore : FirebaseFirestore? = null
    private lateinit var auth : FirebaseAuth
    var title : String? = null
    var date : Long? = null
    var itemList: ArrayList<item> = arrayListOf()

    init {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore?.collection("NoteTaking")?.document("Subjects")?.collection("Toeic")?.whereEqualTo("uid", auth.currentUser?.uid)
            ?.get()?.addOnSuccessListener { documents ->
                itemList.clear()
                for (doc in documents) {
                    title = doc?.data?.get("title").toString()
                    date = (doc?.data?.get("date").toString()).toLong()
                    var item = item(title,date)
                    itemList.add(item!!)
                }
                notifyDataSetChanged()//새로고침
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = (holder as ViewHolder).itemView
        viewHolder.list_title_txt.text = itemList[position].title
        viewHolder.list_date_txt.text = SimpleDateFormat("yyyy.MM.dd").format(itemList[position].date)
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(Item : item){
            itemView.setOnClickListener {
                Intent(context, NoteViewActivity::class.java).apply {
                    putExtra("date", Item.date.toString())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
            }
        }
    }
}





