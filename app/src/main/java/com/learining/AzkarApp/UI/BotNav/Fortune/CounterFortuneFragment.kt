package com.learining.AzkarApp.UI.BotNav.Fortune

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.learining.AzkarApp.Adapter.FortuneAdapter
import com.learining.AzkarApp.Data.model.FortuneItem
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.databinding.FragmentCounterFortuneBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CounterFortuneFragment : Fragment() {

    private lateinit var db: MyDataBase
    private var _binding: FragmentCounterFortuneBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FortuneAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCounterFortuneBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DataBaseBuilder.getInstance(requireContext())

        fun onAddScore(fortuneItem: FortuneItem, score: Int) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.fortuneDao().updateScore(fortuneItem.id, score)
                }
                val updatedFortunes = withContext(Dispatchers.IO) {
                    db.fortuneDao().getAllFortunes()
                }
                adapter.updateList(updatedFortunes)
                Snackbar.make(
                    binding.root, "You have added $score to your score",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        adapter =
            FortuneAdapter(
                emptyList(),
                requireContext(),
                layoutInflater,
                ::onAddScore,
                ::applyCurrentFilter
            )
        binding.rvFortune.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFortune.adapter = adapter

        loadFortunes()

    }

    private fun loadFortunes() {
        viewLifecycleOwner.lifecycleScope.launch {
            val fortunes = withContext(Dispatchers.IO) {
                db.fortuneDao().getAllFortunes()
            }
            adapter.updateList(fortunes)
        }
    }

    private fun applyCurrentFilter() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allFortunes = db.fortuneDao().getAllFortunes()

            withContext(Dispatchers.Main) {
                adapter.updateList(allFortunes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}