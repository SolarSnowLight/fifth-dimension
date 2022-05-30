package com.game.app.models.user

import com.google.gson.annotations.SerializedName

data class UserDataModel (
    @SerializedName("roles_id") var rolesId : String,
    @SerializedName("users_id") var usersId : String,
    @SerializedName("type_auth") var typeAuth : Int,
    @SerializedName("tokens") var tokens : UserTokensModel,
)