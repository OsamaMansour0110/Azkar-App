package com.learining.AzkarApp.UI.BotNav.Fortune

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.FragmentOptionsFortuneBinding

class OptionsFortuneFragment : Fragment() {
    private lateinit var db: MyDataBase
    private var _binding: FragmentOptionsFortuneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOptionsFortuneBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DataBaseBuilder.getInstance(requireContext())

        binding.cardTasbih.setOnClickListener {
            moveToFragment(com.learining.AzkarApp.R.id.action_Options_Fortune_to_Counter_Fortune)
        }
        binding.cardStatistics.setOnClickListener {
            moveToFragment(com.learining.AzkarApp.R.id.action_Options_Fortune_to_Status_Fortune)
        }
    }

    private fun moveToFragment(actionId: Int) {
        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_out_right)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
        findNavController().navigate(
            actionId,
            null, options
        )
    }


    fun loadJsonFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}