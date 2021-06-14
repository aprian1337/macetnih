package com.macetnih.macetnih.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.macetnih.macetnih.R
import com.macetnih.macetnih.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val TAG = javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        writeData(db)
        readData(db)
    }

    private fun readData(db: FirebaseFirestore) {
        db.collection("macet").get()
            .addOnSuccessListener {
                for(doc in it){
                    Log.d(TAG, "${doc.data}")
                }
            }
            .addOnFailureListener{
                Log.w(TAG, "Error ${it.message}")
            }
    }

    private fun writeData(db: FirebaseFirestore) {
        val macet = hashMapOf(
            "street" to "Jl. Sudirman",
            "status" to "Macet",
            "solution" to "Pindah haluan ke Jl. Tamrin"
        )
        db.collection("macet").add(macet)
            .addOnSuccessListener {
                Log.d(TAG, "Document Snapshot : ${it.id}")
            }
            .addOnFailureListener{
                Log.w(TAG, "Error adding ${it.message}")
            }
    }

}