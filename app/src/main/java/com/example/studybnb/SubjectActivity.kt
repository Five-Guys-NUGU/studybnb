package com.example.studybnb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Timer
import kotlinx.android.synthetic.main.activity_subject.back_btn
import kotlinx.android.synthetic.main.activity_subject.subCS_btn
import kotlinx.android.synthetic.main.activity_subject.subHistory_btn
import kotlinx.android.synthetic.main.activity_subject.subTOEIC_btn

class SubjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        val isTimer = intent?.extras?.getBoolean("isTimer")

        back_btn.setOnClickListener {
            finish()
        }



        if ( isTimer == true ) {
            subHistory_btn.setOnClickListener {
                myStartActivity(HistoryTimerActivity::class.java)
            }

            subTOEIC_btn.setOnClickListener {
                myStartActivity(TimerActivity::class.java)
            }

            subCS_btn.setOnClickListener {
                myStartActivity(CsTimerActivity::class.java)
            }
        }
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }
}
