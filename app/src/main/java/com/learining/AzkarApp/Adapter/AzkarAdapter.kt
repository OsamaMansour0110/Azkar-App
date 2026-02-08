package com.learining.AzkarApp.Adapter

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learining.AzkarApp.Data.model.AzkarItem
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.ZekrCardLayoutBinding

class AzkarAdapter(
    private val list: List<AzkarItem>,
    private val onAddZekr: (AzkarItem) -> Unit,
    private val onZekrExist: (AzkarItem, (Boolean) -> Unit) -> Unit
) :
    RecyclerView.Adapter<AzkarAdapter.ViewHolder>() {

    private val originalCounts = mutableMapOf<Int, Int>()

    inner class ViewHolder(val binding: ZekrCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ZekrCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = list[position]
        originalCounts.getOrPut(position) {
            item.count
        }
        adjustTextSizeAnimated(holder.binding.tvTitle, item.text)
        holder.binding.btnCounter.text = "0"
        holder.binding.tvTargetValue.text = item.count.toString()

        onZekrExist(item) { isExist ->
            if (isExist)
                holder.binding.btnSave.iconTint =
                    ColorStateList.valueOf(Color.parseColor("#029D0A"))
            else
                holder.binding.btnSave.iconTint =
                    ColorStateList.valueOf(Color.parseColor("#000000"))
        }

        holder.binding.btnCounter.setOnClickListener {
            val oldCount = holder.binding.btnCounter.text.toString().toInt()
            holder.binding.btnCounter.text = (oldCount + 1).toString()

            if (oldCount == item.count - 1) {
                // Swap to the GREEN 3D surface
                holder.binding.btnCounter.setBackgroundResource(R.drawable.bg_button_3d_success)
            }
            // IMPORTANT: Clear the tint so it doesn't hide the 3D drawable
            holder.binding.btnCounter.backgroundTintList = null
        }

        holder.binding.btnReset.setOnClickListener {
            item.count = originalCounts[position]!!
            holder.binding.btnCounter.apply {
                text = "0"
                // Reset to the BLUE 3D surface
                setBackgroundResource(R.drawable.bg_button_3d)
                // Clear the tint
                backgroundTintList = null
            }
        }

        holder.binding.btnSave.setOnClickListener {
            onAddZekr(item)
            if (holder.binding.btnSave.iconTint == ColorStateList.valueOf(Color.parseColor("#029D0A"))) {
                holder.binding.btnSave.iconTint =
                    ColorStateList.valueOf(Color.parseColor("#000000"))
            } else if (holder.binding.btnSave.iconTint == ColorStateList.valueOf(Color.parseColor("#000000"))) {
                holder.binding.btnSave.iconTint =
                    ColorStateList.valueOf(Color.parseColor("#029D0A"))
            }
        }
    }

    fun adjustTextSizeAnimated(textView: TextView, text: String) {
        textView.text = text

        textView.post {
            val lineCount = textView.lineCount

            val maxSize = 26f
            val midSize = 20f
            val minSize = 16f
            val lowest = 14f

            val targetSize = when {
                lineCount <= 3 -> maxSize
                lineCount <= 6 -> midSize
                lineCount <= 10 -> minSize
                else -> lowest
            }

            val startSize = textView.textSize / textView.resources.displayMetrics.scaledDensity

            ValueAnimator.ofFloat(startSize, targetSize).apply {
                duration = 300
                interpolator = android.view.animation.DecelerateInterpolator()
                addUpdateListener { animator ->
                    textView.textSize = animator.animatedValue as Float
                }
                start()
            }

            textView.setLineSpacing(8f, 1.15f)
        }
    }


    override fun getItemCount(): Int = list.size

}