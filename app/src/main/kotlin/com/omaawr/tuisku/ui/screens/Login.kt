package com.omaawr.tuisku.ui.screens

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.biometric.AuthenticationRequest.Companion.biometricRequest
import androidx.biometric.compose.rememberAuthenticationLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omaawr.tuisku.R
import com.omaawr.tuisku.components.biometricCallback
import com.omaawr.tuisku.ui.viewmodels.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Login(
    onHome: () -> Unit
) {
    val viewModel: LoginViewModel = koinViewModel()
    val password = viewModel.password.collectAsStateWithLifecycle(initialValue = "")
    val usingBiometrics =
        viewModel.usingBiometrics.collectAsStateWithLifecycle(initialValue = false)

    Scaffold { innerPadding ->
        Content(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onHome = onHome,
            password = password.value,
            usingBiometrics = usingBiometrics.value
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    onHome: () -> Unit,
    password: String,
    usingBiometrics: Boolean
) {
    val textFieldState = rememberTextFieldState()
    val activity = LocalActivity.current!!
    val ctx = LocalContext.current
    val resources = LocalResources.current
    val error = remember { mutableStateOf(false) }

    val launcher = rememberAuthenticationLauncher(
        resultCallback = biometricCallback(
            activity = activity,
            onSuccess = {
                onHome()
            },
            onError = {
                Toast.makeText(
                    ctx,
                    resources.getString(R.string.failed_to_use_biometrics),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    )

    // i was too lazy to use LazyColumn lol
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.enter_password_to_unlock_app))

            OutlinedSecureTextField(
                modifier = Modifier.fillMaxWidth(),
                state = textFieldState,
                isError = error.value,
                enabled = password.isNotEmpty()
            )

            Row {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when {
                            textFieldState.text.toString() == password -> onHome()
                            else -> error.value = true
                        }
                    },
                    enabled = password.isNotEmpty()
                ) {
                    Text(stringResource(R.string.confirm))
                }

                Spacer(Modifier.width(4.dp))

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        launcher.launch(
                            biometricRequest(
                                title = resources.getString(R.string.biometric_title)
                            ) {
                                setSubtitle(resources.getString(R.string.write_settings_item_pref))
                            }
                        )
                    },
                    enabled = usingBiometrics
                ) {
                    Text(stringResource(R.string.use_biometrics))
                }
            }
        }
    }
}