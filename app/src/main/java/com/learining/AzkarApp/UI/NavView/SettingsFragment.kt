package com.learining.AzkarApp.UI.NavView

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.learining.AzkarApp.APIs.CityViewModel
import com.learining.AzkarApp.APIs.CountryViewModel
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.FragmentSettingsBinding
import com.learining.AzkarApp.utils.UsagePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private lateinit var manager: UsagePreferences
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val countryViewModel: CountryViewModel by viewModels()
    private val cityViewModel: CityViewModel by viewModels()
    
    private var countriesList: List<String> = emptyList()
    private var citiesList: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager = UsagePreferences(requireContext())

        setupDarkModeSwitch()
        setupAccountDetails()
        setupLocationSection()
        
        // Fetch countries from API
        countryViewModel.fetchCountries()
    }

    private fun setupDarkModeSwitch() {
        // Load current state
        lifecycleScope.launch {
            val isDark = manager.isDarkMode.first()
            binding.switchDarkMode.isChecked = isDark
        }

        // Handle toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                manager.saveDarkMode(isChecked)
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    private fun setupAccountDetails() {
        binding.optionAccountDetails.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )
        }
    }

    private fun setupLocationSection() {
        // Load saved country and city
        lifecycleScope.launch {
            val savedCountry = manager.country.first()
            val savedCity = manager.city.first()
            
            binding.actvCountry.setText(savedCountry, false)
            binding.actvCity.setText(savedCity, false)
            
            // Fetch cities for the saved country
            if (savedCountry.isNotBlank()) {
                cityViewModel.fetchCities(savedCountry)
            }
        }

        // Observe country list state
        countryViewModel.countryState.observe(viewLifecycleOwner) { state ->
            // Handle loading
            binding.progressCountries.visibility = if (state.loading) View.VISIBLE else View.GONE
            
            // Handle error
            if (state.error != null) {
                binding.tvLocationError.text = state.error
                binding.tvLocationError.visibility = View.VISIBLE
            } else if (cityViewModel.cityState.value?.error == null) {
                binding.tvLocationError.visibility = View.GONE
            }
            
            // Update countries list
            if (state.countries.isNotEmpty()) {
                countriesList = state.countries
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    countriesList
                )
                binding.actvCountry.setAdapter(adapter)
            }
        }
        
        // When country is selected, fetch cities
        binding.actvCountry.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = binding.actvCountry.text.toString()
            
            // Clear current city selection
            binding.actvCity.setText("", false)
            
            // Fetch cities for selected country
            cityViewModel.fetchCities(selectedCountry)
        }
        
        // Also handle when user types and selects
        binding.actvCountry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = binding.actvCountry.text.toString()
                if (country.isNotBlank() && countriesList.contains(country)) {
                    cityViewModel.fetchCities(country)
                }
            }
        }
        
        // Observe city list state
        cityViewModel.cityState.observe(viewLifecycleOwner) { state ->
            // Handle loading
            binding.progressCities.visibility = if (state.loading) View.VISIBLE else View.GONE
            
            // Handle error
            if (state.error != null) {
                binding.tvLocationError.text = state.error
                binding.tvLocationError.visibility = View.VISIBLE
            } else if (countryViewModel.countryState.value?.error == null) {
                binding.tvLocationError.visibility = View.GONE
            }
            
            // Update cities list
            if (state.cities.isNotEmpty()) {
                citiesList = state.cities
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    citiesList
                )
                binding.actvCity.setAdapter(adapter)
            }
        }

        // Save location button
        binding.btnSaveLocation.setOnClickListener {
            val country = binding.actvCountry.text.toString().trim()
            val city = binding.actvCity.text.toString().trim()

            if (country.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى اختيار الدولة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (city.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى اختيار المدينة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                manager.saveCountry(country)
                manager.saveCity(city)
                Toast.makeText(requireContext(), "تم حفظ الموقع بنجاح ✅", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}