package com.example.studybnb.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.studybnb.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_note_view.*
import java.text.SimpleDateFormat

class HistoryNoteViewActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var storage : FirebaseStorage?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_note_view)
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
            myStartActivity(HistoryNoteListActivity::class.java)
            finish()
        }



        firestore?.collection("NoteTaking")
            ?.document("${auth?.currentUser?.uid}")
            ?.collection("History")?.whereEqualTo("date",date?.toLong())
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
                    }?.addOnFailureListener { //????????? ?????? ??????
                        Toast.makeText(applicationContext, "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        delete_btn.setOnClickListener {
            val mAlertDialog = AlertDialog.Builder(this)
            mAlertDialog.setTitle("??????")
            mAlertDialog.setMessage("????????? ?????? ????????? ????????? ??? ????????? ???????????????????????? ????????? ???????????????. " +
                    "?????? ??????????????????????")
            mAlertDialog.setPositiveButton("Yes") { dialog, id ->
                firestore?.collection("NoteTaking")
                    ?.document("${auth?.currentUser?.uid}")
                    ?.collection("History")
                    ?.document("${auth.currentUser?.uid}_history_${date}")
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "?????????????????????.",Toast.LENGTH_LONG).show()
                    }
                myStartActivity(HistoryNoteListActivity::class.java)
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //??????????????? ????????? ????????? ?????? ??????. ???????????? ????????? ??? ??????.
        startActivity(intent)
    }
}