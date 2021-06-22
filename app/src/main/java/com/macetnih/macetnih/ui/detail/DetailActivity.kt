package com.macetnih.macetnih.ui.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.macetnih.macetnih.R
import com.macetnih.macetnih.databinding.ActivityDetailBinding
import com.macetnih.macetnih.domain.model.Macet
import com.macetnih.macetnih.ui.edit.EditActivity
import com.macetnih.macetnih.utils.Common
import com.macetnih.macetnih.utils.PDFDocumentAdapter
import java.io.File
import java.io.FileOutputStream
import kotlin.jvm.Throws

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    val file_name : String = "print.pdf"

    private lateinit var binding: ActivityDetailBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData()

        Dexter.withContext(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    binding.fab.setOnClickListener{
                        createPDFFile(Common.getAppPath(this@DetailActivity)+file_name)
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

            })
            .check()
    }

    private fun createPDFFile(path: String) {
        Log.d("PATH", path)
        if(File(path).exists())
            File(path).delete()
        try{
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(path))
            document.open()
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("Macetnih")
            document.addCreator("Lia")

            val colorAccent = BaseColor(0,153,204,255)
            val headingFontSize = 20.0f
            val valueFontSize = 26.0f
            val fontName = BaseFont.createFont("assets/fonts/brandonmedium.otf", "UTF-8", BaseFont.EMBEDDED)

            val titleStyle = Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "Order Details", Element.ALIGN_CENTER, titleStyle)

            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            addNewItem(document, "Order No: ", Element.ALIGN_LEFT, headingStyle)

            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "#123131", Element.ALIGN_LEFT, valueStyle)

            addLineSeparator(document)
            addNewItem(document, "Order Date: ", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "03/07/2019", Element.ALIGN_LEFT, valueStyle)

            addLineSeparator(document)
            addNewItem(document, "Account Name: ", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "User", Element.ALIGN_LEFT, valueStyle)
            addLineSeparator(document)
            addLineSpace(document)
            addNewItemWithLeftAndRight(document, "Pizza", "(0.0%)", titleStyle, valueStyle)
            addNewItemWithLeftAndRight(document, "Pizza", "(0.0%)", titleStyle, valueStyle)
            addLineSpace(document)
            addLineSeparator(document)
            addLineSpace(document)
            addLineSpace(document)
            document.close()
            Toast.makeText(this@DetailActivity, "SUCCESS!", Toast.LENGTH_LONG).show()
            printPDF()
        }catch (e: Exception){
            Log.e("ERRP", e.message.toString())
        }

    }

    private fun printPDF() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        try{
            val printAdapter = PDFDocumentAdapter(this@DetailActivity, Common.getAppPath(this@DetailActivity)+file_name)
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
        }catch (e: Exception){
            Log.e("ERR", e.message.toString())
        }
    }

    @Throws(DocumentException::class)
    private fun addNewItemWithLeftAndRight(document: Document, textLeft: String, textRight: String, leftStyle: Font, rightStyle: Font) {
        val chunkTextLeft = Chunk(textLeft, leftStyle)
        val chunkTextRight = Chunk(textRight, rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }

    private fun addLineSeparator(document: Document) {
        val lineSeparator = LineSeparator()
        lineSeparator.lineColor = BaseColor(0,0,0,80)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)
    }

    private fun addLineSpace(document: Document) {
        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, alignCenter: Int, style: Font) {
        val chunk = Chunk(text,style)
        val p = Paragraph(chunk)
        p.alignment = alignCenter
        document.add(p)
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
