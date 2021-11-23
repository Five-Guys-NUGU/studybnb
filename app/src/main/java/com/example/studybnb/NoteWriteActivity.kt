package com.example.studybnb

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.nfc.cardemulation.HostNfcFService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.activity_info.back_btn
import kotlinx.android.synthetic.main.activity_note_write.*
import java.text.SimpleDateFormat
import java.util.*

class NoteWriteActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage : FirebaseStorage? = null
    var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    var imgFileName = "IMG_" + timeStamp + "_.jpg"
    var selectedPhotoUri: Uri? = null
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_write)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            }
        }

        back_btn.setOnClickListener {
            var intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
            finish()
        }

        img_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        cal_view.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)

            ).apply{}.show()

            val calString = cal.get(Calendar.YEAR).toString()+"년 "+(cal.get(Calendar.MONTH)+1).toString()+"월 "+cal.get(Calendar.DAY_OF_MONTH).toString()+"일"
            cal_view.text = calString

        }

        write_save_btn.setOnClickListener {
            var noteModel = NoteModel()
            noteModel.UID = auth?.currentUser?.uid
            noteModel.date = cal.timeInMillis
            noteModel.title = title_txt.text.toString()
            noteModel.contents=contents_txt.text.toString()


            firestore?.collection("Records")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.set(noteModel)

            uploadImageToFirebaseStorage()//사진 올리는 코드
            var intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //프로필 사진으로 선택한 이미지 보이게 하는 과정
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was ...
            //선택한 이미지가 보이게 하는 과정
            selectedPhotoUri = data.data
            //bitmap으로 우리가 선택한 이미지에 access하기.
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            if(bitmap != null){
                img_btn.setImageBitmap(bitmap)
                //img_btn.alpha = 0f
            }else{
                val icon = BitmapFactory.decodeResource(getResources(), R.drawable.photo_default)
                img_btn.setImageBitmap(icon)
                //img_btn.alpha = 0f
            }

        }
    }

    private fun uploadImageToFirebaseStorage() {

        val ref = FirebaseStorage.getInstance().getReference("/images/$imgFileName")

        if(selectedPhotoUri == null){
            val uri = Uri.parse("android.resource://com.example.studybnb/drawable/photo_default")
            ref.putFile(uri)//selected Photo된거 uri를 file형태로 ref에 넣음.
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        firestore?.collection("Records")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)

                    }
                }
        }else{
            ref.putFile(selectedPhotoUri!!) //selected Photo된거 uri를 file형태로 ref에 넣음.
                //이미지 업로드
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        firestore?.collection("Records")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)
                    }
                }
                .addOnFailureListener{ //실패하면
                }
        }
    }
}