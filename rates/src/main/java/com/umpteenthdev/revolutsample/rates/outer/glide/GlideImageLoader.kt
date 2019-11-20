package com.umpteenthdev.revolutsample.rates.outer.glide

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.umpteenthdev.revolutsample.rates.adapters.imageloading.ImageLoader
import javax.inject.Inject

class GlideImageLoader @Inject constructor() : ImageLoader {

    override fun loadTo(view: ImageView, imageUrl: String, placeholderRes: Int) {
        Glide.with(view.context)
            .load(imageUrl)
            .placeholder(placeholderRes)
            .into(view)
    }
}
