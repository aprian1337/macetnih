package com.macetnih.macetnih.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Macet(
    val id: String?=null,
    val street: String?=null,
    val status: String?=null,
    val solution: String?=null
) : Parcelable