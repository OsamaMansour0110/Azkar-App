package com.learining.AzkarApp.APIs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learining.AzkarApp.Data.model.CitiesRequest
import kotlinx.coroutines.launch

class CityViewModel : ViewModel() {
    
    private val _cityState = MutableLiveData(CityState())
    val cityState: LiveData<CityState> = _cityState
    
    fun fetchCities(country: String) {
        if (country.isBlank()) {
            _cityState.value = CityState(
                loading = false,
                cities = emptyList(),
                error = "يرجى اختيار الدولة أولاً"
            )
            return
        }
        
        _cityState.value = _cityState.value?.copy(
            loading = true,
            error = null
        )
        
        viewModelScope.launch {
            try {
                val response = CityClient.cityService.getCitiesByCountry(
                    CitiesRequest(country = country)
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    
                    if (!body.error && body.data.isNotEmpty()) {
                        val cities = body.data.sorted()
                        
                        _cityState.value = CityState(
                            loading = false,
                            cities = cities,
                            error = null
                        )
                    } else {
                        _cityState.value = CityState(
                            loading = false,
                            cities = emptyList(),
                            error = "لم يتم العثور على مدن لهذه الدولة"
                        )
                    }
                } else {
                    _cityState.value = CityState(
                        loading = false,
                        cities = emptyList(),
                        error = "فشل في تحميل المدن: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _cityState.value = CityState(
                    loading = false,
                    cities = emptyList(),
                    error = "لا يوجد اتصال بالإنترنت. يرجى التحقق من الشبكة."
                )
            }
        }
    }
    
    fun clearCities() {
        _cityState.value = CityState()
    }
    
    data class CityState(
        val loading: Boolean = false,
        val cities: List<String> = emptyList(),
        val error: String? = null
    )
}
