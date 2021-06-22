package com.macetnih.macetnih.utils

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import java.io.*

class PDFDocumentAdapter(val context: Context, val path: String): PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal != null) {
            if(cancellationSignal.isCanceled)
                if (callback != null) {
                    callback.onLayoutCancelled()
                }
            else{
                val builder = PrintDocumentInfo.Builder("file name")
                builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()
                if (callback != null) {
                    callback.onLayoutFinished(builder.build(), newAttributes != oldAttributes)
                }
            }
        }

    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        var `in` : InputStream? = null
        var out : OutputStream? = null
        try {
            val file =  File(path)
            `in` = FileInputStream(file)
            if (destination != null) {
                out = FileOutputStream(destination.fileDescriptor)
            }

            if(!cancellationSignal!!.isCanceled){
                if (out != null) {
                    `in`.copyTo(out)
                }
                callback!!.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }
            else
                callback!!.onWriteCancelled()
        }catch (e: Exception){
            if (callback != null) {
                callback.onWriteFailed(e.message)
            }
            Log.e("ERR", e.message.toString())
        }finally {
            try{
                `in`!!.close()
                out!!.close()
            }catch (e: IOException){
                Log.e("ERR", e.message.toString())
            }
        }
    }
}