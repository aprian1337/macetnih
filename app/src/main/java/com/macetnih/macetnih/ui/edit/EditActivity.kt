package com.macetnih.macetnih.ui.edit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.macetnih.macetnih.R
import com.macetnih.macetnih.databinding.ActivityEditBinding
import com.macetnih.macetnih.domain.model.Macet
import com.macetnih.macetnih.ui.detail.DetailActivity

class EditActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditBinding
    private var checked = 1
    private var status: String? = null
    private var street: String? = null
    private var solution: String? = null
    private val db = FirebaseFirestore.getInstance()
    companion object{
        const val EXTRA_MACET = "extra_macet"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val macet = intent.getParcelableExtra<Macet>(EXTRA_MACET) as Macet
        binding.includeUpdate.apply {
            this.btnSubmit.text = "Update"
            this.etStreet.setText(macet.street)
            this.etSolution.setText(macet.solution)
            when(macet.status){
                "Macet Biasa" -> {
                    this.radioBiasa.isChecked = true
                }
                "Macet Sedang" -> {
                    this.radioSedang.isChecked = true
                }
                "Macet Total" -> {
                    this.radioTotal.isChecked = true
                }
            }
        }
        binding.includeUpdate.btnSubmit.setOnClickListener {
            checked = 1
            onRadioButtonClicked(it)
            onStreetButtonClicked(it)
            onSolutionButtonClicked(it)
            if(checked == 1){
                writeData(macet.id!!, it, db)
            }
        }
    }

    private fun onStreetButtonClicked(it: View) {
        if (binding.includeUpdate.etStreet.text?.isEmpty() == true) {
            showSnackbar(it, resources.getString(R.string.err_street_harus_diisi))
            checked = 0
        } else {
            street = binding.includeUpdate.etStreet.text.toString()
        }
    }

    private fun onSolutionButtonClicked(it: View) {
        if (binding.includeUpdate.etSolution.text?.isEmpty() == true) {
            showSnackbar(it, resources.getString(R.string.err_solution_harus_diisi))
            checked = 0
        } else {
            solution = binding.includeUpdate.etSolution.text.toString()
        }
    }

    private fun onRadioButtonClicked(view: View) {
        if (binding.includeUpdate.rgMacet.checkedRadioButtonId == -1) {
            showSnackbar(view, resources.getString(R.string.err_status_harus_diisi))
            checked = 0
        } else {
            status = when {
                binding.includeUpdate.radioBiasa.isChecked -> {
                    "Macet Biasa"
                }
                binding.includeUpdate.radioSedang.isChecked -> {
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

    private fun writeData(id: String, view: View, db: FirebaseFirestore) {
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
        Intent(this@EditActivity, DetailActivity::class.java).apply {
            val model = Macet(
                id,
                street,
                status,
                solution,
            )
            putExtra(DetailActivity.EXTRA_DETAIL, model)
            setResult(1000, this)
            finish()
        }
    }
}