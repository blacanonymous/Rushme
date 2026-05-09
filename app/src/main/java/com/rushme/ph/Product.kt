package com.rushme.ph

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    // DAPAT image_url (may underscore) para tugma sa D1 Database mo
    @SerializedName("image_url")
    val imageUrl: String
)