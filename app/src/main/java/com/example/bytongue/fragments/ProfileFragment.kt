package com.example.bytongue.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bytongue.R
import com.example.bytongue.api.Endpoint
import com.example.bytongue.databinding.FragmentProfileBinding
import com.example.bytongue.manager.SessionManager
import com.example.bytongue.util.NetworkUtils
import com.example.bytongue.view.SigninActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())



        loadUserDetails()
        listeners()
    }

    private fun loadUserDetails() {
        // Carrega o nome do usuário a partir das preferências
        binding.userName.text = getFirstName(sessionManager.getUserName().toString())

        // Decodifica a imagem do perfil a partir de Base64 e define no ImageView
        if (sessionManager.getUserImage().isNullOrEmpty()){
            binding.userImage.setImageResource(R.drawable.user_default)
        } else {
            val bytes: ByteArray =
                Base64.decode(sessionManager.getUserImage(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.userImage.setImageBitmap(bitmap)
        }
    }

    private fun getFirstName(fullName: String) : String {
        return fullName.trim().split(" ").firstOrNull() ?: ""
    }

    private fun listeners() {

        binding.containerExit.setOnClickListener{
            val retrofitClient = NetworkUtils.getRetrofitInstance("https://bytongue.azurewebsites.net/", requireContext())
            val endpoint = retrofitClient.create(Endpoint::class.java)
            val callback = endpoint.logout()

            callback.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        sessionManager.clearSession()
                        val intent = Intent(context, SigninActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showToast(t.message.toString())
                }
            })
        }

        binding.containerChat.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

    }

    private fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}