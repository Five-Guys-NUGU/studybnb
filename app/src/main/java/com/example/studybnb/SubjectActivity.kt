package com.example.studybnb

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.studybnb.cs.CsTimerActivity
import com.example.studybnb.history.HistoryTimerActivity
import com.example.studybnb.toeic.TimerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.activity_subject.back_btn
import java.time.LocalDate

class SubjectActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var date: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)
        var totalStudyTime: Long = 0
        var historyStudyTime: Long = 0
        var toeicStudyTime: Long = 0
        var csStudyTime: Long = 0
        var studyTime: Long

        auth = FirebaseAuth.getInstance()
        date = LocalDate.now().toString()

        today_date.text=date

        firestore?.collection("StudyTimer")
            ?.document("${auth?.currentUser?.uid}")
            ?.collection("${date}")
            ?.whereEqualTo("date", date)
            ?.get()?.addOnSuccessListener { documents ->
                for (doc in documents) {
                    if (doc?.data?.get("study_time") != null)//널 값이면 제외
                    {
                        studyTime = doc?.data?.get("study_time").toString().toLong()
                        totalStudyTime += studyTime
                    }

                }
                Log.e(totalStudyTime.toString(), "studyTime")
                var hours = totalStudyTime / 3600;
                var minutes = (totalStudyTime % 3600) / 60;
                var seconds = totalStudyTime % 60;

                var timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                total_time.text=timeString
            }

        firestore?.collection("StudyTimer")
            ?.document("${auth?.currentUser?.uid}")
            ?.collection("${date}")
            ?.whereEqualTo("subject", "History")
            ?.whereEqualTo("date", date)
            ?.get()?.addOnSuccessListener { documents ->
                for (doc in documents) {
                    if (doc?.data?.get("study_time") != null)//널 값이면 제외
                    {
                        studyTime = doc?.data?.get("study_time").toString().toLong()
                        historyStudyTime += studyTime
                    }

                }
                Log.e(historyStudyTime.toString(), "studyTime")
                var hours = historyStudyTime / 3600;
                var minutes = (historyStudyTime % 3600) / 60;
                var seconds = historyStudyTime % 60;

                var timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                history_study_time.text=timeString
            }

        firestore?.collection("StudyTimer")
            ?.document("${auth?.currentUser?.uid}")
            ?.collection("${date}")
            ?.whereEqualTo("subject", "Toeic")
            ?.whereEqualTo("date", date)
            ?.get()?.addOnSuccessListener { documents ->
                for (doc in documents) {
                    if (doc?.data?.get("study_time") != null)//널 값이면 제외
                    {
                        studyTime = doc?.data?.get("study_time").toString().toLong()
                        toeicStudyTime += studyTime
                    }

                }
                Log.e(toeicStudyTime.toString(), "studyTime")
                var hours = toeicStudyTime / 3600;
                var minutes = (toeicStudyTime % 3600) / 60;
                var seconds = toeicStudyTime % 60;

                var timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                toeic_study_time.text=timeString
            }

        firestore?.collection("StudyTimer")
            ?.document("${auth?.currentUser?.uid}")
            ?.collection("${date}")
            ?.whereEqualTo("subject", "CS")
            ?.whereEqualTo("date", date)
            ?.get()?.addOnSuccessListener { documents ->
                for (doc in documents) {
                    if (doc?.data?.get("study_time") != null)//널 값이면 제외
                    {
                        studyTime = doc?.data?.get("study_time").toString().toLong()
                        csStudyTime += studyTime
                    }

                }
                Log.e(csStudyTime.toString(), "studyTime")
                var hours = csStudyTime / 3600;
                var minutes = (csStudyTime % 3600) / 60;
                var seconds = csStudyTime % 60;

                var timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                cs_study_time.text=timeString
            }

        back_btn.setOnClickListener {
            myStartActivity(MainActivity::class.java)
        }

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
    override fun onBackPressed() {
        myStartActivity(MainActivity::class.java)
        finish()
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }
}
