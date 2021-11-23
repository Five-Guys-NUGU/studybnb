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

class NoteViewActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var storage : FirebaseStorage?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_view)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        var date : String? = null

        back_btn.setOnClickListener {
            finish()
        }

        firestore?.collection("Records")?.whereEqualTo("visit_date",date?.toLong())//1619271434933
            ?.get()?.addOnSuccessListener { documents ->
                for(doc in documents){
                    title_txt.text = doc?.data?.get("note_title").toString()
                    date_txt.text = SimpleDateFormat("yyyy.MM.dd").format(doc?.data?.get("visit_date"))
                    contents_txt.text = doc?.data?.get("note_title").toString()

                    var storageRef = storage?.reference?.child("images")?.child(doc?.data?.get("img_src").toString())
                    storageRef?.downloadUrl?.addOnSuccessListener { uri ->
                        Glide.with(applicationContext)
                            .load(uri)
                            .into(img_view)
                        Log.v("IMAGE","Success")
                    }?.addOnFailureListener { //이미지 로드 실패
                        Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
                        Log.v("IMAGE","failed")

                    }
                    //break
                }
            }

        delete_btn.setOnClickListener {
            val mAlertDialog = AlertDialog.Builder(this)
            mAlertDialog.setTitle("삭제")
            mAlertDialog.setMessage("삭제된 노트 기록은 복구할 수 없으며 데이터베이스에서 완전히 삭제됩니다. " +
                    "정말 삭제하겠습니까?")
            mAlertDialog.setPositiveButton("Yes") { dialog, id ->
                //perform some tasks here
                firestore.collection("Records")?.document("record_${auth.currentUser?.uid}_${date}")
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "삭제되었습니다.",Toast.LENGTH_LONG).show()
                    }
                val intent = Intent(this, NoteListActivity :: class.java )
                startActivity(intent)
                finish()
            }

            mAlertDialog.setNegativeButton("No") { dialog, id ->
                //perform som tasks here
            }
            mAlertDialog.show()
        }

    }
}