package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
data class DataActivity(
    @SerializedName("id_activity")
    var id: String,
    @SerializedName("activity_name")
    var name: String,
    @SerializedName("activity_type")
    var type: DataActivityType,
    @SerializedName("activity_user")
    var user: DataUser?,
    @SerializedName("activity_my_user")
    var myUser: DataUser?,
    @SerializedName("activity_member")
    var member: List<DataUser>,
    @SerializedName("activity_pending_user_count")
    var userPendingCount: Int,
    @SerializedName("datetime")
    var time: String,
    @SerializedName("price")
    var price: String,
    @SerializedName("description")
    var desc: String,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lng")
    var lng: Double,
    @SerializedName("address")
    var address: String,
    @SerializedName("distance")
    var distance: Double?
)