package com.example.studybnb

/**
 * @author Michael Sklors
 *
 * https://www.youtube.com/watch?v=RLnb4vVkftc&ab_channel=CodinginFlow
 */

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.studybnb.databinding.ActivityTimerBinding
import com.example.studybnb.model.StudyTimerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_note_view.*
import kotlinx.android.synthetic.main.activity_setting.back_btn
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.alert_popup.view.*
import java.time.LocalDate
import kotlin.reflect.typeOf

class HistoryTimerActivity : AppCompatActivity() {
    // Firebase setup
    private lateinit var auth: FirebaseAuth
    var firestore :FirebaseFirestore = FirebaseFirestore.getInstance()
    var studyTimerModel = StudyTimerModel()

    // Timer attributes
    private lateinit var chronometer: Chronometer
    private var pauseOffset: Long = 0
    private var running = false
    private lateinit var date : String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity_timer)
        auth = FirebaseAuth.getInstance()

        date= LocalDate.now().toString()
        var totalStudyTime : Long = 0
        var studyTime : Long
        // Receive value of subject and display it in the screen
        studyTimerModel.subject = "History"


        // Setup the timer
        chronometer = findViewById(R.id.chronometer)
        chronometer.format

        back_btn.setOnClickListener {
            showSettingPopup()
        }

        finish_timer_btn.setOnClickListener {
            showSettingPopup()
        }

    }

    override fun onBackPressed() {
        showSettingPopup()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startTimer(v: View) {
        if (!running) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            running = true
            chronometer.setTextColor(Color.DKGRAY)

            studyTimerModel.start_time = System.currentTimeMillis()
            studyTimerModel.date = LocalDate.now().toString()

            firestore?.collection("StudyTimer")?.
            document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.set(studyTimerModel)

            firestore?.collection("User")?.
            document("${auth?.currentUser?.uid}")?.
            update(mapOf("is_studying" to studyTimerModel.start_time))
        }
    }


    private fun showSettingPopup() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup, null)
        val textView: TextView = view.findViewById(R.id.textView)
        textView.text = "정말 공부를 종료하시겠습니까?"

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("잠깐!")
            .setPositiveButton("예") { dialog, which ->
                //studyTimerModel.UID = auth?.currentUser?.uid
                studyTimerModel.study_time = (SystemClock.elapsedRealtime() - chronometer.base) / 1000
                //studyTimerModel.break_time = pauseOffset / 1000
                studyTimerModel.finish_time = System.currentTimeMillis()

                firestore?.collection("StudyTimer")?.
                document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.
                update(mapOf("finish_time" to studyTimerModel.finish_time))

                firestore?.collection("StudyTimer")?.
                document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.
                update(mapOf("study_time" to studyTimerModel.study_time))


                firestore?.collection("User")?.
                document("${auth?.currentUser?.uid}")?.
                update(mapOf("is_studying" to 0))

                chronometer.base = SystemClock.elapsedRealtime()
                pauseOffset = 0

                var intent = Intent(this, SubjectActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNeutralButton("아니오", null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }

}