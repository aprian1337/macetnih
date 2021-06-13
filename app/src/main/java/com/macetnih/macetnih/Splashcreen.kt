package com.macetnih.macetnih

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class Splashcreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashcreen)
        val time = 4000L
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this@Splashcreen, MainActivity::class.java).apply {
                startActivity(this)
            }
        }, time)
    }
}