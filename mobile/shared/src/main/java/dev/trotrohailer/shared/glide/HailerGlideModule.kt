package dev.trotrohailer.shared.glide

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import dev.trotrohailer.shared.R

@GlideModule
open class HailerGlideModule : AppGlideModule()

fun ImageView.load(uri: Uri?, circleCrop: Boolean = true) = GlideApp.with(context)
    .asBitmap()
    .load(uri.toString())
    .apply {
        if (circleCrop) {
            circleCrop()
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
        }
    }
    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    .into(this)