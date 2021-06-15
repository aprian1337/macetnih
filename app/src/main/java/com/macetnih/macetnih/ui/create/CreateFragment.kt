package com.macetnih.macetnih.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.macetnih.macetnih.R
import com.macetnih.macetnih.databinding.FragmentCreateBinding
import com.macetnih.macetnih.domain.model.Macet

class CreateFragment : Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private var checked = 1
    private var status: String? = null
    private var street: String? = null
    private var solution: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeCreate.btnSubmit.setOnClickListener {
            checked = 1
            showLoading(true)
            onRadioButtonClicked(it)
            onStreetButtonClicked(it)
            onSolutionButtonClicked(it)
            if(checked == 1){
                writeData(it, db)
            }
            showLoading(false)
        }
    }

    private fun onStreetButtonClicked(it: View) {
        if (binding.includeCreate.etStreet.text?.isEmpty() == true) {
            showSnackbar(it, resources.getString(R.string.err_street_harus_diisi))
            checked = 0
        } else {
            street = binding.includeCreate.etStreet.text.toString()
        }
    }

    private fun onSolutionButtonClicked(it: View) {
        if (binding.includeCreate.etSolution.text?.isEmpty() == true) {
            showSnackbar(it, resources.getString(R.string.err_solution_harus_diisi))
            checked = 0
        } else {
            solution = binding.includeCreate.etSolution.text.toString()
        }
    }

    private fun onRadioButtonClicked(view: View) {
        if (binding.includeCreate.rgMacet.checkedRadioButtonId == -1) {
            showSnackbar(view, resources.getString(R.string.err_status_harus_diisi))
             checked = 0
        } else {
            status = when {
                binding.includeCreate.radioBiasa.isChecked -> {
                    "Macet Biasa"
                }
                binding.includeCreate.radioSedang.isChecked -> {
                    "Macet Sedang"
                }
                else -> {
                    "Macet Total"
                }
            }
        }
    }

    private fun showSnackbar(v: View, msg: String) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun writeData(view: View, db: FirebaseFirestore) {
        val id = "macet"+ (-1*System.currentTimeMillis()).toString()
        val macet = hashMapOf(
            "street" to street,
            "status" to status,
            "solution" to solution,
            "id" to id
        )
        db.collection("macet").document(id).set(macet)
            .addOnSuccessListener {
                showSnackbar(view, resources.getString(R.string.success_write))
            }
            .addOnFailureListener {
                showSnackbar(view, resources.getString(R.string.err_write))
            }
        clearInput()
    }

    private fun showLoading(state: Boolean){
        if(state){
            binding.includeCreate.pbCreateUpdate.visibility = View.VISIBLE
        }else{
            binding.includeCreate.pbCreateUpdate.visibility = View.GONE
        }
    }

    private fun clearInput(){
        binding.includeCreate.apply {
            rgMacet.clearCheck()
            etStreet.text?.clear()
            etSolution.text?.clear()
        }
    }
}