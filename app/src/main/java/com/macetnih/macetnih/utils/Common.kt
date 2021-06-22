package com.macetnih.macetnih.utils

import android.content.Context
import android.util.Log
import com.macetnih.macetnih.R
import java.io.File

object Common {
    fun getAppPath(context: Context): String {
        val dir = File(
            android.os.Environment.getExternalStorageDirectory().toString()
         + File.separator + context.resources.getString(R.string.app_name) + File.separator)
        Log.d("COMDIR",dir.toString())
        if (!dir.exists())
            dir.mkdir()
        if (!dir.exists())
            Log.d("NOTEXIST", "1")
        return dir.path + File.separator
    }
}