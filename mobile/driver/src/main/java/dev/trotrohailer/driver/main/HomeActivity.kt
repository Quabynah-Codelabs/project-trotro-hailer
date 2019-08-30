package dev.trotrohailer.driver.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dev.trotrohailer.driver.R
import dev.trotrohailer.driver.databinding.ActivityHomeBinding
import dev.trotrohailer.driver.util.MainNavigationFragment
import dev.trotrohailer.driver.util.NavigationHost
import dev.trotrohailer.shared.base.BaseTrackingActivity
import dev.trotrohailer.shared.util.shouldCloseDrawerFromBackPress
import dev.trotrohailer.shared.widget.HeightTopWindowInsetsListener
import dev.trotrohailer.shared.widget.NoopWindowInsetsListener

class HomeActivity : BaseTrackingActivity(), NavigationHost {
    private lateinit var binding: ActivityHomeBinding
//    private lateinit var navController: NavController
//    private var navHostFragment: NavHostFragment? = null
    private var currentNavId = NAV_ID_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setupNavHost(savedInstanceState)
    }

    private fun setupNavHost(savedInstanceState: Bundle?) {
        binding.contentContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        // Make the content ViewGroup ignore insets so that it does not use the default padding
        binding.contentContainer.setOnApplyWindowInsetsListener(NoopWindowInsetsListener)
        binding.statusBarScrim.setOnApplyWindowInsetsListener(HeightTopWindowInsetsListener)

        /*navHostFragment = supportFragmentManager
            .findFragmentById(R.id.driver_nav_host_fragment) as NavHostFragment?
        navController = findNavController(R.id.driver_nav_host_fragment)
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
        }*/
    }

    override fun locationServicesEnabled() {
        // todo:
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, binding.drawer)
        //toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        //getCurrentFragment()?.onUserInteraction()
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

   /* private fun getCurrentFragment(): MainNavigationFragment? {
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? MainNavigationFragment
    }

    private fun navigateTo(navId: Int) {
        if (navId == currentNavId) {
            return // user tapped the current item
        }
        navController.navigate(navId)
    }*/

    companion object {
        /** Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"
        private const val NAV_ID_NONE = -1
        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_home,
            R.id.navigation_trips,
            R.id.navigation_earning,
            R.id.navigation_settings,
            R.id.navigation_about
        )
    }

}
