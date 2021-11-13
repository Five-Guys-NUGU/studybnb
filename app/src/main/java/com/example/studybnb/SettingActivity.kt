package com.example.studybnb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        auth = FirebaseAuth.getInstance()
        email_txt.setText(auth.currentUser?.email)

        signOut_btn.setOnClickListener {
            auth.signOut()
            Toast.makeText(this,"로그아웃 완료", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        withdrawal_btn.setOnClickListener {
            val mAlertDialog = AlertDialog.Builder(this)
            mAlertDialog.setTitle("회원탈퇴")
            mAlertDialog.setMessage("삭제된 계정은 복구할 수 없으며 해당 계정의 게시물과 정보는 완전히 삭제됩니다. " +
                    "정말 탈퇴하겠습니까?")
            mAlertDialog.setPositiveButton("Yes") { dialog, id ->
                //perform some tasks here
                deleteId()
                auth.signOut()
                val intent = Intent(this, LoginActivity :: class.java )//로그인화면으로 넘어감
                startActivity(intent)
                finish()
            }
            mAlertDialog.setNegativeButton("No") { dialog, id ->
                //perform som tasks here
            }
            mAlertDialog.show()
        }


        contact_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("plain/Text")
            val string = "hanyangfiveguys@gmail.com"
            val address = arrayOf(string)
            intent.putExtra(Intent.EXTRA_EMAIL, address)
            intent.putExtra(Intent.EXTRA_SUBJECT, "StudyBNB 문의")
            intent.putExtra(Intent.EXTRA_TEXT, "문의 내용을 작성하세요")

            startActivity(intent)
        }

        back_btn.setOnClickListener {
            finish()
        }
    }

    fun deleteId() {
        auth?.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successful membership withdrawal", Toast.LENGTH_LONG).show()
                }
            }
    }
}