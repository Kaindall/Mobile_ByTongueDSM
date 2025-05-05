package com.example.bytongue.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bytongue.adapter.ChatAdapter
import com.example.bytongue.api.Endpoint
import com.example.bytongue.databinding.ActivityChatBinding
import com.example.bytongue.manager.SessionManager
import com.example.bytongue.model.ChatRequest
import com.example.bytongue.model.ChatResponse
import com.example.bytongue.model.CreateChatRequest
import com.example.bytongue.model.CreateChatResponse
import com.example.bytongue.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatResponse>()
    private var userId = ""// ou um ID único do usuário
    private val level = "1"
    private val from = "pt-BR"
    private val to = "us-US"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        init()
        listeners()

    }

    private fun init() {
        chatAdapter = ChatAdapter(chatMessages)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true  // Garante que a lista comece pelo final
        binding.chatRecyclerView.layoutManager = layoutManager
        binding.chatRecyclerView.adapter = chatAdapter
    }

    private fun listeners() {
        binding.layoutSend.setOnClickListener {
            val userInput = binding.inputMessage.text.toString().trim()
            if (userInput.isNotEmpty()) {
                loading(true)
                chatMessages.add(ChatResponse(userInput, true)) // true: é do usuário
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)


                if (userId != "") {
                    sendMessageToApi(userInput)  // Envia para a API
                    binding.inputMessage.setText("") // Limpa o campo de texto
                } else {
                    createMessageToApi(userInput)
                    binding.inputMessage.setText("")
                }
            }
        }

        binding.imageBack.setOnClickListener {
            finish()
        }

    }

    private fun createMessageToApi(userInput: String){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/") // Substitua pela URL da sua API
        val endpoint = retrofitClient.create(Endpoint::class.java)

        val request = CreateChatRequest(
            level,
            from,
            to,
            content = userInput
        )

        val callback = endpoint.createSendMessage(request)

        callback.enqueue(object : Callback<CreateChatResponse> {
            override fun onResponse(
                call: Call<CreateChatResponse>,
                response: Response<CreateChatResponse>
            ) {
                val statusCode = response.code()

                when(statusCode) {

                    200 -> {

                        if (response.isSuccessful && response.body() != null) {

                            val botReply = response.body()!!.content
                            userId = response.body()?.id.toString()

                            chatMessages.add(ChatResponse(botReply, false)) // false: é do bot
                            chatAdapter.notifyItemInserted(chatMessages.size - 1)
                            binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)

                            loading(false)

                        }
                    }
                }

            }

            override fun onFailure(call: Call<CreateChatResponse>, t: Throwable) {
                showToast("Falhou")
            }

        })
    }

    private fun sendMessageToApi(userInput: String) {
        // Configura o Retrofit
        val retrofitClient =
            NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/") // Substitua pela URL da sua API
        val endpoint = retrofitClient.create(Endpoint::class.java)

        // Cria o objeto de solicitação com a mensagem do usuário
        val request = ChatRequest(
            userId = userId,
            content = userInput
        )

        // Faz a chamada para a API
        val callback = endpoint.sendMessage(userId, request)

        callback.enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val botReply = response.body()!!.content

                    // Adiciona a resposta ao RecyclerView
                    chatMessages.add(ChatResponse(botReply, false)) // false: é do bot
                    chatAdapter.notifyItemInserted(chatMessages.size - 1)
                    binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)

                    loading(false)
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                // Caso a chamada falhe
                showToast("Erro ao comunicar com a API")
            }

        })
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun loading(isLoading : Boolean){
        if(isLoading) {
            binding.layoutSend.visibility = View.INVISIBLE // Esconde o botão de cadastro
            binding.progressBar.visibility = View.VISIBLE // Exibe a barra de progresso
        } else {
            binding.layoutSend.visibility = View.VISIBLE// Exibe o botão de cadastro
            binding.progressBar.visibility = View.INVISIBLE // Oculta a barra de progresso
        }
    }

}


