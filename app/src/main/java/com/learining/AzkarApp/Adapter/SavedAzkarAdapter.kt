package com.learining.AzkarApp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learining.AzkarApp.Data.model.AzkarItem
import com.learining.AzkarApp.databinding.SavedZekrLayoutBinding

class SavedAzkarAdapter(
    private var listData: MutableList<AzkarItem>,
    private val deleteZekr: (AzkarItem, Int) -> Unit
) :
    RecyclerView.Adapter<SavedAzkarAdapter.AzkarViewHolder>() {

    // Create layout and access it using binding
    inner class AzkarViewHolder(val binding: SavedZekrLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Each Time Declare Object From AzkarViewHolder ->
    // onCreateViewHolder convert layout to object and send it to AzkarViewHolder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AzkarViewHolder {
        val binding =
            SavedZekrLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AzkarViewHolder(binding)
    }

    // Display List into Layout created with FieldViewHolder
    override fun onBindViewHolder(
        holder: AzkarViewHolder,
        position: Int
    ) {
        val item = listData[position]
        holder.binding.tvTitle.text = item.text
        holder.binding.tvCount.text = "${item.count} مرة" // Show target count
        
        holder.binding.btnDelete.setOnClickListener {
            deleteZekr(item, position)
        }
    }

    override fun getItemCount(): Int = listData.size

    // Simple update for the whole list
    fun updateList(newList: List<AzkarItem>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }

    // This is the "Pagination" part: Add new items to the bottom of the current list
    fun addPage(newItems: List<AzkarItem>) {
        val startPos = listData.size
        listData.addAll(newItems)
        // notifyItemRangeInserted instead of notifyDataSetChanged for better performance
        notifyItemRangeInserted(startPos, newItems.size)
    }

    fun removeItem(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

}