package com.macetnih.macetnih.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.macetnih.macetnih.R
import com.macetnih.macetnih.databinding.ActivityDetailBinding
import com.macetnih.macetnih.domain.model.Macet
import com.macetnih.macetnih.ui.edit.EditActivity

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    private lateinit var binding: ActivityDetailBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData()
    }

    private fun showDialogDel(strId: String) {
        val builder =
            AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                .setTitle("Hapus Data")
                .setMessage("Yakin mau hapus?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteById(strId)
                }
                .setNegativeButton("Tidak jadi", null)
        builder.create().show()
    }

    private fun deleteById(strId: String) {
        db.collection("macet").document(strId).delete()
        finish()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        showLoading(true)
        val id = intent.getStringExtra(EXTRA_DETAIL)!!
        val docRef = db.collection("macet").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.apply {
                        this.tvStreet.text = document.data?.get("street").toString()
                        this.tvMacet.text =
                            "Jenis Kemacetan : " + document.data?.get("status").toString()
                        this.tvSolution.text = document.data?.get("solution").toString()
                        this.btnDelete.setOnClickListener {
                            showDialogDel(id)
                        }
                        this.btnEdit.setOnClickListener {
                            val macet = Macet(
                                id,
                                document.data?.get("street").toString(),
                                document.data?.get("status").toString(),
                                document.data?.get("solution").toString(),
                            )
                            Intent(this@DetailActivity, EditActivity::class.java).apply {
                                putExtra(EditActivity.EXTRA_MACET, macet)
                                startActivity(this)
                            }
                        }
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        showLoading(false)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.pbDetail.visibility = View.VISIBLE
        } else {
            binding.pbDetail.visibility = View.GONE
        }
    }
}