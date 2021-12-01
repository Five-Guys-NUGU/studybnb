package com.example.studybnb

/**
 * @author Michael Sklors
 *
 * https://www.youtube.com/watch?v=RLnb4vVkftc&ab_channel=CodinginFlow
 */

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.example.studybnb.databinding.ActivityTimerBinding
import com.example.studybnb.model.StudyTimerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.android.synthetic.main.activity_setting.back_btn
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {
    // Firebase setup
    private lateinit var auth: FirebaseAuth
    var firestore :FirebaseFirestore = FirebaseFirestore.getInstance()
    var studyTimerModel = StudyTimerModel()

    // Timer attributes
    private lateinit var chronometer: Chronometer
    private var pauseOffset: Long = 0
    private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        auth = FirebaseAuth.getInstance()

        // Receive value of subject and display it in the screen
        val extraSubject = intent?.extras?.getString("subject").toString()
        studyTimerModel.subject = extraSubject
        val binding: ActivityTimerBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_timer)
        binding.studyTimerModel = studyTimerModel

        // Setup the timer
        chronometer = findViewById(R.id.chronometer)
        chronometer.format

        back_btn.setOnClickListener {
            finish()
        }

        finish_timer_btn.setOnClickListener {
            //studyTimerModel.UID = auth?.currentUser?.uid
            //studyTimerModel.study_time = (SystemClock.elapsedRealtime() - chronometer.base) / 1000
            //studyTimerModel.break_time = pauseOffset / 1000
            studyTimerModel.finish_time = System.currentTimeMillis()

            firestore?.collection("StudyTimer")?.
            document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.
            update(mapOf("finish_time" to studyTimerModel.finish_time))

            firestore?.collection("User")?.
            document("${auth?.currentUser?.uid}")?.
            update(mapOf("is_studying" to 0))

            chronometer.base = SystemClock.elapsedRealtime()
            pauseOffset = 0

            Toast.makeText(this, "Studying time has been saved.", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startTimer(v: View) {
        if (!running) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            running = true
            chronometer.setTextColor(Color.WHITE)

            studyTimerModel.start_time = System.currentTimeMillis()
            studyTimerModel.date = LocalDate.now().toString()

            firestore?.collection("StudyTimer")?.
            document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.set(studyTimerModel)

            firestore?.collection("User")?.
            document("${auth?.currentUser?.uid}")?.
            update(mapOf("is_studying" to studyTimerModel.start_time))
        }
    }

    fun breakTimer(v: View) {
        if (running) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            running = false;
            chronometer.setTextColor(Color.GRAY)
        }
    }
}