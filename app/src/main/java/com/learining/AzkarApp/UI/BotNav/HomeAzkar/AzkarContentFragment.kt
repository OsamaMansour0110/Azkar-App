package com.learining.AzkarApp.UI.BotNav.HomeAzkar

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learining.AzkarApp.Adapter.AzkarAdapter
import com.learining.AzkarApp.Data.model.AzkarCategory
import com.learining.AzkarApp.Data.model.AzkarItem
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.FragmentAzkarContentBinding
import kotlinx.coroutines.launch

class AzkarContentFragment : Fragment() {

    private lateinit var db: MyDataBase
    private var _binding: FragmentAzkarContentBinding? = null
    private val binding get() = _binding!!

    private var timer: CountDownTimer? = null
    private var elapsedSeconds = 0
    private var isRunning = false

    private fun startTimer() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedSeconds++

                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60

                binding.tvTimer.text =
                    String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {}
        }.start()

        isRunning = true
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAzkarContentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DataBaseBuilder.getInstance(requireContext())

        val zkrId = (requireArguments().getString("zkr"))?.toInt()
        val jsonString = loadJsonFromAssets(requireContext(), "AzkarData.json")

        val type = object : TypeToken<List<AzkarCategory>>() {}.type
        val categories: List<AzkarCategory> =
            Gson().fromJson(jsonString, type)

        binding.tvTitleHeader.text = categories.first { it.id == zkrId }.name
        val items = categories.first { it.id == zkrId }.items

        fun addZekr(azkarItem: AzkarItem) {
            lifecycleScope.launch {
                val isExistZekr = db.zekrDAO().isZekrExists(azkarItem.text)
                if (isExistZekr) {
                    Toast.makeText(requireContext(), "كدا تمسحني", Toast.LENGTH_SHORT)
                        .show()
                    db.zekrDAO().deleteZekrByText(azkarItem.text)
                } else {
                    db.zekrDAO().addZekr(azkarItem)
                    Toast.makeText(requireContext(), "الذكر اتضاف", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        fun onZekrExist(azkarItem: AzkarItem, onResult: (Boolean) -> Unit) {
            lifecycleScope.launch {
                val isExistZekr = db.zekrDAO().isZekrExists(azkarItem.text)
                onResult(isExistZekr)
            }
        }

        binding.viewPager.adapter = AzkarAdapter(items, ::addZekr, ::onZekrExist)
        binding.totalQuestion.text = "     / ${items.size}"

        binding.viewPager.apply {
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            setPageTransformer { page, position ->
                val absPos = kotlin.math.abs(position)
                page.translationX = -40f * position
                page.scaleY = 1 - (0.08f * absPos)
                page.alpha = 1 - (0.25f * absPos)
            }
            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        binding.tvProgress.text = (position + 1).toString()
                    }
                }
            )
        }
        // Remove This buttons at First
        binding.cardPrev.visibility = View.GONE
        binding.cardFinish.visibility = View.GONE

        binding.btnPrev.setOnClickListener {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, true)
            }
            if (binding.viewPager.currentItem == items.size - 1) {
                binding.cardNext.visibility = View.VISIBLE
                binding.cardFinish.visibility = View.GONE
            } else if (binding.viewPager.currentItem == 0) {
                binding.cardPrev.visibility = View.GONE
            } else if (binding.viewPager.currentItem in 1 until items.size - 1) {
                binding.cardFinish.visibility = View.GONE
                binding.cardNext.visibility = View.VISIBLE
            }
        }

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < items.size - 1) {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            }

            if (binding.viewPager.currentItem == items.size - 1) {
                binding.cardNext.visibility = View.GONE
                binding.cardFinish.visibility = View.VISIBLE
            } else if (binding.viewPager.currentItem in 1 until items.size - 1) {
                binding.cardPrev.visibility = View.VISIBLE
            }
        }

        binding.cardFinish.setOnClickListener {

        }


        binding.iconTimerAction.setOnClickListener {
            if (isRunning) {
                pauseTimer()
                binding.iconTimerAction.setImageResource(R.drawable.ic_play)
            } else {
                startTimer()
                binding.iconTimerAction.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    fun loadJsonFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}