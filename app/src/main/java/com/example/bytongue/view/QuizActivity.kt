package com.example.bytongue.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bytongue.R
import com.example.bytongue.api.Endpoint
import com.example.bytongue.databinding.ActivityQuizBinding
import com.example.bytongue.manager.SessionManager
import com.example.bytongue.model.QuestionResponse
import com.example.bytongue.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizBinding
    private lateinit var sessionManager: SessionManager
    private var questionList: List<QuestionResponse>? = null
    private var currentQuestion = 0
    private var scorePlayer = 0
    private var isclickBtn = false
    private var valueChoose = ""
    private var btnclick: Button? = null
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        sessionManager = SessionManager(this)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getQuestions()

        binding.imageBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (isclickBtn) {
                isclickBtn = false

                val current = questionList?.getOrNull(currentQuestion)
                val correctAnswer = current?.options?.getOrNull(current.correct)

                if(correctAnswer == null) {
                    Toast.makeText(applicationContext, "Erro ao carregar questões", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                if (valueChoose != correctAnswer) {
                    btnclick?.setBackgroundResource(R.drawable.background_btn_wrong)

                } else {
                    btnclick?.setBackgroundResource(R.drawable.background_btn_correct)
                    scorePlayer++
                }

                Handler().postDelayed({
                    if (currentQuestion < (questionList?.size ?: 0) - 1) {
                        currentQuestion++
                        showQuestion()
                        resetButtons()
                    } else {
                        val intent = Intent(
                            this@QuizActivity,
                            ResultActivity::class.java
                        )
                        intent.putExtra("result", scorePlayer)
                        intent.putExtra("sizeQuestions", questionList?.size)
                        startActivity(intent)
                        finish()
                    }
                }, 2000)
            } else {
                Toast.makeText(applicationContext, "Você deve escolher uma opção!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getQuestions() {

        showLoading()

        val userLevel = sessionManager.getLevel().toString().toInt()
        val userLanguage = sessionManager.getLanguage()

        val retrofitClient = NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/", this)
        val endpoint = retrofitClient.create(Endpoint::class.java)
        val callback = endpoint.getQuestions(userLevel, "pt-BR", userLanguage)

        callback.enqueue(object : Callback<List<QuestionResponse>> {
            override fun onResponse(
                call: Call<List<QuestionResponse>>,
                response: Response<List<QuestionResponse>>
            ) {

                hideLoading()

                if (response.isSuccessful && response.body() != null) {
                    questionList = response.body()
                    showQuestion()
                } else {
                    Toast.makeText(applicationContext, "Erro ao carregar as questões", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<QuestionResponse>>, t: Throwable) {
                hideLoading()
                Toast.makeText(applicationContext, "Falha: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showQuestion() {
        val question = questionList?.getOrNull(currentQuestion)

        binding.cptQuestion.text = "${currentQuestion + 1}/${questionList?.size ?: 0}"
        binding.textQuestion.text = question?.question ?: "Pergunta indisponível"

        val options = question?.options ?: emptyList()

        binding.btnChoose1.text = options.getOrNull(0) ?: ""
        binding.btnChoose2.text = options.getOrNull(1) ?: ""
        binding.btnChoose3.text = options.getOrNull(2) ?: ""
        binding.btnChoose4.text = options.getOrNull(3) ?: ""
        binding.btnChoose5.text = options.getOrNull(4) ?: ""
    }

    fun clickChoose(view: View) {
        if (isclickBtn) {
            resetButtons()
        }

        btnclick = view as? Button
        btnclick?.setBackgroundResource(R.drawable.background_btn_choose_color)

        isclickBtn = true
        valueChoose = btnclick?.text.toString()
    }

    private fun resetButtons() {
        binding.btnChoose1.setBackgroundResource(R.drawable.background_btn_choose)
        binding.btnChoose2.setBackgroundResource(R.drawable.background_btn_choose)
        binding.btnChoose3.setBackgroundResource(R.drawable.background_btn_choose)
        binding.btnChoose4.setBackgroundResource(R.drawable.background_btn_choose)
        binding.btnChoose5.setBackgroundResource(R.drawable.background_btn_choose)
    }

    private fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar).apply {
                setContentView(R.layout.loading_dialog)
                setCancelable(false)
            }
        }
        loadingDialog?.show()
    }

    private fun hideLoading() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog?.dismiss()
        }
    }
}