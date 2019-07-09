package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class DataActivity(
    @SerializedName("name")
    var name: String,
    @SerializedName("desc")
    var desc: String,
    @SerializedName("type")
    var type: DataActivityType,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lng")
    var lng: Double,
    @SerializedName("address")
    var address: String,
    @SerializedName("time")
    var time: String,
    @SerializedName("price")
    var price: String,
    @SerializedName("user")
    var user: DataUser
)