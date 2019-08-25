package dev.trotrohailer.passenger.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dev.trotrohailer.passenger.ui.home.MapsActivity
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.databinding.ActivityAuthBinding
import dev.trotrohailer.shared.datasource.PassengerRepository
import dev.trotrohailer.shared.util.Constants
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.intentTo
import dev.trotrohailer.shared.util.mapToPassenger
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import dev.trotrohailer.passenger.R as appR
import dev.trotrohailer.shared.R as sharedR

class AuthActivity : BaseActivity() {
    private val repo by inject<PassengerRepository>(named(Constants.PASSENGERS))
    private lateinit var binding: ActivityAuthBinding
    private val snackbar by lazy {
        Snackbar.make(
            binding.coordinatorLayout,
            "Authentication Failed.",
            Snackbar.LENGTH_LONG
        )
    }

    // Sign in options for Google Auth
    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(appR.string.default_web_client_id))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, sharedR.layout.activity_auth)

        binding.btLogin.setOnClickListener {
            startGoogleAuth()
        }
    }

    // Start Google Login
    private fun startGoogleAuth() {
        with(GoogleSignIn.getClient(this, gso)) {
            startActivityForResult(this.signInIntent, RC_GOOGLE_AUTH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_AUTH) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account!!)
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        debugger("Login failed")
                        snackbar.show()
                    }
                }

                else -> {
                    debugger("Unable to login user")
                    snackbar.apply {
                        setText("Login was cancelled")
                        show()
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        debugger("Logging in with account: ${acct.idToken}")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        // Get auth instance
        val auth: FirebaseAuth = get()

        // Sign in user
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    debugger("signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    debugger("signInWithCredential:failure")
                    snackbar.show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            debugger("Firebase user could not be created")
        } else {
            snackbar.setText("Saving user information").show()
            ioScope.launch {
                repo.saveUser(firebaseUser.mapToPassenger())
                uiScope.launch {
                    intentTo(MapsActivity::class.java, true)
                }
            }
        }
    }

    companion object {
        private const val RC_GOOGLE_AUTH = 88
    }
}