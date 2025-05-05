package com.example.bytongue.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bytongue.R
import com.example.bytongue.databinding.FragmentEditProfileBinding
import com.example.bytongue.manager.SessionManager
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var encodedImage: String? = null

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
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        loadUserDetails()
        listeners()

    }

    private fun listeners() {

        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        binding.btSave.setOnClickListener {
            sessionManager.saveUserName(binding.inputName.text.toString())
            sessionManager.saveUserEmail(binding.inputEmail.text.toString())
            sessionManager.saveUserImage(encodedImage.toString())

            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        binding.layoutImage.setOnClickListener { v ->
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            pickImage.launch(intent) // Lança a galeria de imagens
        }

    }

    private fun loadUserDetails() {
        // Carrega o nome do usuário a partir das preferências
        binding.inputName.setText(sessionManager.getUserName().toString())
        binding.inputEmail.setText(sessionManager.getUserEmail().toString())

        // Decodifica a imagem do perfil a partir de Base64 e define no ImageView
        if (sessionManager.getUserImage().isNullOrEmpty()){
            binding.imageProfile.setImageResource(R.drawable.user_default)
        } else {
            val bytes: ByteArray =
                Base64.decode(sessionManager.getUserImage(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.imageProfile.setImageBitmap(bitmap)
            binding.textAddImage.visibility = View.GONE
        }

    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { imageUri ->
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = encodedImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun encodedImage(bitmap: Bitmap): String {
        // Redimensiona a imagem para um tamanho adequado e a codifica para Base64
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = bitmap.scale(previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(
            Bitmap.CompressFormat.JPEG,
            50,
            byteArrayOutputStream
        ) // Compacta a imagem em JPEG
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT) // Retorna a imagem codificada em Base64
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}