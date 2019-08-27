package dev.trotrohailer.shared.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.trotrohailer.shared.R
import dev.trotrohailer.shared.widget.CustomSwipeRefreshLayout

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}


@SuppressLint("NewApi") // Lint does not understand isAtLeastQ currently
fun DrawerLayout.shouldCloseDrawerFromBackPress(): Boolean {
    if (BuildCompat.isAtLeastQ()) {
        // If we're running on Q, and this call to closeDrawers is from a key event
        // (for back handling), we should only honor it IF the device is not currently
        // in gesture mode. We approximate that by checking the system gesture insets
        return rootWindowInsets?.let {
            val systemGestureInsets = it.systemGestureInsets
            return systemGestureInsets.left == 0 && systemGestureInsets.right == 0
        } ?: false
    }
    // On P and earlier, always close the drawer
    return true
}


fun openWebsiteUrl(context: Context, url: String) {
    if (url.isBlank()) {
        return
    }
    openWebsiteUri(context, Uri.parse(url))
}

fun openWebsiteUri(context: Context, uri: Uri) {
    if (context.isChromeCustomTabsSupported()) {
        CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            .build()
            .launchUrl(context, uri)
    } else {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

private const val CHROME_PACKAGE = "com.android.chrome"
private fun Context.isChromeCustomTabsSupported(): Boolean {
    val serviceIntent = Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
    serviceIntent.setPackage(CHROME_PACKAGE)
    val resolveInfos = packageManager.queryIntentServices(serviceIntent, 0)
    return !resolveInfos.isNullOrEmpty()
}

object ViewExts {

    /**
     * Sets the colors of the [CustomSwipeRefreshLayout] loading indicator.
     */
    @JvmStatic
    @BindingAdapter("swipeRefreshColors")
    fun setSwipeRefreshColors(swipeRefreshLayout: CustomSwipeRefreshLayout, colorResIds: IntArray) {
        swipeRefreshLayout.setColorSchemeColors(*colorResIds)
    }

    @JvmStatic
    @BindingAdapter("websiteLink", "hideWhenEmpty", requireAll = false)
    fun websiteLink(
        button: View,
        url: String?,
        hideWhenEmpty: Boolean = false
    ) {
        if (url.isNullOrEmpty()) {
            if (hideWhenEmpty) {
                button.isVisible = false
            } else {
                button.isClickable = false
            }
            return
        }
        button.isVisible = true
        button.setOnClickListener {
            openWebsiteUrl(it.context, url)
        }
    }

    @JvmStatic
    @BindingAdapter("app:goneUnless")
    fun goneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("app:fabVisibility")
    fun fabVisibility(fab: FloatingActionButton, visible: Boolean) {
        if (visible) fab.show() else fab.hide()
    }

    @JvmStatic
    @BindingAdapter("app:pageMargin")
    fun pageMargin(viewPager: ViewPager, pageMargin: Float) {
        viewPager.pageMargin = pageMargin.toInt()
    }

    @JvmStatic
    @BindingAdapter("app:clipToCircle")
    fun clipToCircle(view: View, clip: Boolean) {
        view.clipToOutline = clip
        view.outlineProvider = if (clip) CircularOutlineProvider else null
    }
}