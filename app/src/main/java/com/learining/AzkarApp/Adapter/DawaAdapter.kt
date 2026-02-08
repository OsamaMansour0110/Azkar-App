package com.learining.AzkarApp.Adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.learining.AzkarApp.Data.model.DawaItem
import com.learining.AzkarApp.databinding.ItemDawaCardBinding

class DawaAdapter(private var list: List<DawaItem>) : 
    RecyclerView.Adapter<DawaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDawaCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDawaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        
        holder.binding.tvTitle.text = item.title
        holder.binding.tvOwnerName.text = item.ownerName
        holder.binding.tvEpisodesCount.text = "${item.episodesCount} حلقة"
        
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.binding.ivPlaylistImage)

        holder.binding.chipFavorite.isChecked = item.isLoved
        
        holder.binding.btnWatch.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.playlistLink))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<DawaItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
