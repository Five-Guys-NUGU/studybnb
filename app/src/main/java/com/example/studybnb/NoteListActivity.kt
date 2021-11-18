package com.example.studybnb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_note_list.*

class NoteListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        back_btn.setOnClickListener {
            finish()
        }
        write_btn.setOnClickListener {
            var intent = Intent(this, NoteWriteActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}