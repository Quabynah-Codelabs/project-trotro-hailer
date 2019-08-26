package dev.trotrohailer.passenger.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.hypertrack.sdk.HyperTrack
import dev.trotrohailer.passenger.R
import dev.trotrohailer.passenger.databinding.ActivityMainBinding
import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.passenger.util.MainNavigationFragment
import dev.trotrohailer.passenger.util.NavigationHost
import dev.trotrohailer.shared.base.BaseTrackingActivity
import dev.trotrohailer.shared.databinding.HeaderViewBinding
import dev.trotrohailer.shared.glide.load
import dev.trotrohailer.shared.util.shouldCloseDrawerFromBackPress
import dev.trotrohailer.shared.widget.HeightTopWindowInsetsListener
import dev.trotrohailer.shared.widget.NoopWindowInsetsListener
import org.koin.android.ext.android.inject

class MainActivity : BaseTrackingActivity(), NavigationHost {
    private val viewModel by inject<SettingsViewModel>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var navHostFragment: NavHostFragment? = null
    private var currentNavId = NAV_ID_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (HyperTrack.isTracking()){
            HyperTrack.addNotificationIconsAndTitle(
                dev.trotrohailer.shared.R.drawable.group,
                dev.trotrohailer.shared.R.drawable.group,
                getString(
                    dev.trotrohailer.shared.R.string.default_app_name_passenger
                ),
                "You are currently visible to all drivers in your vicinity"
            )
        }
        fetchAndSaveUser()
        setupNavHost(savedInstanceState)
    }

    private fun setupNavHost(savedInstanceState: Bundle?) {
        binding.contentContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        // Make the content ViewGroup ignore insets so that it does not use the default padding
        binding.contentContainer.setOnApplyWindowInsetsListener(NoopWindowInsetsListener)
        binding.statusBarScrim.setOnApplyWindowInsetsListener(HeightTopWindowInsetsListener)

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentNavId = destination.id
            val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(destination.id)
            val lockMode = if (isTopLevelDestination) {
                DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }
            binding.drawer.setDrawerLockMode(lockMode)
        }
        binding.navView.setupWithNavController(navController)
        if (savedInstanceState == null) {
            // default to showing Home
            val initialNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, R.id.navigation_home)
            binding.navView.setCheckedItem(initialNavId) // doesn't trigger listener
            navigateTo(initialNavId)
        }
    }

    private fun fetchAndSaveUser() {
        viewModel.passenger.observe(this, Observer { passenger ->
            HeaderViewBinding.inflate(layoutInflater, binding.navView, false)
                .apply {
                    lifecycleOwner = this@MainActivity
                    userAvatar.load(passenger?.avatar?.toUri())
                    userName.text = passenger?.name
                }
        })
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, binding.drawer)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        getCurrentFragment()?.onUserInteraction()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentNavId = binding.navView.checkedItem?.itemId ?: NAV_ID_NONE
    }

    override fun onBackPressed() {
        /**
         * If the drawer is open, the behavior changes based on the API level.
         * When gesture nav is enabled (Q+), we want back to exit when the drawer is open.
         * When button navigation is enabled (on Q or pre-Q) we want to close the drawer on back.
         */
        if (binding.drawer.isDrawerOpen(binding.navView) && binding.drawer.shouldCloseDrawerFromBackPress()) {
            closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    private fun closeDrawer() {
        binding.drawer.closeDrawer(GravityCompat.START)
    }

    private fun getCurrentFragment(): MainNavigationFragment? {
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? MainNavigationFragment
    }

    private fun navigateTo(navId: Int) {
        if (navId == currentNavId) {
            return // user tapped the current item
        }
        navController.navigate(navId)
    }

    companion object {
        /** Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"
        private const val NAV_ID_NONE = -1
        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_home,
            R.id.navigation_trips,
            R.id.navigation_settings,
            R.id.navigation_about
        )
    }

}
