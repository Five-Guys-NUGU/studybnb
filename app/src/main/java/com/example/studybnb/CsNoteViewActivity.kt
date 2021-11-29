package com.example.studybnb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_note_view.*
import java.text.SimpleDateFormat

class CsNoteViewActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var storage : FirebaseStorage?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cs_note_view)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        var date : String? = null

        if(intent.hasExtra("date")){
            date = intent.getStringExtra("date")
        }
        else{
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
        }


        back_btn.setOnClickListener {
            myStartActivity(CsNoteListActivity::class.java)
            finish()
        }

//        firestore?.collection("Records")?.whereEqualTo("date",date?.toLong())
//        firestore?.collection("NoteTaking : Toeic")?.document("${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)

//        firestore?.collection("User")?.document("Study")?.collection("NoteTaking")?.whereEqualTo("date",date?.toLong())
//        firestore?.collection("NoteTaking")?.document("Subjects")?.collection("CS")?.document("${auth?.currentUser?.uid}_cs_${cal.timeInMillis}")?.update("img_src",imgFileName)

        firestore?.collection("NoteTaking")?.document("Subjects")?.collection("CS")?.whereEqualTo("date",date?.toLong())
            ?.get()?.addOnSuccessListener { documents ->
                for(doc in documents){
                    title_view.text = doc?.data?.get("title").toString()
                    date_view.text = SimpleDateFormat("yyyy.MM.dd").format(doc?.data?.get("date"))
                    contents_view.text = doc?.data?.get("contents").toString()

                    var storageRef = storage?.reference?.child("images")?.child(doc?.data?.get("img_src").toString())

                    storageRef?.downloadUrl?.addOnSuccessListener { uri ->
                        Glide.with(applicationContext)
                            .load(uri)
                            .into(img_view)
                    }?.addOnFailureListener { //이미지 로드 실패
                        Toast.makeText(applicationContext, "이미지 로드에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        delete_btn.setOnClickListener {
            val mAlertDialog = AlertDialog.Builder(this)
            mAlertDialog.setTitle("삭제")
            mAlertDialog.setMessage("삭제된 노트 기록은 복구할 수 없으며 데이터베이스에서 완전히 삭제됩니다. " +
                    "정말 삭제하겠습니까?")
            mAlertDialog.setPositiveButton("Yes") { dialog, id ->
                //perform some tasks here


//                firestore?.collection("User")?.document("Study")?.collection("NoteTaking")?.document("record_${auth.currentUser?.uid}_${date}")
                firestore?.collection("NoteTaking")?.document("Subjects")?.collection("CS")?.document("${auth.currentUser?.uid}_cs_${date}")
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "삭제되었습니다.",Toast.LENGTH_LONG).show()
                    }
                myStartActivity(CsNoteListActivity::class.java)
                finish()


            }

            mAlertDialog.setNegativeButton("No") { dialog, id ->
                //perform som tasks here
            }
            mAlertDialog.show()
        }

    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }
}