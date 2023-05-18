package com.acun.storyapp.utils

import android.view.View

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}

fun View.isVisible(boolean: Boolean) {
    visibility = if (boolean) View.VISIBLE else View.GONE
}