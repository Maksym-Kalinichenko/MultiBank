package com.realtime.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class StockDTO(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double
)
