package com.learining.AzkarApp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.learining.AzkarApp.Data.model.FortuneItem
import com.learining.AzkarApp.databinding.DialogPreviewZikrBinding
import com.learining.AzkarApp.databinding.ItemFortuneCardBinding

class FortuneAdapter(
    private var listData: List<FortuneItem>,
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val onAddScoreClick: (fortune: FortuneItem, addedScore: Int) -> Unit,
    private val onStatusChanged: () -> Unit
) :
    RecyclerView.Adapter<FortuneAdapter.FortuneViewHolder>() {

    // Create layout and access it using binding
    inner class FortuneViewHolder(val binding: ItemFortuneCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    // with Each item creating view using FortuneViewHolder ->
    // onCreateViewHolder convert layout to object to display it in Recyclerview
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FortuneViewHolder {
        val binding =
            ItemFortuneCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FortuneViewHolder(binding)
    }

    // Display Each item in List in the created view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: FortuneViewHolder,
        position: Int
    ) {
        val fortune = listData.get(position)

        holder.binding.tvMainText.text = fortune.zikr
        holder.binding.tvSubText.text = fortune.summary
        holder.binding.tvCount.text = "0"

        holder.binding.btnAddScore.setOnClickListener {
            val newScore = holder.binding.tvCount.text.toString().toInt()
            onAddScoreClick(fortune, newScore)
            holder.binding.tvCount.text = "0"
        }

        holder.binding.btnPlus.setOnClickListener {
            holder.binding.tvCount.text =
                (holder.binding.tvCount.text.toString().toInt() + 1).toString()
        }

        holder.binding.btnPreview.setOnClickListener {
            showDialog(fortune)
        }
    }

    override fun getItemCount(): Int = listData.count()

    fun updateList(newList: List<FortuneItem>) {
        listData = newList
        notifyDataSetChanged()
    }

    fun showDialog(fortune: FortuneItem) {
        var BindingPreviewItem: DialogPreviewZikrBinding =
            DialogPreviewZikrBinding.inflate(layoutInflater)
        var Dialog = AlertDialog.Builder(context).setView(BindingPreviewItem.root).create()
        Dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Dialog?.show()
        Dialog?.setCanceledOnTouchOutside(true)

        BindingPreviewItem.tvZikr.text = fortune.zikr
        BindingPreviewItem.tvSummary.text = fortune.summary
        BindingPreviewItem.tvHadith.text = fortune.hadith
        BindingPreviewItem.tvScore.text = fortune.score.toString()

        BindingPreviewItem.btnX10.setOnClickListener {
            Dialog?.dismiss()
            onAddScoreClick(fortune, 10)
        }
        BindingPreviewItem.btnX20.setOnClickListener {
            Dialog?.dismiss()
            onAddScoreClick(fortune, 20)
        }
        BindingPreviewItem.btnX50.setOnClickListener {
            Dialog?.dismiss()
            onAddScoreClick(fortune, 50)
        }

        BindingPreviewItem.btnX100.setOnClickListener {
            Dialog?.dismiss()
            onAddScoreClick(fortune, 100)
        }

        BindingPreviewItem.btnClose.setOnClickListener {
            Dialog?.dismiss()
        }

        BindingPreviewItem.btnIncrease.setOnClickListener {
            val currentScore = BindingPreviewItem.tvScore.text.toString().toInt()
            BindingPreviewItem.tvScore.text = (currentScore + 1).toString()
            onAddScoreClick(fortune, 1)
        }

        BindingPreviewItem.btnSource.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fortune.source))
            context.startActivity(intent)
        }
    }
}