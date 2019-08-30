package dev.trotrohailer.driver.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import dev.trotrohailer.driver.R
import dev.trotrohailer.driver.about.AboutFragment
import dev.trotrohailer.driver.databinding.ActivityHomeBinding
import dev.trotrohailer.driver.earnings.EarningsFragment
import dev.trotrohailer.driver.profile.ProfileFragment
import dev.trotrohailer.driver.trips.TripsFragment
import dev.trotrohailer.driver.util.NavigationHost
import dev.trotrohailer.driver.viewmodel.DriverViewModel
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.glide.load
import dev.trotrohailer.shared.util.shouldCloseDrawerFromBackPress
import dev.trotrohailer.shared.widget.NoopWindowInsetsListener
import org.koin.android.ext.android.get

class HomeActivity : BaseActivity(), NavigationHost,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
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
        supportFragmentManager.beginTransaction().replace(R.id.content_container, HomeFragment())
            .commit()

        // Toggle handler
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawer,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(binding.navView.menu.findItem(R.id.navigation_home))

        with(binding.navView.getHeaderView(0)) {
            val viewModel: DriverViewModel = get()

            viewModel.driver.observe(this@HomeActivity, Observer { driver ->
                this.findViewById<ImageView>(R.id.user_avatar).apply {
                    tag = null
                    load(driver.avatar?.toUri(), true)
                }
                this.findViewById<TextView>(R.id.user_name).text = driver.name
            })
        }
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, binding.drawer)
        //toolbar.setupWithNavController(navController, appBarConfiguration)
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

    private fun closeDrawer() = binding.drawer.closeDrawer(GravityCompat.START)

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                binding.toolbar.title = item.title
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_container, HomeFragment())
                    .commit()
            }
            R.id.navigation_trips -> {
                binding.toolbar.title = item.title
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_container, TripsFragment())
                    .commit()
            }
            R.id.navigation_earning -> {
                binding.toolbar.title = item.title
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_container, EarningsFragment())
                    .commit()
            }
            R.id.navigation_settings -> {
                binding.toolbar.title = item.title
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_container, ProfileFragment())
                    .commit()
            }
            R.id.navigation_about -> {
                binding.toolbar.title = item.title
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_container, AboutFragment())
                    .commit()
            }
        }
        closeDrawer()
        return true
    }

}
