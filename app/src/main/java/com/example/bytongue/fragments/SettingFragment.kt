package com.example.bytongue.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bytongue.R
import com.example.bytongue.databinding.FragmentSettingBinding
import com.example.bytongue.manager.SessionManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager


    private lateinit var spLevel: Spinner
    private lateinit var spCountry: Spinner

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
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())


        spLevel = binding.spLevel
        spCountry = binding.spCountry

        setupSpinners()
        listeners()
    }

    private fun listeners() {

        binding.btSave.setOnClickListener{
            val languageCode = mapLanguageToCode(spCountry.selectedItem.toString())

            sessionManager.saveLevel(spLevel.selectedItem.toString().toInt())
            sessionManager.saveLanguage(languageCode)

            findNavController().navigate(R.id.action_settingFragment_to_quizFragment)

        }
    }


    private fun setupSpinners() {
            val levels = (1..5).map { it.toString() }
            val languages = listOf("Inglês", "Italiano", "Francês", "Alemão", "Espanhol")

            val levelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, levels)
            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spLevel.adapter = levelAdapter

            val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
            languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spCountry.adapter = languageAdapter

            // Preencher com dados salvos
            val savedLevel = sessionManager.getLevel()
            spLevel.setSelection(savedLevel - 1)

            val savedLangCode = sessionManager.getLanguage()
            val languageMap = mapOf(
                "en-US" to "Inglês",
                "it-IT" to "Italiano",
                "fr-FR" to "Francês",
                "de-DE" to "Alemão",
                "es-ES" to "Espanhol"
            )
            val selectedLangName = languageMap[savedLangCode]
            val langIndex = languages.indexOf(selectedLangName)
            if (langIndex != -1) {
                spCountry.setSelection(langIndex)
            }
    }


    private fun mapLanguageToCode(language: String): String {
        return when (language) {
            "Inglês" -> "en-US"
            "Espanhol" -> "es-ES"
            "Francês" -> "fr-FR"
            "Italiano" -> "it-IT"
            "Alemão" -> "de-DE"
            else -> "en-US" // valor padrão
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}