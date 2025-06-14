package com.example.bytongue.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bytongue.R
import com.example.bytongue.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val score = intent.extras?.get("result")
        val sizeQuestions = intent.extras?.get("sizeQuestions")
        setContentView(binding.root)

        binding.textView.text = "Você acertou ${score} de ${sizeQuestions} questões"

        binding.btnRestart.setOnClickListener{
            finish()
        }
    }
}