package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class DataNotif(
    @SerializedName("content")
    var content: String,
    @SerializedName("photo")
    var photo: String?,
    @SerializedName("time")
    var time: String
)