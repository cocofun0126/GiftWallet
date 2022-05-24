package com.example.giftwallet

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giftwallet.databinding.ActivityAddGiftBinding
import com.example.giftwallet.giftlist.db.AppDatabase
import com.example.giftwallet.giftlist.db.BrandDao
import com.example.giftwallet.giftlist.db.GiftDao
import com.example.giftwallet.giftlist.db.GiftEntity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat


class AddGiftActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddGiftBinding
    lateinit var db : AppDatabase
    lateinit var giftDao : GiftDao
    lateinit var brandDao : BrandDao
    lateinit var giftimageurl : String
    lateinit var giftimageinfo : String

    lateinit var temp : Intent
    val DEFAULT_GALLERY_REQUEST_CODE : Int = 1

//    https://lab.cliel.com/283


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
        brandDao = db.getBrandDao()


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
        val data = temp


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
        var newuri : String = ""

        val imageUri = data.data
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

//        val extension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())

        if (imageUri != null) {
//            var img = data?.extras?.get("data") as Bitmap
            newuri = saveFile(RandomFileName(), "image/jpeg", bitmap).toString()
        }

        if(giftImportance== 0||giftTitle.isBlank()){
            Toast.makeText(this, "모든항목을 채워주세요.",Toast.LENGTH_SHORT).show()
        }else{
            Thread{
                giftDao.insertGift(GiftEntity(null,giftTitle,giftImportance,newuri, giftimageinfo))
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
//                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                getContentResolver().takePersistablePermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

//                if (data?.extras?.get("data") != null) {
//                    val img = data?.extras?.get("data") as Bitmap
//                }


                temp = data
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
//파일명 생성
    fun RandomFileName() : String
    {
        val fineName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        return fineName
    }

//    이미지 텍스트 추출

    private fun recognizeText(image: InputImage) {

        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->

//                텍스트 보자
                binding.edtInfo.setText(visionText.text)
                giftimageinfo = visionText.text
                var tmptxt_n = giftimageinfo.replace("\n","|").replace(" ","|") // newline |로 변환
                var tmptxt_split = tmptxt_n.split("|") //띄워쓰기 기준 변환
                var tmptxt_listset = tmptxt_split.toSet() //중복제거
                var gall_arraylist = tmptxt_listset.toCollection(ArrayList<String>())


//                val aa = ArrayAdapter(this,android.R.layout.simple_spinner_item,tmptxt_n)
//                binding.spinnerDetail.adapter = aa

//                입력값과 array 입력값 비교해야한다.
//                for (brand in tmptxt)

                val brandarray = resources.getStringArray(R.array.brand_array)
//                https://kkh0977.tistory.com/650
//                1. ArrayList : 코틀린에서 동적 배열 역할을 수행합니다
//                2. contains : 특정 값이 포함된 여부를 확인합니다
//                3. indexOf : 특정 데이터 인덱스 값을 확인합니다


                val arrayList = brandarray.toCollection(ArrayList<String>())//xml에 입력된 브랜드 array
//                gall_arraylist 갤러리에서 불러온 단어들



                //특정 데이터 인덱스값 확인 실시
                for (i in arrayList) {
                    var int_idx = gall_arraylist.indexOf(i)

                    println("[둘] 인덱스 : " + int_idx)

                }

//                tmptxt_listset.containsAll(R.array.brand_array)
                ArrayAdapter.createFromResource(
                    this,
                    R.array.brand_array,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    binding.spinnerBrand.adapter = adapter
                }


//                val aaa = ArrayAdapter(this,android.R.layout.simple_spinner_item,android.R.id.brand_array)
//                binding.spinnerBrand.adapter = aaa



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



    fun saveFile(fileName: String, mimeType: String, bitmap: Bitmap): Uri?
    {
        var CV = ContentValues()
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)

        if (uri != null) {
            var scriptor = contentResolver.openFileDescriptor(uri, "w")

            if (scriptor != null) {
                val fos = FileOutputStream(scriptor.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CV.clear()
                    CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, CV, null, null)
                }
            }
        }
        return uri;
    }
}