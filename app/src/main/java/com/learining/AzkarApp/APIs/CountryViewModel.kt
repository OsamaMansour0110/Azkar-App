package com.learining.AzkarApp.APIs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CountryViewModel : ViewModel() {
    
    private val _countryState = MutableLiveData(CountryState())
    val countryState: LiveData<CountryState> = _countryState
    
    fun fetchCountries() {
        _countryState.value = _countryState.value?.copy(
            loading = true,
            error = null
        )
        
        viewModelScope.launch {
            try {
                val response = CountryClient.countryService.getAllCountries()
                
                if (response.isSuccessful && response.body() != null) {
                    val countries = response.body()!!
                        .map { it.name.common }
                        .sorted()
                    
                    _countryState.value = CountryState(
                        loading = false,
                        countries = countries,
                        error = null
                    )
                } else {
                    _countryState.value = CountryState(
                        loading = false,
                        countries = emptyList(),
                        error = "فشل في تحميل الدول: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _countryState.value = CountryState(
                    loading = false,
                    countries = emptyList(),
                    error = "لا يوجد اتصال بالإنترنت. يرجى التحقق من الشبكة."
                )
            }
        }
    }
    
    data class CountryState(
        val loading: Boolean = false,
        val countries: List<String> = emptyList(),
        val error: String? = null
    )
}
