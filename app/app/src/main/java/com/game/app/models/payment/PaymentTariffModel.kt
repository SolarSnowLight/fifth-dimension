package com.game.app.models.payment

import com.google.gson.annotations.SerializedName

data class PaymentTariffModel(
    @SerializedName("tariff" ) var tarrif : ArrayList<TariffElementModel> = arrayListOf()
)