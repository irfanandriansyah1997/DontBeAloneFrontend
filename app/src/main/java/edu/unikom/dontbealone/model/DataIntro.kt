package edu.unikom.dontbealone.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */

data class DataIntro(
    @SerializedName("image")
    var image: String,
    @SerializedName("title_text")
    var titleText: String,
    @SerializedName("desc_text")
    var descText: String
)