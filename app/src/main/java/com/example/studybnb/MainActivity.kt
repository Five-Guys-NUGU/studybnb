package com.example.studybnb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timer_btn.setOnClickListener {
//            val intent = Intent(this, TimerActivity::class.java)
//            startActivity(intent)
        }

        note_btn.setOnClickListener {
//            val intent = Intent(this, MyNoteActivity::class.java)
//            startActivity(intent)
        }

        setting_btn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}