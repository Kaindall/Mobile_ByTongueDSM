package com.example.bytongue.fragments

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bytongue.R
import com.example.bytongue.databinding.CustomDialogBoxBinding
import com.example.bytongue.databinding.FragmentHomeBinding
import com.example.bytongue.manager.SessionManager
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var dialogBinding : CustomDialogBoxBinding
    private lateinit var dialog : Dialog
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var title: String
    private lateinit var text: String

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        dialogBinding = CustomDialogBoxBinding.inflate(layoutInflater)
        title = "\"There is\" vs \"There are\""
        text  = "\"There is\" e \"There are\" são expressões usadas para indicar a existência de algo. \"There is\" é usado quando falamos de algo no singular, como em \"There is a car outside\" (Há um carro lá fora). Já \"there are\" é utilizado para se referir a coisas no plural, como em \"There are five people in the room\" (Há cinco pessoas na sala). A escolha entre as duas formas depende do número do substantivo que estamos descrevendo."



        listeners()
        configTextToSpeech()
        createDialog(title, text)
        loadUserDetails()
    }

    private fun listeners() {

        dialogBinding.iconCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        binding.iconTextTip.setOnClickListener {
            dialog.show()
        }

        binding.iconPlay.setOnClickListener {
            val params = Bundle()
            val utteranceId = "tts-${System.currentTimeMillis()}"
            Handler(Looper.getMainLooper()).postDelayed({
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            },1000)}

        binding.iconPause.setOnClickListener {
            binding.iconPause.visibility = View.INVISIBLE
            binding.iconPlay.visibility = View.VISIBLE
            textToSpeech.stop()
        }

    }

    private fun getFirstName(fullName: String) : String {
        return fullName.trim().split(" ").firstOrNull() ?: ""
    }

    private fun loadUserDetails() {
        // Carrega o nome do usuário a partir das preferências
        binding.userName.text = "Olá, ${getFirstName(sessionManager.getUserName().toString())}"

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

    private fun createDialog(title: String, text: String) {
        dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tipTitle.text = title
        dialogBinding.tipText.text = text
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.custom_dialog_background))
        dialog.setCancelable(false)
    }

    private fun configTextToSpeech() {
        textToSpeech =  TextToSpeech(context) { status ->
            if(status == TextToSpeech.SUCCESS){
                textToSpeech.language = Locale.forLanguageTag("PT-BR")
                textToSpeech.setSpeechRate(1.0f)

                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        requireActivity().runOnUiThread{
                            binding.iconPlay.visibility = View.INVISIBLE
                            binding.iconPause.visibility = View.VISIBLE
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        requireActivity().runOnUiThread {
                            binding.iconPause.visibility = View.INVISIBLE
                            binding.iconPlay.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        requireActivity().runOnUiThread {
                            binding.iconPause.visibility = View.INVISIBLE
                            binding.iconPlay.visibility = View.VISIBLE
                        }
                    }

                })
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}