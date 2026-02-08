package com.learining.AzkarApp.UI.BotNav.Fortune

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.FragmentStatusFortuneBinding
import com.learining.AzkarApp.utils.ChartData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatusFortuneFragment : Fragment() {

    private var _binding: FragmentStatusFortuneBinding? = null
    private val binding get() = _binding!!

    private val colors = listOf(
        R.color.teal_primary,
        R.color.teal_light,
        R.color.teal_dark,
        android.R.color.holo_blue_light,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_purple,
        android.R.color.holo_red_light
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatusFortuneBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFortuneData()
    }

    private fun loadFortuneData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.scrollViewChart.visibility = View.GONE
        binding.tvEmptyState.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val db = DataBaseBuilder.getInstance(requireContext())
                val fortuneDao = db.fortuneDao()

                // Fetch all fortunes from database
                val fortunes = withContext(Dispatchers.IO) {
                    fortuneDao.getAllFortunes()
                }

                // Calculate total score
                val totalScore = fortunes.sumOf { it.score }
                binding.tvTotalScore.text = totalScore.toString()

                // Prepare chart data
                if (fortunes.isNotEmpty()) {
                    val chartData = fortunes.mapIndexed { index, fortune ->
                        // Truncate zikr text if too long
                        val displayText = if (fortune.zikr.length > 30) {
                            fortune.zikr.take(27) + "..."
                        } else {
                            fortune.zikr
                        }

                        ChartData(
                            label = displayText,
                            value = fortune.score,
                            color = ContextCompat.getColor(
                                requireContext(),
                                colors[index % colors.size]
                            )
                        )
                    }.sortedByDescending { it.value } // Sort by score descending

                    // Update chart
                    binding.barChart.setData(chartData)
                    binding.scrollViewChart.visibility = View.VISIBLE
                } else {
                    binding.tvEmptyState.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvEmptyState.text = "حدث خطأ في تحميل البيانات"
                binding.tvEmptyState.visibility = View.VISIBLE
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload data when fragment becomes visible (in case scores were updated)
        if (_binding != null) {
            loadFortuneData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}