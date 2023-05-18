package com.acun.storyapp.utils

import android.location.Geocoder
import android.util.Log
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

fun TextView.setLocation(lat: Double, long: Double) {
    text = try {
        val addresses = Geocoder(context, Locale.getDefault()).getFromLocation(lat, long, 1)

        if (addresses.isNullOrEmpty()) "Unknown"
        else addresses.firstOrNull()?.locality ?: "Unknown"
    } catch (e: Exception) {
        "Unknown"
    }
}

fun TextView.setFormattedDate(date: String) {
    val unformattedSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val unformattedDate = unformattedSdf.parse(date)
    val formattedSdf = SimpleDateFormat("yyyy-MM-dd | HH:mm:ss", Locale.getDefault())
    val formattedDate = unformattedDate?.let { formattedSdf.format(it) }

    text = formattedDate
}