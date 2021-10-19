package com.tejas.foodapp.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.tejas.foodapp.databinding.LayoutSplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity() {

    private lateinit var binding:LayoutSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSplashBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(binding.root)

        }
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){

               window.insetsController?.hide(WindowInsets.Type.statusBars())

           }else{
               @Suppress("DEPRECATION")
               window.setFlags(
                   WindowManager.LayoutParams.FLAG_FULLSCREEN,
                   WindowManager.LayoutParams.FLAG_FULLSCREEN
               )
           }





        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 1000)


    }
}