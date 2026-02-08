package com.learining.AzkarApp.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.learining.AzkarApp.Data.model.FastingDayItem
import com.learining.AzkarApp.databinding.ItemFastingDayBinding
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class FastingDayAdapter(
    private var listData: List<FastingDayItem>,
    private val onStatusChanged: () -> Unit
) :
    RecyclerView.Adapter<FastingDayAdapter.FastingDayViewHolder>() {

    // Create layout and access it using binding
    inner class FastingDayViewHolder(val binding: ItemFastingDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    // with Each item creating view using FastingDayViewHolder ->
    // onCreateViewHolder convert layout to object to display it in Recyclerview
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FastingDayViewHolder {
        val binding =
            ItemFastingDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FastingDayViewHolder(binding)
    }

    // Display Each item in List in the created view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: FastingDayViewHolder,
        position: Int
    ) {
        val day = listData.get(position)

        val dateString = day.dateDay
        try {
            // 1. Parse the Gregorian date
            // Make sure this matches how you saved it (e.g., "yyyy-MM-dd")
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
            val localDate = LocalDate.parse(dateString, inputFormatter)
            // 2. Convert to Hijri and format as "2 Rajab" (Arabic)
            val hijriDate = HijrahDate.from(localDate)
            // "d MMMM" creates the format
            val hijriFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ar"))
            val hijriString = hijriFormatter.format(hijriDate)
            // 3. Display Gregorian and Hijri together
            holder.binding.tvDayDate.text = "$dateString  |  $hijriString"
            // 4. Display Arabic Day Name
            holder.binding.tvDayNumber.text =
                localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ar"))
        } catch (e: Exception) {
            holder.binding.tvDayDate.text = dateString
            holder.binding.tvDayNumber.text = day.dayNum
        }

        bindStatusChip(holder, day)
    }

    override fun getItemCount(): Int = listData.count()

    private fun bindStatusChip(
        holder: FastingDayViewHolder,
        day: FastingDayItem
    ) {
        if (day.status) {
            holder.binding.chipStatus.text = "اعادة"
            holder.binding.chipStatus.setTextColor(Color.parseColor("#757575"))
            holder.binding.chipStatus.setChipBackgroundColor(
                ColorStateList.valueOf(Color.parseColor("#E0E0E0")) // gray
            )
        } else {
            holder.binding.chipStatus.text = "انتهيت"
            holder.binding.chipStatus.setTextColor(Color.parseColor("#004D40"))
            holder.binding.chipStatus.setChipBackgroundColor(
                ColorStateList.valueOf(Color.parseColor("#E0F2F1")) // teal
            )
        }

        holder.binding.chipStatus.setOnClickListener {
            day.status = !day.status
            onStatusChanged()
        }
    }

    fun updateList(newList: List<FastingDayItem>) {
        listData = newList
        notifyDataSetChanged()
    }

}