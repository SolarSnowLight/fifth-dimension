package com.game.app.models.payment

import com.google.gson.annotations.SerializedName

data class PaymentTokenModel(
    @SerializedName("payment_token") var paymentToken: String? = null
)