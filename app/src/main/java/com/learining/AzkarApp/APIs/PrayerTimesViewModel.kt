package com.learining.AzkarApp.APIs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learining.AzkarApp.Data.model.PrayerTimings
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrayerTimesViewModel : ViewModel() {
    
    private val _prayerState = MutableLiveData(PrayerState())
    val prayerState: LiveData<PrayerState> = _prayerState
    
    fun fetchPrayerTimes(city: String, country: String) {
        _prayerState.value = _prayerState.value?.copy(
            loading = true,
            error = null
        )
        
        viewModelScope.launch {
            try {
                // Format today's date as DD-MM-YYYY
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                val todayDate = dateFormat.format(Date())
                
                val response = PrayerTimesClient.prayerTimesService.getPrayerTimesByCity(
                    date = todayDate,
                    city = city,
                    country = country
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    
                    _prayerState.value = PrayerState(
                        loading = false,
                        timings = data.data.timings,
                        dateReadable = data.data.date.readable,
                        error = null
                    )
                } else {
                    _prayerState.value = PrayerState(
                        loading = false,
                        timings = null,
                        error = "فشل في تحميل مواقيت الصلاة: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _prayerState.value = PrayerState(
                    loading = false,
                    timings = null,
                    error = "لا يوجد اتصال بالإنترنت. يرجى التحقق من الشبكة."
                )
            }
        }
    }
    
    /**
     * Get the recommended time to say morning azkar (after Fajr)
     */
    fun getMorningAzkarTime(): String? {
        val timings = _prayerState.value?.timings
        return timings?.fajr?.let { fajr ->
            "بعد صلاة الفجر ($fajr)"
        }
    }
    
    /**
     * Get the recommended time to say evening azkar (after Asr or Maghrib)
     */
    fun getEveningAzkarTime(): String? {
        val timings = _prayerState.value?.timings
        return timings?.let {
            "بعد صلاة العصر (${it.asr}) أو المغرب (${it.maghrib})"
        }
    }
    
    data class PrayerState(
        val loading: Boolean = false,
        val timings: PrayerTimings? = null,
        val dateReadable: String? = null,
        val error: String? = null
    )
}
