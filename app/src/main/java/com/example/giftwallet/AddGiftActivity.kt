package com.example.giftwallet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giftwallet.databinding.ActivityAddGiftBinding
import com.example.giftwallet.giftlist.db.AppDatabase
import com.example.giftwallet.giftlist.db.GiftDao
import com.example.giftwallet.giftlist.db.GiftEntity


class AddGiftActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddGiftBinding
    lateinit var db : AppDatabase
    lateinit var giftDao : GiftDao
    lateinit var giftimageurl : String

    val DEFAULT_GALLERY_REQUEST_CODE : Int = 1

//    var gifturl : String = binding.tvUrl.text.toString()

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
                giftDao.insertGift(GiftEntity(null,giftTitle,giftImportance, giftimageurl))
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

                // 이미지 URI를 가지고 하고 싶은거 하면 된다.
            }

            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}