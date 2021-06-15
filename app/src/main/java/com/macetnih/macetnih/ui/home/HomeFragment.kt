package com.macetnih.macetnih.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.macetnih.macetnih.databinding.FragmentHomeBinding
import com.macetnih.macetnih.domain.model.Macet
import com.macetnih.macetnih.domain.model.MacetModel
import com.macetnih.macetnih.ui.detail.DetailActivity

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = db.collection("macet")
    private lateinit var adapters : HomeAdapters

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
    }

    private fun setupRv() {
        showLoading(true)

        val query :  Query = collectionReference
        val firestoreRecyclerOptions : FirestoreRecyclerOptions<MacetModel> = FirestoreRecyclerOptions.Builder<MacetModel>()
            .setQuery(query, MacetModel::class.java)
            .build()

        adapters = HomeAdapters(firestoreRecyclerOptions)
        binding.rvHome.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapters
        }
        adapters.setOnItemClickCallback(object : HomeAdapters.OnItemClickCallback{
            override fun onItemClicked(data: MacetModel) {
                selectedMacet(data)
            }
        })
        showLoading(false)
    }

    private fun selectedMacet(data: MacetModel) {
        Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_DETAIL, data.id)
            startActivity(this)
        }
    }

    override fun onStart() {
        super.onStart()
        adapters!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapters!!.stopListening()
    }

    fun showLoading(state: Boolean){
        if(state){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

}