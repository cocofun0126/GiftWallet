package com.example.giftwallet

import android.app.Activity
import android.content.Intent
import android.content.Context

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giftwallet.databinding.ActivityAddGiftBinding
import com.example.giftwallet.giftlist.db.AppDatabase
import com.example.giftwallet.giftlist.db.GiftDao
import com.example.giftwallet.giftlist.db.GiftEntity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class AddGiftActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddGiftBinding
    lateinit var db : AppDatabase
    lateinit var giftDao : GiftDao
    lateinit var giftimageurl : String
    lateinit var giftimageinfo : String

    val DEFAULT_GALLERY_REQUEST_CODE : Int = 1
    // When using Latin script library
//    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

//    // When using Korean script library
//    val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddGiftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        giftDao = db.getGiftDao()

        binding.btnCompletion.setOnClickListener{
            insertGift()
        }

//        갤러리 연동
        binding.btnGalary.setOnClickListener{
            getImageFromGalary()
        }

    }

    private fun insertGift(){
        val giftTitle = binding.edtTitle.text.toString()
        var giftImportance = binding.radioGroup.checkedRadioButtonId
        var giftInfo = binding.edtInfo


        when(giftImportance){
            R.id.btn_high->{
                giftImportance = 3
            }
            R.id.btn_middle->{
                giftImportance = 2
            }
            R.id.btn_low->{
                giftImportance = 1
            }
            else->{
                giftImportance = 0
            }
        }

        if(giftImportance== 0||giftTitle.isBlank()){
            Toast.makeText(this, "모든항목을 채워주세요.",Toast.LENGTH_SHORT).show()
        }else{
            Thread{
                giftDao.insertGift(GiftEntity(null,giftTitle,giftImportance, giftimageurl, giftimageinfo))
                runOnUiThread{
                    Toast.makeText(this,"추가되었습니다",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        }
    }
    private fun getImageFromGalary(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, DEFAULT_GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var giftid : Int = 0
        var gifturl : String = ""

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            DEFAULT_GALLERY_REQUEST_CODE -> {
                data?:return

                val uri = data.data as Uri


                giftimageurl = uri.toString()
                binding.tvUrl.text = uri.toString()
                binding.ivAddgift.setImageURI(uri)


                binding.edtTitle.text.toString()

                Toast.makeText(this, "사진URI$binding.tvUrl.text.", Toast.LENGTH_SHORT).show()


//                이미지 텍스트 인식
                imageFromPath(this,uri)
            }

            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    이미지 텍스트 추출

    private fun recognizeText(image: InputImage) {

        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->

//                텍스트 보자
                binding.edtInfo.setText(visionText.text)
                giftimageinfo = visionText.text
                var tmptxt = visionText.text.lines()

//                ArrayAdapter.createFromResource(
//                    this,
//                    android.R.layout.simple_spinner_item,
//                    tmptxt
//                ).also { adapter ->
//                    // Specify the layout to use when the list of choices appears
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    // Apply the adapter to the spinner
//                    binding.spinner.adapter = adapter
//                }

                val aa = ArrayAdapter(this,android.R.layout.simple_spinner_item,tmptxt)
                binding.spinner.adapter = aa



                for (block in visionText.textBlocks) {
//                    block.boundingBox?.set(31,19,59,8)
                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text

                    for (line in block.lines) {
                        line.toString()
                        val lineText = line.text
                        val lineCornerPoints = line.cornerPoints
                        val lineFrame = line.boundingBox

                        for (element in line.elements) {
                            element.toString()
                            val elementText = element.text
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox

                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
        // [END run_detector]
//        processTextBlock(result)
    }

//    private fun processTextBlock(result: Text) {
//        // [START mlkit_process_text_block]
//        val resultText = result.text
//        for (block in result.textBlocks) {
//            val blockText = block.text
//            val blockCornerPoints = block.cornerPoints
//            val blockFrame = block.boundingBox
//            for (line in block.lines) {
//                val lineText = line.text
//                val lineCornerPoints = line.cornerPoints
//                val lineFrame = line.boundingBox
//                for (element in line.elements) {
//                    val elementText = element.text
//                    val elementCornerPoints = element.cornerPoints
//                    val elementFrame = element.boundingBox
//                }
//            }
//        }
//        // [END mlkit_process_text_block]
//    }

//    private fun getTextRecognizer(): TextRecognizer {
//        // [START mlkit_local_doc_recognizer]
//        return TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//        // [END mlkit_local_doc_recognizer]
//    }

    private fun imageFromPath(context: Context, uri: Uri) {
        // [START image_from_path]
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
            recognizeText(image)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // [END image_from_path]
    }


}