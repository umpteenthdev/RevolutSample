package com.umpteenthdev.revolutsample.rates.adapters.imageloading

import android.widget.ImageView

interface ImageLoader {
    fun loadTo(view: ImageView, imageUrl: String, placeholderRes: Int)
}
