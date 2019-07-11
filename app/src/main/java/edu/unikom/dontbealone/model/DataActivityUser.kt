package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class DataActivityUser(
    @SerializedName("id_activity")
    var id: String,
    @SerializedName("id_activity_user")
    var idActUser: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("level_user")
    var level: String,
    @SerializedName("status")
    var status: String
)