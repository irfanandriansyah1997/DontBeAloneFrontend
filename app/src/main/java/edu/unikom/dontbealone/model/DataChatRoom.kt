package edu.unikom.dontbealone.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 7/17/2019.
 */
data class DataChatRoom(

    @SerializedName("id_room")
    @Expose
    var id: String,

    @SerializedName("activity")
    @Expose
    var activity: DataActivity,

    @SerializedName("last_message")
    @Expose
    var message: DataChat?,

    @SerializedName("notif")
    @Expose
    var notif: Int
)