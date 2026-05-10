package com.rushme.ph

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    @SerializedName("item_code")
    val itemCode: String,
    @SerializedName("item_description")
    val itemDescription: String,
    @SerializedName("sub_category")
    val subCategory: String,
    @SerializedName("net_price")
    val netPrice: Double,
    @SerializedName("image_url")
    val imageUrl: String
)