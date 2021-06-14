package com.macetnih.macetnih.domain.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Macet(
    val id: String,
    val street: String,
    val status: String,
    val solution: String
) : Parcelable {
    companion object {
        private const val TAG = "MODELMACET"
        fun DocumentSnapshot.toMacet(): Macet?{
            return try {
                val street = getString("street")!!
                val status = getString("status")!!
                val solution = getString("solution")!!
                Macet(
                    id, street, status, solution
                )
            }catch (e: Exception){
                Log.e(TAG, e.message.toString())
                FirebaseCrashlytics.getInstance().log("Error Model Converting")
                FirebaseCrashlytics.getInstance().setCustomKey("id", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
}