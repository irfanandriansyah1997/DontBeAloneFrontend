package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class WebServiceResult<T>(
    @SerializedName("data")
    var data: T?,
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Boolean
)