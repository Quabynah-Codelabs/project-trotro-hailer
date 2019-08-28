package dev.trotrohailer.passenger.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.trotrohailer.passenger.ui.home.MainActivity
import dev.trotrohailer.passenger.ui.settings.SettingsViewModel
import dev.trotrohailer.shared.BuildConfig.DEBUG
import dev.trotrohailer.shared.base.BaseActivity
import dev.trotrohailer.shared.databinding.ActivityAuthBinding
import dev.trotrohailer.shared.util.debugger
import dev.trotrohailer.shared.util.intentTo
import dev.trotrohailer.shared.util.mapToPassenger
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import dev.trotrohailer.shared.R as sharedR

class AuthActivity : BaseActivity() {
    private val viewModel by inject<SettingsViewModel>()
    private lateinit var binding: ActivityAuthBinding

    private val snackbar by lazy {
        Snackbar.make(
            binding.coordinatorLayout,
            "Authentication Failed.",
            Snackbar.LENGTH_LONG
        )
    }

    // Sign in options for Google Auth
    /*private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(appR.string.default_web_client_id))
            .build()
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, sharedR.layout.activity_auth)

        binding.btLogin.setOnClickListener {
            startGoogleAuth()
        }
    }

    // Start Google Login
    private fun startGoogleAuth() {
        val authIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(
                mutableListOf(
                    AuthUI.IdpConfig.PhoneBuilder()
                            // todo: change default device number
                        .setDefaultNumber("gh", "554024702")
                        .build()
                )
            )
            .setIsSmartLockEnabled(DEBUG, true)
            .setTosAndPrivacyPolicyUrls(
                "https://superapp.example.com/terms-of-service.html",
                "https://superapp.example.com/privacy-policy.html"
            )
            .build()
        startActivityForResult(authIntent, RC_AUTH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val response = IdpResponse.fromResultIntent(data)
                    if (response == null) {
                        debugger("Login failed")
                        snackbar.show()
                    } else if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                        debugger("Login failed. No network")
                        snackbar.setText("Please check your internet connection").show()
                    } else {
                        val phoneNumber = response.phoneNumber
                        // Login to Hyper Track with credentials
                        debugger("Phone number after successful login: $phoneNumber")

                        // Get firebase auth instance
                        val auth: FirebaseAuth = get()

                        // get the current user
                        val currentUser = auth.currentUser
                        if (currentUser == null) {
                            debugger("Current user is null")
                            snackbar.show()
                        } else {
                            // Sign in user
                            updateUI(currentUser)
                        }
                    }


                    /*val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account!!)
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        debugger("Login failed")
                        snackbar.show()
                    }*/
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

    /*private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        debugger("Logging in with email: ${acct.email}")
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
    }*/

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            debugger("Firebase user could not be created")
        } else {
            snackbar.setText("Saving user information").show()
            viewModel.saveUser(firebaseUser.mapToPassenger())
            intentTo(MainActivity::class.java, true)
        }
    }

    companion object {
        private const val RC_AUTH = 88
    }
}