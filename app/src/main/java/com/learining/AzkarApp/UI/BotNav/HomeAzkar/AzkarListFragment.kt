package com.learining.AzkarApp.UI.BotNav.HomeAzkar

import com.learining.AzkarApp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.learining.AzkarApp.APIs.PrayerTimesViewModel
import com.learining.AzkarApp.databinding.FragmentAzkarListBinding
import com.learining.AzkarApp.utils.UsagePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class AzkarListFragment : Fragment() {

    private var _binding: FragmentAzkarListBinding? = null
    private val binding get() = _binding!!
    
    private val prayerTimesViewModel: PrayerTimesViewModel by viewModels()
    private lateinit var manager: UsagePreferences
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAzkarListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun moveToContent(value: Int) {
        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_out_right)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
        val bundle = bundleOf("zkr" to value.toString())

        findNavController().navigate(
            com.learining.AzkarApp.R.id.action_azkarList_to_azkarContent, bundle,
            options
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        manager = UsagePreferences(requireContext())

        // Setup click listeners
        binding.morningAzkar.setOnClickListener {
            moveToContent(1)
        }
        binding.nightAzkar.setOnClickListener {
            moveToContent(2)
        }
        binding.sleepAzkar.setOnClickListener {
            moveToContent(3)
        }
        binding.wakeUpAzkar.setOnClickListener {
            moveToContent(4)
        }
        
        // Fetch prayer times and show timing hints
        fetchPrayerTimesAndShowHints()
    }
    
    private fun fetchPrayerTimesAndShowHints() {
        lifecycleScope.launch {
            val country = manager.country.first()
            val city = manager.city.first()
            
            // Fetch prayer times
            prayerTimesViewModel.fetchPrayerTimes(city, country)
        }
        
        // Observe prayer times state
        prayerTimesViewModel.prayerState.observe(viewLifecycleOwner) { state ->
            // Show/hide loading indicator
            if (state.loading) {
                binding.prayerTimesLoading?.visibility = View.VISIBLE
                binding.prayerTimesError?.visibility = View.GONE
            } else {
                binding.prayerTimesLoading?.visibility = View.GONE
            }
            
            // Handle error
            if (state.error != null) {
                binding.prayerTimesError?.text = state.error
                binding.prayerTimesError?.visibility = View.VISIBLE
                // Hide timing hints on error
                binding.tvMorningAzkarTime?.visibility = View.GONE
                binding.tvEveningAzkarTime?.visibility = View.GONE
            } else {
                binding.prayerTimesError?.visibility = View.GONE
            }
            
            // Update timing hints
            if (state.timings != null) {
                // Morning Azkar timing hint
                val morningTime = prayerTimesViewModel.getMorningAzkarTime()
                binding.tvMorningAzkarTime?.text = morningTime
                binding.tvMorningAzkarTime?.visibility = if (morningTime != null) View.VISIBLE else View.GONE
                
                // Evening Azkar timing hint
                val eveningTime = prayerTimesViewModel.getEveningAzkarTime()
                binding.tvEveningAzkarTime?.text = eveningTime
                binding.tvEveningAzkarTime?.visibility = if (eveningTime != null) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}