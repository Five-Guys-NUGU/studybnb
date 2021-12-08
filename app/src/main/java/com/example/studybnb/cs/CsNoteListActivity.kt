package com.example.studybnb.cs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studybnb.R
import com.example.studybnb.SubjectSelectActivity
import com.example.studybnb.adapter.CsItemAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_note_list.*

class CsNoteListActivity : AppCompatActivity() {
    private var firestore : FirebaseFirestore? = null
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cs_note_list)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        back_btn.setOnClickListener {
            myStartActivity(SubjectSelectActivity::class.java)
            finish()
        }
        write_btn.setOnClickListener {
            myStartActivity(CsNoteWriteActivity::class.java)
            finish()
        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.setReverseLayout(true)
        layoutManager.setStackFromEnd(true)
        diary_list.layoutManager = layoutManager
        diary_list.adapter = CsItemAdapter(this)
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }

    override fun onBackPressed() {
        myStartActivity(SubjectSelectActivity::class.java)
        finish()
    }




}