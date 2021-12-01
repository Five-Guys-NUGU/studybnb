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
            val intent = Intent(this, SubjectActivity::class.java).apply {
                putExtra("isTimer", true)
            }
            startActivity(intent)
        }

        note_btn.setOnClickListener {
          myStartActivity(SubjectSelectActivity::class.java)
        }

        setting_btn.setOnClickListener {
            myStartActivity(SettingActivity::class.java)
        }
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }

}