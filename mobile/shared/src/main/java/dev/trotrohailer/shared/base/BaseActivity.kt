package dev.trotrohailer.shared.base

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseActivity : AppCompatActivity() {
    private val job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO)
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    open fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}