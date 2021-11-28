package com.example.studybnb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.studybnb.model.UserInfoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firestore :FirebaseFirestore = FirebaseFirestore.getInstance()
    private var sex : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        auth = FirebaseAuth.getInstance()


        back_btn.setOnClickListener {
            var intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()
        }
        radio_men.setOnClickListener {
            sex="남자"
        }

        radio_women.setOnClickListener {
            sex="여자"
        }

        save_btn.setOnClickListener {
            var userInfoModel = UserInfoModel()
            userInfoModel.UID = auth?.currentUser?.uid
            userInfoModel.name = name_txt.text.toString()
            userInfoModel.sex = sex
            userInfoModel.age = (age_txt.text.toString()).toLong()
            userInfoModel.gpa = gpa_txt.text.toString()
            userInfoModel.intersts = interest_txt.text.toString()

            firestore?.collection("User")?.document("info_${auth?.currentUser?.uid}")?.set(userInfoModel)

            Toast.makeText(this, "회원 정보가 등록되었습니다.", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
}