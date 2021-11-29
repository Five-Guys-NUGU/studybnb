package com.example.studybnb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_subject_select.*

class SubjectSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_select)

        back_btn.setOnClickListener {
            myStartActivity(MainActivity::class.java)
            finish()
        }

        subHistory_btn.setOnClickListener {

            }

        subTOEIC_btn.setOnClickListener {
            myStartActivity(NoteListActivity::class.java)
            finish()
        }

        subCS_btn.setOnClickListener {
            myStartActivity(CsNoteListActivity::class.java)
            finish()
        }
            
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }
}