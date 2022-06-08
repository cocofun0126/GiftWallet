package com.example.giftwallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.giftwallet.databinding.ActivityImageZoomBinding
import com.example.giftwallet.giftlist.db.GiftDTO
import com.example.giftwallet.giftlist.db.GiftEntity

lateinit var binding : ActivityImageZoomBinding
class ImageZoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)





        binding = ActivityImageZoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val data = intent.getParcelableExtra<GiftDTO>("imageurl")
        val data = intent.getStringExtra(("imageurl"))
        if (data != null) {
            binding.ivZoomGift.setImageURI(data.toUri())
        }

//        binding.detailTitle.text = data!!.title

    }
}