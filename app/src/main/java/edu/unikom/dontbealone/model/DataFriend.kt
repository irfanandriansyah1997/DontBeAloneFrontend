package edu.unikom.dontbealone.model

import androidx.room.Embedded
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 7/17/2019.
 */
data class DataFriend(

    @SerializedName("sender")
    @Expose
    val sender: DataUser,

    @SerializedName("receiver")
    @Expose
    @Embedded
    val receiver: DataUser,

    @SerializedName("status")
    @Expose
    val status: Int
)