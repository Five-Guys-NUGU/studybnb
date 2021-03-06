package com.example.studybnb.toeic

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
import android.view.LayoutInflater
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.studybnb.R
import com.example.studybnb.SubjectActivity
import com.example.studybnb.databinding.ActivityTimerBinding
import com.example.studybnb.model.StudyTimerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_setting.back_btn
import kotlinx.android.synthetic.main.activity_timer.*
import java.time.LocalDate

class TimerActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_timer)
        auth = FirebaseAuth.getInstance()

        date= LocalDate.now().toString()
        var totalStudyTime : Long = 0
        var studyTime : Long

        studyTimerModel.subject = "Toeic"
        val binding: ActivityTimerBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_timer
        )
        binding.studyTimerModel = studyTimerModel

        // Setup the timer
        chronometer = findViewById(R.id.chronometer)
        chronometer.format

        chronometer.setOnChronometerTickListener(Chronometer.OnChronometerTickListener { chronometer ->
            val time = SystemClock.elapsedRealtime() - chronometer.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            val t =
                (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
            chronometer.text = t
        })
        chronometer.setBase(SystemClock.elapsedRealtime())
        chronometer.setText("00:00:00")

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
            document("${auth?.currentUser?.uid}")?.
            collection("${date}")?.
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
        textView.text = "?????? ????????? ?????????????????????????"

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("??????!")
            .setPositiveButton("???") { dialog, which ->
                //studyTimerModel.UID = auth?.currentUser?.uid
                studyTimerModel.study_time = (SystemClock.elapsedRealtime() - chronometer.base) / 1000
                //studyTimerModel.break_time = pauseOffset / 1000
                studyTimerModel.finish_time = System.currentTimeMillis()

                firestore?.collection("StudyTimer")?.
                document("${auth?.currentUser?.uid}")?.
                collection("${date}")?.
                document("${auth?.currentUser?.uid+"_"+studyTimerModel.start_time}")?.
                update(mapOf("finish_time" to studyTimerModel.finish_time))

                firestore?.collection("StudyTimer")?.
                document("${auth?.currentUser?.uid}")?.
                collection("${date}")?.
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
            .setNeutralButton("?????????", null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }

}