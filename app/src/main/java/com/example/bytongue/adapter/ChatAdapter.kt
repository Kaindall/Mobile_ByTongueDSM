package com.example.bytongue.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bytongue.databinding.ItemContainerReceivedMessageBinding
import com.example.bytongue.databinding.ItemContainerSentMessageBinding
import com.example.bytongue.model.ChatResponse

class ChatAdapter(
    private val chatMessages: List<ChatResponse>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1      // Mensagem enviada pelo usuário
        private const val VIEW_TYPE_RECEIVED = 2  // Mensagem recebida da API
    }


    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isUser) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        if (holder is SentMessageViewHolder) {
            holder.setData(chatMessage)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.setData(chatMessage)
        }
    }

    class SentMessageViewHolder internal constructor(itemContainerSentMessageBinding: ItemContainerSentMessageBinding) :
        RecyclerView.ViewHolder(itemContainerSentMessageBinding.getRoot()) {
        private val binding: ItemContainerSentMessageBinding =
            itemContainerSentMessageBinding // Inicializa o binding

        // Preenche os dados da mensagem enviada
        fun setData(chatMessage: ChatResponse) {
            binding.textMessage.text = chatMessage.content // Define o texto da mensagem
        }
    }


    class ReceivedMessageViewHolder internal constructor(itemContainerReceivedMessageBinding: ItemContainerReceivedMessageBinding) :
        RecyclerView.ViewHolder(itemContainerReceivedMessageBinding.getRoot()) {
        private val binding: ItemContainerReceivedMessageBinding =
            itemContainerReceivedMessageBinding // Inicializa o binding

        // Preenche os dados da mensagem recebida
        fun setData(chatMessage: ChatResponse) {
            binding.textMessage.text = chatMessage.content // Define o texto da mensagem
        }
    }

}
