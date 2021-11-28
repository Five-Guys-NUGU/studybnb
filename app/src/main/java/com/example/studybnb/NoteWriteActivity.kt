package com.example.studybnb

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.DatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_info.back_btn
import kotlinx.android.synthetic.main.activity_note_write.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.studybnb.model.NoteModel


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
            myStartActivity(NoteListActivity::class.java)
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
            noteModel.title = title_view.text.toString()
            noteModel.contents=contents_view.text.toString()

            firestore?.collection("NoteTaking")?.document("${auth?.currentUser?.uid}_toeic_${cal.timeInMillis}")?.set(noteModel)


//            firestore?.collection("Records")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.set(noteModel)

            uploadImageToFirebaseStorage()//사진 올리는 코드
            myStartActivity(NoteListActivity::class.java)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //프로필 사진으로 선택한 이미지가 보임
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            if(bitmap != null){
                img_btn.setImageBitmap(bitmap)
            }else{
                val icon = BitmapFactory.decodeResource(getResources(), R.drawable.photo_default)
                img_btn.setImageBitmap(icon)
            }

            //Create a FirebaseVisionImage object from your image/bitmap.
            val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)
            val firebaseVision = FirebaseVision.getInstance()
            val firebaseVisionTextRecognizer = firebaseVision.onDeviceTextRecognizer

            //Process the Image
            val task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage)

            task.addOnSuccessListener { firebaseVisionText: FirebaseVisionText ->
                //Set recognized text from image in our TextView
                val text = firebaseVisionText.text
                contents_view!!.text = text
        }
    }
    }
    private fun uploadImageToFirebaseStorage() {

        val ref = FirebaseStorage.getInstance().getReference("/images/$imgFileName")

        if(selectedPhotoUri == null){
            val uri = Uri.parse("android.resource://com.example.studybnb/drawable/photo_default")
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        firestore?.collection("NoteTaking")?.document("${auth?.currentUser?.uid}_toeic_${cal.timeInMillis}")?.update("img_src",imgFileName)

//                        firestore?.collection("User")?.document("Study")?.collection("NoteTaking")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)

//                        firestore?.collection("Records")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)

                    }
                }
        }else{
            ref.putFile(selectedPhotoUri!!) //selected Photo된거 uri를 file형태로 ref에 넣음.
                //이미지 업로드
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        firestore?.collection("NoteTaking")?.document("${auth?.currentUser?.uid}_toeic_${cal.timeInMillis}")?.update("img_src",imgFileName)

//                        firestore?.collection("Records")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)
//                        firestore?.collection("User")?.document("Study")?.collection("NoteTaking")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)

                    }
                }
                .addOnFailureListener{ //실패하면
                }
        }
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티가 여러게 쌓이는 것을 방지. 뒤로가기 누르면 앱 종료.
        startActivity(intent)
    }


}