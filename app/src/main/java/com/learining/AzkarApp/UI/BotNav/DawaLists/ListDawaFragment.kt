package com.learining.AzkarApp.UI.BotNav.DawaLists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.learining.AzkarApp.Adapter.DawaAdapter
import com.learining.AzkarApp.Data.model.DawaItem
import com.learining.AzkarApp.databinding.FragmentListDawaBinding

class ListDawaFragment : Fragment() {

    private var _binding: FragmentListDawaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DawaAdapter
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListDawaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DawaAdapter(emptyList())
        binding.DawaRecyclerView.adapter = adapter
        binding.DawaRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Listen for playlists (previously fields)
        db.collection("dawa_lists").addSnapshotListener { snapshot, _ ->
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(DawaItem::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            adapter.updateList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
