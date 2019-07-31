package edu.unikom.dontbealone.model

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 7/17/2019.
 */
data class DataChat(

    @SerializedName("message")
    @Expose
    val message: String = "",

    @SerializedName("user")
    @Expose
    @Embedded
    val user: DataUser? = null,

    @SerializedName("date")
    @Expose
    val date: Long = 0
) {

    override fun toString(): String {
        return (if (user?.name != null && !user?.name.equals("")) user?.name else user?.username) + ": " + message
    }

}