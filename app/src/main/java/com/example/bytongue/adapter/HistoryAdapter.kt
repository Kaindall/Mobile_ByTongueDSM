package com.example.bytongue.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.bytongue.R
import com.example.bytongue.model.HistoryResponse
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val historyList: List<HistoryResponse>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatIdTextView: TextView = itemView.findViewById(R.id.textChatId)
        val createdDateTextView: TextView = itemView.findViewById(R.id.textCreatedDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        // 1. Parse da string para Date
        val inputFormat = android.icu.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = inputFormat.parse(item.created_dt) ?: Date()

        // 2. Formatar a data para pt-BR
        val outputFormat = android.icu.text.SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        outputFormat.timeZone = android.icu.util.TimeZone.getTimeZone("America/Sao_Paulo")
        val formattedDate = outputFormat.format(date)

        holder.chatIdTextView.text = "Chat ${position + 1}"
        holder.createdDateTextView.text = "$formattedDate"
    }

    override fun getItemCount(): Int = historyList.size
}
