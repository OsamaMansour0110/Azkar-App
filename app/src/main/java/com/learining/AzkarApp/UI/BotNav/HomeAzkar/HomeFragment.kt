package com.learining.AzkarApp.UI.BotNav.HomeAzkar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learining.AzkarApp.Data.model.FortuneItem
import com.learining.AzkarApp.DataBase.DataBaseBuilder
import com.learining.AzkarApp.DataBase.FortuneDao
import com.learining.AzkarApp.DataBase.MyDataBase
import com.learining.AzkarApp.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: MyDataBase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DataBaseBuilder.getInstance(requireContext())
        val dao = db.fortuneDao()

        CoroutineScope(Dispatchers.IO).launch {
            initFortunesIfNeeded(requireContext(), dao)
        }
    }


    fun loadFortunesFromJson(context: Context): List<FortuneItem> {
        val json = context.assets.open("FortuneData.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<FortuneItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    suspend fun initFortunesIfNeeded(
        context: Context,
        dao: FortuneDao
    ) {
        val count = dao.getCount()

        if (count == 0) {
            val fortunes = loadFortunesFromJson(context)

            fortunes.forEach { item ->
                dao.insert(
                    item.copy(id = 0)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}