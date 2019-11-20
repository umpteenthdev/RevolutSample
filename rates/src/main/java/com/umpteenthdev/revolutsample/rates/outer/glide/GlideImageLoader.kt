package com.umpteenthdev.revolutsample.rates.outer.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.umpteenthdev.revolutsample.core.entity.sendNonFatalException
import com.umpteenthdev.revolutsample.rates.adapters.imageloading.ImageLoader
import javax.inject.Inject

class GlideImageLoader @Inject constructor() : ImageLoader {

    override fun loadTo(view: ImageView, imageUrl: String, placeholderRes: Int) {
        Glide.with(view.context)
            .load(imageUrl)
            .placeholder(placeholderRes)
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                    if (e == null) return false
                    sendNonFatalException(e)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .into(view)
    }
}
