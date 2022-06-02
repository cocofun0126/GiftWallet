package com.example.giftwallet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import com.bumptech.glide.Glide
import com.example.giftwallet.databinding.IntroLayoutBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: IntroLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IntroLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

         Glide.with(this).load(R.raw.intro_cat).into(binding.ivIntroCat)

        var handler = Handler()
        handler.postDelayed({
            var intent = Intent(this, MainActivity::class.java)
            startActivity (intent)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}