package com.game.app.models.payment

import com.google.gson.annotations.SerializedName

data class TariffElementModel(
    @SerializedName("uuid"        ) var uuid        : String? = null,
    @SerializedName("value"       ) var value       : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("price"       ) var price       : Int?    = null,
    @SerializedName("period"      ) var period      : Int?    = null
)
