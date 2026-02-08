package com.learining.AzkarApp.UI.NavView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.learining.AzkarApp.Adapter.SavedAzkarAdapter
import com.learining.AzkarApp.Data.model.AzkarItem
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.databinding.FragmentSaveZekrBinding
import kotlinx.coroutines.launch

class saveZekrFragment : Fragment() {
    private lateinit var db: MyDataBase
    private var _binding: FragmentSaveZekrBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SavedAzkarAdapter

    // --- Button-based Pagination Variables ---
    private var currentPage = 0
    private val pageSize = 10 // Let's show 10 items per page

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveZekrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DataBaseBuilder.getInstance(requireContext())

        setupRecyclerView()
        setupPaginationButtons()
        loadCurrentPage()
    }

    private fun setupRecyclerView() {
        adapter = SavedAzkarAdapter(mutableListOf()) { item, position ->
            deleteZekr(item, position)
        }
        binding.AzkarRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.AzkarRecyclerView.adapter = adapter
    }

    private fun setupPaginationButtons() {
        binding.btnNextPage.setOnClickListener {
            currentPage++
            loadCurrentPage()
            binding.AzkarRecyclerView.scrollToPosition(0) // Go to top when page changes
        }

        binding.btnPrevPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                loadCurrentPage()
                binding.AzkarRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun loadCurrentPage() {
        lifecycleScope.launch {
            // Calculate OFFSET (Page 0 starts at 0, Page 1 starts at 10, etc.)
            val offset = currentPage * pageSize
            val results = db.zekrDAO().getPaginatedZekr(pageSize, offset)

            // Update UI
            adapter.updateList(results)
            binding.tvPageNumber.text = "صفحة ${currentPage + 1}"

            // Enable/Disable buttons
            binding.btnPrevPage.isEnabled = currentPage > 0

            // Check if there's a next page by trying to fetch 1 item from next offset
            val nextResults = db.zekrDAO().getPaginatedZekr(1, (currentPage + 1) * pageSize)
            binding.btnNextPage.isEnabled = nextResults.isNotEmpty()
        }
    }

    private fun deleteZekr(azkarItem: AzkarItem, position: Int) {
        lifecycleScope.launch {
            db.zekrDAO().deleteZekr(azkarItem)

            // After deleting, reload current page to keep list accurate
            loadCurrentPage()
            Toast.makeText(requireContext(), "تم الحذف", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}