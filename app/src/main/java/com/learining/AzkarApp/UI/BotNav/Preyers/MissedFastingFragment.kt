package com.learining.AzkarApp.UI.BotNav.Preyers

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.learining.AzkarApp.Adapter.FastingDayAdapter
import com.learining.AzkarApp.Data.model.FastingDayItem
import com.learining.AzkarApp.Data.model.FilterType
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.DialogFastingAddDaysBinding
import com.learining.AzkarApp.databinding.FragmentMissedFastingBinding
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MissedFastingFragment : Fragment() {
    private var _binding: FragmentMissedFastingBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: MyDataBase
    private var dialogBinding: DialogFastingAddDaysBinding? = null
    private var updateDialog: AlertDialog? = null
    private lateinit var adapter: FastingDayAdapter
    private var fullList: List<FastingDayItem> = emptyList()
    private var currentFilter = FilterType.REMAINING

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMissedFastingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DataBaseBuilder.getInstance(requireContext())

        binding.fabAddDays.setOnClickListener {
            showDialog()
        }

        adapter = FastingDayAdapter(emptyList(), ::applyCurrentFilter)
        binding.rvFastingDays.adapter = adapter
        binding.rvFastingDays.layoutManager =
            LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            db.FastingDAO().getFastingDays()
                .collect { list ->
                    fullList = list
                    binding.tvTotalValue.text = list.size.toString()
                    adapter.updateList(list)
                }
        }

        binding.tvClearAll.setOnClickListener {
            lifecycleScope.launch {
                db.FastingDAO().deleteAllFastingDays()
            }
        }

        binding.tvFilterRemaining.setOnClickListener {
            currentFilter = FilterType.REMAINING
            selectRemaining()
            applyCurrentFilter()
        }

        binding.tvFilterCompleted.setOnClickListener {
            currentFilter = FilterType.COMPLETED
            selectCompleted()
            applyCurrentFilter()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDialog() {
        dialogBinding = DialogFastingAddDaysBinding.inflate(layoutInflater)
        updateDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding!!.root).create()

        updateDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        updateDialog?.show()
        updateDialog?.setCanceledOnTouchOutside(true)

        // 1. Define the options list
        val operationOptions = listOf("إضافة إلى الموجود", "حذف القديم وإضافة")
        val dayTypeOptions = listOf("أيام عادية", "أيام سنة نبوية")

        // 2. Create Adapters
        val operationAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, operationOptions)
        val dayTypeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dayTypeOptions)

        // 3. Set Adapters to the AutoCompleteTextViews
        dialogBinding!!.actvOperationType.setAdapter(operationAdapter)
        dialogBinding!!.actvDayType.setAdapter(dayTypeAdapter)

        dialogBinding!!.btnConfirm.setOnClickListener {
            val inp = dialogBinding!!.etDaysInput.text.toString().trim()
            if (inp.isEmpty()) {
                dialogBinding!!.tilDaysInput.error = "Required"
                return@setOnClickListener
            }

            val daysCount = inp.toIntOrNull()
            if (daysCount == null || daysCount <= 0) {
                dialogBinding!!.tilDaysInput.error = "Enter valid number"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                if (dialogBinding!!.actvOperationType.text.toString() == "حذف القديم وإضافة") {
                    db.FastingDAO().deleteAllFastingDays()
                }

                val currentList = db.FastingDAO().getFastingDaysSortedByDate()

                val startDate = if (currentList.isEmpty()) {
                    LocalDate.now().plusDays(1)
                } else {
                    LocalDate.parse(currentList.last().dateDay, formatter).plusDays(1)
                }

                val fastingList = when (dialogBinding!!.actvDayType.text.toString()) {
                    "أيام عادية" -> {
                        (0 until daysCount).map { i ->
                            val date = startDate.plusDays(i.toLong())
                            FastingDayItem(
                                dateDay = date.format(formatter),
                                dayNum = (currentList.size + i + 1).toString()
                            )
                        }
                    }

                    "أيام سنة نبوية" -> {
                        val list = mutableListOf<FastingDayItem>()
                        var date = startDate
                        var addedCount = 0

                        while (addedCount < daysCount) {
                            val isMondayOrThursday =
                                date.dayOfWeek == DayOfWeek.MONDAY || date.dayOfWeek == DayOfWeek.THURSDAY

                            if (isMondayOrThursday) {
                                list.add(
                                    FastingDayItem(
                                        dateDay = date.format(formatter),
                                        dayNum = (currentList.size + addedCount + 1).toString()
                                    )
                                )
                                addedCount++
                            }

                            date = date.plusDays(1)
                        }
                        list
                    }

                    else -> {
                        return@launch
                    }
                }

                db.FastingDAO().insertFastingDays(fastingList)
            }
            updateDialog?.dismiss()
        }

        dialogBinding!!.btnCancel.setOnClickListener {
            updateDialog?.dismiss()
        }
    }

    private fun selectRemaining() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.filterContainer)

        constraintSet.clear(R.id.viewFilterIndicator, ConstraintSet.END)
        constraintSet.connect(
            R.id.viewFilterIndicator,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START
        )
        constraintSet.connect(
            R.id.viewFilterIndicator,
            ConstraintSet.END,
            R.id.guidelineFilterCenter,
            ConstraintSet.START
        )

        TransitionManager.beginDelayedTransition(binding.cardFilter)
        constraintSet.applyTo(binding.filterContainer)

        binding.tvFilterCompleted.setTextColor(Color.parseColor("#A8A7A7"))
        binding.tvFilterRemaining.setTextColor(Color.parseColor("#000000"))
    }

    private fun selectCompleted() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.filterContainer)

        constraintSet.clear(R.id.viewFilterIndicator, ConstraintSet.START)
        constraintSet.connect(
            R.id.viewFilterIndicator,
            ConstraintSet.START,
            R.id.guidelineFilterCenter,
            ConstraintSet.END
        )
        constraintSet.connect(
            R.id.viewFilterIndicator,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END
        )

        TransitionManager.beginDelayedTransition(binding.cardFilter)
        constraintSet.applyTo(binding.filterContainer)

        binding.tvFilterRemaining.setTextColor(Color.parseColor("#A8A7A7"))
        binding.tvFilterCompleted.setTextColor(Color.parseColor("#000000"))
    }

    private fun applyCurrentFilter() {
        val filteredList = when (currentFilter) {
            FilterType.REMAINING -> fullList.filter { !it.status }
            FilterType.COMPLETED -> fullList.filter { it.status }
        }
        adapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}