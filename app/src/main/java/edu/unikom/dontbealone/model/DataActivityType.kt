package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class DataActivityType(
    @SerializedName("id_activity_type")
    var id: Int,
    @SerializedName("type")
    var name: String,
    @SerializedName("icon")
    var icon: String?
)