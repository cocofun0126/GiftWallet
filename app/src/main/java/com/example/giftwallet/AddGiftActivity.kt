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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giftwallet.databinding.ActivityAddGiftBinding
import com.example.giftwallet.giftlist.db.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import com.example.giftwallet.giftlist.db.BrandDao


class AddGiftActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddGiftBinding
    lateinit var db : AppDatabase
    lateinit var giftDao : GiftDao
    lateinit var brandDao : BrandDao
    lateinit var giftimageurl : String
    lateinit var giftimageinfo : String

    lateinit var temp : Intent
    val DEFAULT_GALLERY_REQUEST_CODE : Int = 1


    private var brandList: ArrayList<String> = arrayListOf<String>()
//    private var brandList: ArrayList<BrandEntity> = arrayListOf<BrandEntity>()

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
        binding.ivAddgift.setOnClickListener{
            getImageFromGalary()
        }
//        binding.ivAddgift.setOnLongClickListener{
//
////            zoomOut()
//
//        }

    }

    private fun insertGift(){
//        val giftTitle = binding.edtTitle.text.toString() //설명
        var giftInfo = binding.edtInfo.toString() //내용(이미지 내용)
        val giftBrand = binding.spinnerBrand.selectedItem.toString() // 브랜드명
        val giftValidDate = binding.edtValidDate.text.toString() //유효기간 설정
        var giftUseYn = binding.radioGroup.checkedRadioButtonId //쿠폰 사용여부

        val data = temp

        var savedUri : String = ""

        val imageUri = data.data
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

        if (imageUri != null) {
//            var img = data?.extras?.get("data") as Bitmap
            savedUri = saveFile(RandomFileName(), "image/jpeg", bitmap).toString()
        }

//        사용여부
        when(giftUseYn){
            R.id.btn_y->{
                giftUseYn = 0
            }
            R.id.btn_n->{
                giftUseYn = 1
            }
        }

        if(giftInfo.isBlank()){
            Toast.makeText(this, "모든항목을 채워주세요.",Toast.LENGTH_SHORT).show()
        }else{
            Thread{
//                giftDao.insertGift(GiftEntity(null,giftTitle,savedUri, giftimageinfo, giftBrand, giftValidDate))
                giftDao.insertGift(GiftEntity(null,savedUri, giftimageinfo, giftBrand, giftValidDate, giftUseYn))
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
//                binding.edtTitle.text.toString()

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
                var gall_arraylist = tmptxt_listset.toCollection(ArrayList<String>()) //collection으로 고유값 엔터라인 분기 arraylist

//                정규식 참고자료
//                https://codechacha.com/ko/kotlin-how-to-use-regex/

//--------------유효기간 정규식 생성 START--------------
                val valDateRegex1 = Regex("..년.*월.*일") // 2022년12월31일 -> 22년12월31일
                val valDateRegex2 = Regex("..\\-..\\-..")//2022-12-31 -> 22-12-31
                val valDateRegex3 = Regex("..\\...\\...")//2022.12.31 -> 22.12.31

//                문자열 띄워쓰기 제거
                var getDateStr = giftimageinfo.replace("\n","").replace(" ","")
//                string에서 일자추출 정규식 적용 및 arraylist변환 필요!!!!!!!!!!!!!!!

                val matchResult1: MatchResult? = valDateRegex1.find(getDateStr)
                println("match value1: ${matchResult1?.value}")

                val matchResult2: MatchResult? = valDateRegex2.find(getDateStr)
                println("match value2: ${matchResult2?.value}")

                val matchResult3: MatchResult? = valDateRegex3.find(getDateStr)
                println("match value3: ${matchResult3?.value}")

                if (matchResult1?.value != null){ //22년12월31일 -> 20221231
                    binding.edtValidDate.setText("20"+matchResult1?.value.toString()
                        .replace("년","")
                        .replace("월","")
                        .replace("일",""))
                }else if(matchResult2?.value != null){//22-12-31 -> 20221231
                    binding.edtValidDate.setText("20"+matchResult2?.value.toString()
                        .replace("-",""))
                }else if(matchResult3?.value != null){//22.12.31 -> 20221231
                    binding.edtValidDate.setText("20"+matchResult3?.value.toString()
                        .replace(".",""))
                }else{
                    binding.edtValidDate.setText("")
                }

//              날짜 유효성 체크
                try {
                    val dateFormatParser = SimpleDateFormat("yyyy/MM/dd") //검증할 날짜 포맷 설정
                    dateFormatParser.isLenient = false //false일경우 처리시 입력한 값이 잘못된 형식일 시 오류가 발생
                    dateFormatParser.parse(binding.edtValidDate.toString()) //대상 값 포맷에 적용되는지 확인
                    true
                } catch (e: Exception) {
                    Toast.makeText(this, "날짜를 확인해 주세요",Toast.LENGTH_SHORT).show()
                    false
                }


//--------------유효기간 정규식 생성 END--------------

                getAllBrandList(gall_arraylist) // brandList에 값 전달



//                val brandarray = resources.getStringArray(R.array.brand_array)
//                val brandarray = resources.getStringArray(R.array.brand_array)
//                https://kkh0977.tistory.com/650
//                1. ArrayList : 코틀린에서 동적 배열 역할을 수행합니다
//                2. contains : 특정 값이 포함된 여부를 확인합니다
//                3. indexOf : 특정 데이터 인덱스 값을 확인합니다


//                val arrayList = brandarray.toCollection(ArrayList<String>())//xml에 입력된 브랜드 array
//                gall_arraylist 갤러리에서 불러온 단어들

//                tmptxt_listset.containsAll(R.array.brand_array)

//                ArrayAdapter.createFromResource(
//                    this,
//                    R.array.brand_array,
//                    android.R.layout.simple_spinner_item
//                ).also { adapter ->
//                    // Specify the layout to use when the list of choices appears
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    // Apply the adapter to the spinner
//                    binding.spinnerBrand.adapter = adapter
//                }











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


    private fun getAllBrandList(gall_arraylist:ArrayList<String>) {
        Thread {
            brandList = ArrayList(brandDao.getAllBrand())
            setBrandSpinner(gall_arraylist)
        }.start()
    }

    private fun setBrandSpinner(gall_arraylist:ArrayList<String>) {
        runOnUiThread {
            val adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item, brandList
            )
            binding.spinnerBrand.adapter = adapter

            //특정 데이터 인덱스값 확인 실시
            for (i in brandList) {
                if(gall_arraylist.contains(i) == true) {
//              인덱스 지정해놓기
                    binding.spinnerBrand.setSelection(brandList.indexOf(i))
                    break
                }
                else{
//                        로직 개선의 여지 -> 매핑된 값 없으면 마지막(기타)로 매핑 한번만 처리
                    binding.spinnerBrand.setSelection(brandList.lastIndex)
                }
            }
//            if(gall_arraylist.contains("사용완료") == true or gall_arraylist.contains("사용") == true or gall_arraylist.contains("완료") == true){
            if(gall_arraylist.contains("사용완료") == true) {
                binding.radioGroup.check(binding.btnY.id)
            }else if((gall_arraylist.contains("사용") == true) and (gall_arraylist.contains("완료") == true)) {
                binding.radioGroup.check(binding.btnY.id)
            }else if(gall_arraylist.contains("미사용") == true) {
                binding.radioGroup.check(binding.btnN.id)
            }else{
                binding.radioGroup.check(binding.btnN.id)
            }
        }
    }
}


