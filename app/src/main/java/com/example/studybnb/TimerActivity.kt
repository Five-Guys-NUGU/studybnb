package com.example.studybnb

/**
 * @author Michael Sklors
 *
 * https://www.youtube.com/watch?v=RLnb4vVkftc&ab_channel=CodinginFlow
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import kotlinx.android.synthetic.main.activity_setting.back_btn

class TimerActivity : AppCompatActivity() {
    private lateinit var chronometer: Chronometer
    private var pauseOffset: Long = 0
    private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        chronometer = findViewById(R.id.chronometer)
        chronometer.format

        back_btn.setOnClickListener {
            finish()
        }
    }

    fun startTimer(v: View) {
        if (!running) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            running = true
        }
    }

    fun pauseTimer(v: View) {
        if (running) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            running = false;
        }
    }

    fun resetTimer(v: View) {
        chronometer.base = SystemClock.elapsedRealtime()
        pauseOffset = 0
    }
}