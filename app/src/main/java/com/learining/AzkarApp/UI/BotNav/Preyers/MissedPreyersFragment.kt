package com.learining.AzkarApp.UI.BotNav.Preyers

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.learining.AzkarApp.Data.model.MissedPrayers
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.databinding.DialogCalcMissedPrayersBinding
import com.learining.AzkarApp.databinding.FragmentMissedPreyersBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MissedPreyersFragment : Fragment() {
    private var _binding: FragmentMissedPreyersBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: MyDataBase
    private var dialogBinding: DialogCalcMissedPrayersBinding? = null
    private var updateDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMissedPreyersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DataBaseBuilder.getInstance(requireContext())

        lifecycleScope.launch {
            db.PrayerDAO().getMissedPrayers().collect { missedPrayer ->
                missedPrayer ?: return@collect
                binding.tvCountFajr.text = missedPrayer.fajr.toString()
                binding.tvCountDhuhr.text = missedPrayer.dhuhr.toString()
                binding.tvCountAsr.text = missedPrayer.asr.toString()
                binding.tvCountMaghrib.text = missedPrayer.maghrib.toString()
                binding.tvCountIsha.text = missedPrayer.isha.toString()
            }
        }

        binding.btnOpenDialog.setOnClickListener {
            showDialog()
        }

        val addMinusButtonList = listOf(
            Triple(binding.btnPlusFajr, binding.btnMinusFajr, binding.tvCountFajr),
            Triple(binding.btnPlusDhuhr, binding.btnMinusDhuhr, binding.tvCountDhuhr),
            Triple(binding.btnPlusAsr, binding.btnMinusAsr, binding.tvCountAsr),
            Triple(binding.btnPlusMaghrib, binding.btnMinusMaghrib, binding.tvCountMaghrib),
            Triple(binding.btnPlusIsha, binding.btnMinusIsha, binding.tvCountIsha)
        )

        for ((plus, minus, text) in addMinusButtonList) {
            plus.setOnClickListener {
                updatePrayerValue(text, 1)
            }
            minus.setOnClickListener {
                updatePrayerValue(text, -1)
            }
        }

        binding.btnAddAll.setOnClickListener {
            lifecycleScope.launch {
                db.PrayerDAO().incrementAllPrayers()
            }
        }
        binding.btnMinusAll.setOnClickListener {
            lifecycleScope.launch {
                db.PrayerDAO().decrementAllPrayersSafely()
            }
        }
    }

    fun showDialog() {
        dialogBinding = DialogCalcMissedPrayersBinding.inflate(layoutInflater)
        updateDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding!!.root).create()

        updateDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        updateDialog?.show()
        updateDialog?.setCanceledOnTouchOutside(true)

        dialogBinding!!.btnUpdateCalc.setOnClickListener {
            val checkList = listOf(
                dialogBinding!!.etMonths to dialogBinding!!.layoutMonth,
                dialogBinding!!.etYears to dialogBinding!!.layoutYears
            )
            for ((input, layout) in checkList) {
                if (input.text.toString().trim().isEmpty()) {
                    layout.error = "Required"
                    layout.isErrorEnabled = true
                    input.requestFocus()
                    return@setOnClickListener
                } else {
                    layout.error = null
                    layout.isErrorEnabled = false
                }
            }
            showConfirmDialog(dialogBinding!!.etYears, dialogBinding!!.etMonths)
        }
    }

    private fun showConfirmDialog(etYears: TextInputEditText, etMonths: TextInputEditText) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("في كل مرة تقوم بالموافقة سوف يلغي الصلوات المسجلة ويعيد التسجيل مجددا\nهل انت موافق علي ذلك؟")
            .setPositiveButton("نعم") { dialogInterface, _ ->
                val days = (etYears.text?.trim().toString()
                    .toInt() * 365) + (etMonths.text?.trim().toString().toInt() * 30)
                val missedPreyers = MissedPrayers(
                    fajr = days,
                    dhuhr = days,
                    asr = days,
                    maghrib = days,
                    isha = days
                )
                lifecycleScope.launch {
                    db.PrayerDAO().insertOrUpdate(missedPreyers)
                }
                binding.tvCountFajr.text = missedPreyers.fajr.toString()
                binding.tvCountDhuhr.text = missedPreyers.dhuhr.toString()
                binding.tvCountAsr.text = missedPreyers.asr.toString()
                binding.tvCountMaghrib.text = missedPreyers.maghrib.toString()
                binding.tvCountIsha.text = missedPreyers.isha.toString()
                dialogInterface.dismiss()
                updateDialog?.dismiss()

            }
            .setNegativeButton("إلغاء") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(true)
            .create()

        dialog.show()
    }

    private fun updatePrayerValue(prayerText: TextView, value: Int) {
        val curr = prayerText.text.toString().trim().toIntOrNull() ?: 0
        val newVal = curr + value
        if (newVal >= 0)
            prayerText.text = newVal.toString()

        lifecycleScope.launch {
            val missedPrayer = db.PrayerDAO().getMissedPrayers().first() ?: return@launch
            val updatedMissedPrayer = when (prayerText.hint.toString()) {
                "fajr" -> missedPrayer.copy(fajr = newVal)
                "dhuhr" -> missedPrayer.copy(dhuhr = newVal)
                "asr" -> missedPrayer.copy(asr = newVal)
                "maghrib" -> missedPrayer.copy(maghrib = newVal)
                "isha" -> missedPrayer.copy(isha = newVal)
                else -> missedPrayer
            }
            db.PrayerDAO().insertOrUpdate(updatedMissedPrayer)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}