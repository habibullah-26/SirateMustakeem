package com.habib.siratemustakeem.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Hadith : Serializable {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("arabic")
    var arabic: String? = null

    @SerializedName("urdu")
    var urdu: String? = null

    @SerializedName("reference")
    var reference: String? = null
}
