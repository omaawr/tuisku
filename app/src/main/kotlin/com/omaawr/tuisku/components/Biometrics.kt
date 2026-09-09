package com.omaawr.tuisku.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.biometric.AuthenticationResult
import androidx.biometric.AuthenticationResultCallback
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Callback used for the Biometric launcher, handles a state when theres no lock set too..
 *
 * @param activity - App activity
 * @param onSuccess - When authentication ends up being successful
 * @param onError - When authentication ends up having an error
 * @since 1.3.1
 *
 * (InlinedApi is suppressed because its already handled)
 */
@SuppressLint("InlinedApi")
fun biometricCallback(
    activity: Activity,
    onSuccess: () -> Unit,
    onError: () -> Unit,
): AuthenticationResultCallback {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    return AuthenticationResultCallback { result ->
        when (result) {
            is AuthenticationResult.Success -> {
                onSuccess()
            }

            is AuthenticationResult.Error -> {
                if (result.errorCode == 11) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG
                        )
                    }

                    scope.launch {
                        activity.startActivity(enrollIntent)
                    }
                } else {
                    Log.d("Tuisku", "Unknown error (error code: ${result.errorCode})")
                    onError()
                }
            }

            else -> {}
        }
    }
}

/**
 * Checks if biometrics is available, also determines if biometrics can be enabled
 * usually this will return false if biometrics isn't enabled, or isnt really strong
 *
 * @param context - Android context
 * @since 1.3.1
 */
fun checkBiometrics(context: Context): Boolean {
    return BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
}