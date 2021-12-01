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
                val intent = Intent(this, TimerActivity::class.java).apply {
                    putExtra("subject", "History")
                }
                startActivity(intent)
            }

            subTOEIC_btn.setOnClickListener {
                val intent = Intent(this, TimerActivity::class.java).apply {
                    putExtra("subject", "TOEIC")
                }
                startActivity(intent)
            }

            subCS_btn.setOnClickListener {
                val intent = Intent(this, TimerActivity::class.java).apply {
                    putExtra("subject", "CS")
                }
                startActivity(intent)
            }
        }
    }
}
