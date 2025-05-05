package com.example.bytongue.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bytongue.api.Endpoint
import com.example.bytongue.databinding.ActivitySigninBinding
import com.example.bytongue.manager.SessionManager
import com.example.bytongue.model.AuthResponse
import com.example.bytongue.model.LoginRequest
import com.example.bytongue.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {

    private var passwordVisible : Boolean = false
    private lateinit var binding : ActivitySigninBinding
    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {

        sessionManager = SessionManager(this)

        if (!sessionManager.getToken().isNullOrEmpty()) {
            navigateToHome()
        }

        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listeners()
    }

    private fun listeners() {
        binding.btnEntrar.setOnClickListener {
            if (isValidSignUpDetails()){
                loading(true)
                login()
            }
        }

        binding.tvCriarConta.setOnClickListener{
            navigateToSignup()
        }

        binding.tvMostrarSenha.setOnClickListener{
            showPassword()
        }
    }

    private fun isValidSignUpDetails(): Boolean {
        if (binding.etEmail.text.toString().trim().isEmpty()) {
            showToast("Digite seu email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {
            showToast("Digite um email valido")
            return false
        } else if (binding.etSenha.text.toString().trim().isEmpty()) {
            showToast("Digite sua senha")
            return false
        } else {
            return true
        }
    }

    private fun showToast(message : String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun showPassword() {

        passwordVisible = !passwordVisible

        if (passwordVisible) {
            binding.etSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.tvMostrarSenha.text = "Ocultar"
        } else {
            binding.etSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.tvMostrarSenha.text = "Mostrar"
        }

        binding.etSenha.setSelection(binding.etSenha.text.length)

    }

    private fun navigateToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun login(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        val loginRequest = LoginRequest(email = binding.etEmail.text.toString(), password = binding.etSenha.text.toString())
        val callback = endpoint.authenticate(loginRequest)

        callback.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when(response.code()){
                    200 -> {
                        endpoint.getUserToken().enqueue((object : Callback<AuthResponse> {
                            override fun onResponse(
                                call: Call<AuthResponse>,
                                response: Response<AuthResponse>
                            ) {
                                if (response.isSuccessful){
                                    sessionManager.saveToken(response.body()?.user_id.toString())
                                    sessionManager.saveUserName(response.body()?.name.toString())
                                    sessionManager.saveUserEmail(response.body()?.email.toString())
                                    navigateToHome()
                                    loading(false)
                                }
                            }

                            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                showToast(t.message.toString())
                            }
                        }))
                    }

                    400 -> {
                        showToast("Operação não autorizada")
                        loading(false)

                    }
                    401 -> {
                        showToast("Email ou Senha invalidos")
                        loading(false)
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SigninActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loading(isLoading : Boolean){
        if(isLoading) {
            binding.btnEntrar.visibility = View.INVISIBLE // Esconde o botão de cadastro
            binding.progressBar.visibility = View.VISIBLE // Exibe a barra de progresso
        } else {
            binding.btnEntrar.visibility = View.VISIBLE// Exibe o botão de cadastro
            binding.progressBar.visibility = View.INVISIBLE // Oculta a barra de progresso
        }
    }
}

