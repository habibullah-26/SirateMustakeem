package com.habib.siratemustakeem.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Duwa : Serializable {
    @SerializedName("sno")
    @Expose
    var id: String? = null

    @SerializedName("title_eng")
    @Expose
    var titleEnglish: String? = null

    @SerializedName("title_urdu")
    @Expose
    var titleUrdu: String? = null

    @SerializedName("arabic_trn")
    @Expose
    var arabicTrn: String? = null

    @SerializedName("urdu_trn")
    @Expose
    var urduTrn: String? = null

    @SerializedName("english_trn")
    @Expose
    var englishTrn: String? = null

    @SerializedName("reference")
    @Expose
    var referenceNo: String? = null
}