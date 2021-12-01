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
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.studybnb.databinding.ActivityTimerBinding
import com.example.studybnb.model.StudyTimerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_note_view.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.android.synthetic.main.activity_setting.back_btn
import kotlinx.android.synthetic.main.activity_timer.*
import java.text.SimpleDateFormat

class TimerActivity : AppCompatActivity() {
    // Firebase setup
    private lateinit var auth: FirebaseAuth
    var firestore :FirebaseFirestore = FirebaseFirestore.getInstance()
    var studyTimerModel = StudyTimerModel()

    // Timer attributes
    private lateinit var chronometer: Chronometer
    private var pauseOffset: Long = 0
    private var running = false
    var date : String? = null
    var studyTime : Long?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_timer)
        this.auth = FirebaseAuth.getInstance()
        this.date =LocalDate.now().toString()
        Log.e(this.date, "dateToday")


        this.firestore?.collection("StudyTimer")?.document("Subjects")
            ?.collection("Toeic")?.whereEqualTo("date", this.date)
            ?.get()?.addOnSuccessListener { documents ->
                for(doc in documents){
//                    studyTime!! += doc?.data?.get("study_time").toString().toLong()
                    Log.e(studyTime.toString(), "studyTime")
                }
            }

        this.study_time.text = this.studyTime.toString()

        // Receive value of subject and display it in the screen
        val extraSubject = this.intent?.extras?.getString("subject").toString()
        this.studyTimerModel.subject = extraSubject
        val binding: ActivityTimerBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_timer)
        binding.studyTimerModel = this.studyTimerModel


        // Setup the timer
        this.chronometer = this.findViewById(R.id.chronometer)
        this.chronometer.format


        this.back_btn.setOnClickListener {
            this.finish()
        }

        this.finish_timer_btn.setOnClickListener {
            //studyTimerModel.UID = auth?.currentUser?.uid
            this.studyTimerModel.study_time = (SystemClock.elapsedRealtime() - this.chronometer.base) / 1000
            //studyTimerModel.break_time = pauseOffset / 1000
            this.studyTimerModel.finish_time = System.currentTimeMillis()


//            firestore?.collection("StudyTimer")?.
//            document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.
            this.firestore?.collection("StudyTimer")?.document("Subjects")?.collection("Toeic")?.
            document("${this.auth?.currentUser?.uid+"_"+ this.studyTimerModel.start_time}")?.
            update(mapOf("finish_time" to this.studyTimerModel.finish_time))

            this.firestore?.collection("StudyTimer")?.document("Subjects")?.collection("Toeic")?.
            document("${this.auth?.currentUser?.uid+"_"+ this.studyTimerModel.start_time}")?.
            update(mapOf("study_time" to this.studyTimerModel.study_time))

            this.firestore?.collection("User")?.
            document("${this.auth?.currentUser?.uid}")?.
            update(mapOf("is_studying" to 0))

            this.chronometer.base = SystemClock.elapsedRealtime()
            this.pauseOffset = 0

            Toast.makeText(this, "Studying time has been saved.", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startTimer(v: View) {
        if (!this.running) {
            this.chronometer.base = SystemClock.elapsedRealtime() - this.pauseOffset
            this.chronometer.start()
            this.running = true
            this.chronometer.setTextColor(Color.WHITE)

            this.studyTimerModel.start_time = System.currentTimeMillis()
            this.studyTimerModel.date = LocalDate.now().toString()

            this.firestore?.collection("StudyTimer")?.document("Subjects")?.collection("Toeic")?.
            document("${this.auth?.currentUser?.uid+"_"+ this.studyTimerModel.start_time}")?.set(
                this.studyTimerModel
            )

//            firestore?.collection("StudyTimer")?.
//            document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.set(studyTimerModel)

            this.firestore?.collection("User")?.
            document("${this.auth?.currentUser?.uid}")?.
            update(mapOf("is_studying" to this.studyTimerModel.start_time))
        }
    }

    fun breakTimer(v: View) {
        if (this.running) {
            this.chronometer.stop()
            this.pauseOffset = SystemClock.elapsedRealtime() - this.chronometer.base
            this.running = false;
            this.chronometer.setTextColor(Color.GRAY)
        }
    }
}