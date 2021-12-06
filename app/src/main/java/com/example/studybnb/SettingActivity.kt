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

        info_btn.setOnClickListener{
            myStartActivity(InfoActivity :: class.java)
        }

        signOut_btn.setOnClickListener {
            auth.signOut()
            Toast.makeText(this,"로그아웃 완료", Toast.LENGTH_SHORT).show()
            myStartActivity(LoginActivity :: class.java)
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
                myStartActivity(LoginActivity :: class.java)
                finish()
            }
            mAlertDialog.setNegativeButton("No") { dialog, id ->
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

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }
}