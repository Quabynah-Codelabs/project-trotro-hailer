package dev.trotrohailer.shared.glide

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import dev.trotrohailer.shared.R

@GlideModule
open class HailerGlideModule : AppGlideModule()

/**
 * Load avatar for user's dynamically
 */
fun ImageView.load(uri: Uri?, circleCrop: Boolean = true) = GlideApp.with(context)
    .asBitmap()
    .load(uri.toString())
    .apply {
        if (circleCrop) {
            circleCrop()
                .placeholder(R.drawable.ic_default_avatar_2)
                .error(R.drawable.ic_default_avatar_1)
        } else {
            placeholder(R.color.avatar_content_color)
                .error(R.color.avatar_content_color)
        }
    }
    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    .into(this)