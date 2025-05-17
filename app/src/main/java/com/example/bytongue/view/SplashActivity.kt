package com.example.bytongue.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bytongue.R
import com.example.bytongue.api.Endpoint
import com.example.bytongue.manager.SessionManager
import com.example.bytongue.model.AuthResponse
import com.example.bytongue.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        getToken()
    }

    private fun getToken() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/", this)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getUserToken().enqueue((object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                when(response.code()){
                    200 -> {
                        sessionManager.saveUserId(response.body()?.user_id.toString())
                        sessionManager.saveUserName(response.body()?.name.toString())
                        sessionManager.saveUserEmail(response.body()?.email.toString())
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)

                    }

                    404 -> {
                        val intent = Intent(applicationContext, SigninActivity::class.java)
                        startActivity(intent)
                    }
                }

            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                showToast(t.message.toString())
            }
        }))
    }

    private fun showToast(message : String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}