package com.example.bytongue.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bytongue.api.Endpoint
import com.example.bytongue.databinding.ActivitySignupBinding
import com.example.bytongue.model.SignupRequest
import com.example.bytongue.util.NetworkUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private var passwordVisible : Boolean = false
    private var passwordConfirmVisible : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        listeners()
    }

    private fun listeners() {

        binding.tvLogin.setOnClickListener {
            navigateToSignin()
        }

        binding.tvMostrarSenha.setOnClickListener{
            showPassword()
        }

        binding.tvConfirmMostrarSenha.setOnClickListener{
            showConfirmPassword()
        }

        binding.btnCadastrar.setOnClickListener {
            if (isValidSignUpDetails()){
                loading(true)
                registerUser(binding.etNome.text.toString(), binding.etEmail.text.toString(), binding.etSenha.text.toString())
            }
        }
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

    private fun showConfirmPassword() {

        passwordConfirmVisible = !passwordConfirmVisible

        if (passwordConfirmVisible) {
            binding.etSenha2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.tvConfirmMostrarSenha.text = "Ocultar"
        } else {
            binding.etSenha2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.tvConfirmMostrarSenha.text = "Mostrar"
        }

        binding.etSenha2.setSelection(binding.etSenha.text.length)
    }

    private fun isValidSignUpDetails(): Boolean {
        if (binding.etNome.text.toString().trim().isEmpty()) {
            showToast("Preencha seu nome")
            return false
        } else if (binding.etEmail.text.toString().trim().isEmpty()) {
            showToast("Preencha seu email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {
            showToast("Preencha um email valido")
            return false
        } else if (binding.etSenha.text.toString().trim().isEmpty()) {
            showToast("Insira uma senha")
            return false
        } else if (binding.etSenha2.text.toString().trim().isEmpty()) {
            showToast("Confirme sua senha")
            return false
        } else if (binding.etSenha.text.toString() != binding.etSenha2.text.toString()) {
            showToast("Senhas não coincidem")
            return false
        } else {
            return true
        }
    }

    private fun showToast(message : String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToSignin(){
        val intent = Intent(this, SigninActivity::class.java)
        startActivity(intent)
    }

    private fun registerUser(name : String, email : String, password : String) {

        val request = SignupRequest(name, email, password)
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/", this)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        val call = endpoint.registerUser(request)

        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    showToast("Usuário cadastrado com sucesso!")
                    finish()

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showToast("Erro ao cadastrar")
                loading(false)
            }
        })
    }

    private fun loading(isLoading : Boolean){
        if(isLoading) {
            binding.btnCadastrar.visibility = View.INVISIBLE // Esconde o botão de cadastro
            binding.progressBar.visibility = View.VISIBLE // Exibe a barra de progresso
        } else {
            binding.btnCadastrar.visibility = View.VISIBLE// Exibe o botão de cadastro
            binding.progressBar.visibility = View.INVISIBLE // Oculta a barra de progresso
        }
    }
}

