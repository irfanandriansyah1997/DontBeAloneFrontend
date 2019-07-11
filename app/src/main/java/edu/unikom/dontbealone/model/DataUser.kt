package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class DataUser(
    @SerializedName("username")
    var username: String,
    @SerializedName("bio")
    var bio: String?,
    @SerializedName("email")
    var email: String,
    @SerializedName("name")
    var name: String?,
    @SerializedName("photo")
    var photo: String?,
    @SerializedName("phone_number")
    var phoneNumber: String?,
    @SerializedName("address")
    var address: String?,
    @SerializedName("level_user")
    var level: String?,
    @SerializedName("status")
    var status: String?
)