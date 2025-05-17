package com.example.bytongue.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bytongue.R
import com.example.bytongue.adapter.HistoryAdapter
import com.example.bytongue.api.Endpoint
import com.example.bytongue.databinding.FragmentRecentBinding
import com.example.bytongue.manager.SessionManager
import com.example.bytongue.util.NetworkUtils
import com.example.bytongue.view.ChatActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRecentBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupRecycler()
        loadHistory()
        listeners()
    }

    private fun listeners() {

        binding.btHistory.setOnClickListener {
            findNavController().navigate(R.id.action_recentFragment_to_historyFragment)
        }

        binding.newChat.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadHistory() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/", requireContext())
        val endpoint = retrofitClient.create(Endpoint::class.java)
        val userId = sessionManager.getUserId().toString()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = endpoint.historyUser(userId)
                if (response.isSuccessful) {
                    val historyList = response.body() ?: emptyList()

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                    val daysAgo = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    daysAgo.add(Calendar.DAY_OF_YEAR, -30)

                    val historyFilter = historyList.filter { item ->
                        try {
                            val itemDate = dateFormat.parse(item.created_dt)
                            itemDate != null && (itemDate.after(daysAgo.time) || itemDate == daysAgo.time)
                        } catch (e: Exception) {
                            false
                        }
                    }


                    historyAdapter = HistoryAdapter(historyFilter)
                    binding.chatRecyclerView.adapter = historyAdapter
                } else {
                    Toast.makeText(requireContext(), "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecycler() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}