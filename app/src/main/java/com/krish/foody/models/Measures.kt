package com.krish.foody.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Measures(
    @SerializedName("metric")
    val metric: Metric?,
    @SerializedName("us")
    val us: Us?
) : Parcelable