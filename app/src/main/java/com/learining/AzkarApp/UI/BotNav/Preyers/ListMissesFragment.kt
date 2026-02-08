package com.learining.AzkarApp.UI.BotNav.Preyers

import com.learining.AzkarApp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.learining.AzkarApp.Data.model.MissedPrayers
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.databinding.FragmentListMissesBinding
import kotlinx.coroutines.launch


class ListMissesFragment : Fragment() {
    private lateinit var db: MyDataBase
    private var _binding: FragmentListMissesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListMissesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DataBaseBuilder.getInstance(requireContext())

        // Run for First time
        lifecycleScope.launch {
            val missedPrayersCheck = db.PrayerDAO().getMissedPrayers()
            if (missedPrayersCheck == null) {
                val prayer = MissedPrayers(fajr = 0, dhuhr = 0, asr = 0, maghrib = 0, isha = 0)
                db.PrayerDAO().insertOrUpdate(prayer)
            }
        }

        binding.cardMissedPrayers.setOnClickListener {
            moveToFragment(com.learining.AzkarApp.R.id.action_missed_extra_to_missed_preyers)
        }
        binding.cardFasting.setOnClickListener {
            moveToFragment(com.learining.AzkarApp.R.id.action_missed_extra_to_missed_fasting)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}