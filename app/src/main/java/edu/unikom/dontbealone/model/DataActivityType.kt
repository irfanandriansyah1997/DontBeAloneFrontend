package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class DataActivityType(
    @SerializedName("name")
    var name: String,
    @SerializedName("icon")
    var icon: String
)