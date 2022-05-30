package com.game.app.models.notification

import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("is_receive_notification") var isReceiveNotification: Boolean = true
)
