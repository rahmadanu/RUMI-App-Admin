package com.example.rumiappadmin.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Admin(
    val id: String,
    val email: String,
    val address: String,
    val mobile: Long,
    val image: String,
    val restaurantName: String,
    val restaurantDescription: String
) : Parcelable