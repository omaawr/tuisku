package com.omaawr.tuisku.screens

import android.content.res.Resources
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.biometric.AuthenticationRequest.Companion.biometricRequest
import androidx.biometric.AuthenticationResultLauncher
import androidx.biometric.compose.rememberAuthenticationLauncher
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omaawr.tuisku.BuildConfig
import com.omaawr.tuisku.R
import com.omaawr.tuisku.components.ChangePasswordDialog
import com.omaawr.tuisku.components.PasswordDialog
import com.omaawr.tuisku.components.SettingsItem
import com.omaawr.tuisku.components.biometricCallback
import com.omaawr.tuisku.components.checkBiometrics
import com.omaawr.tuisku.viewmodels.SettingsUiState
import com.omaawr.tuisku.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Settings page
 *
 * @param onBack - Upon navigating back to the home page
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    onBack: () -> Unit,
    onPort: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState = viewModel.uiState

    val encryptionKey = viewModel.encryptionKey.collectAsStateWithLifecycle(initialValue = "")
    val useSystemFont = viewModel.useSystemFont.collectAsStateWithLifecycle(initialValue = false)
    val disableScreenshots =
        viewModel.disableScreenshots.collectAsStateWithLifecycle(initialValue = false)
    val password = viewModel.password.collectAsStateWithLifecycle(initialValue = "")
    val showNotesNames = viewModel.showNotesNames.collectAsStateWithLifecycle(initialValue = false)
    val useBiometrics = viewModel.useBiometrics.collectAsStateWithLifecycle(initialValue = false)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    DisposableEffect(Unit) {
        // somehow theres a ui bug where some dialogs stay even after navigating to another page
        // causing a NullPointerException on them since settings page and their state is cleared
        onDispose {
            uiState.clear()
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Content(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            encryptionKey = encryptionKey,
            useSystemFont = useSystemFont,
            disableScreenshots = disableScreenshots,
            password = password,
            showNotesNames = showNotesNames,
            useBiometrics = useBiometrics,
            uiState = uiState,
            onWritePassword = {
                viewModel.writePassword(it)
            },
            onWriteShowNotesNames = {
                viewModel.writeShowNotesNames(it)
            },
            onWriteDisableScreenshots = {
                viewModel.writeDisableScreenshots(it)
            },
            onWriteSystemFont = {
                viewModel.writeUseSystemFont(it)
            },
            onWriteUseBiometrics = {
                viewModel.writeUseBiometrics(it)
            },
            onPort = onPort
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    encryptionKey: State<String>,
    useSystemFont: State<Boolean>,
    disableScreenshots: State<Boolean>,
    password: State<String>,
    showNotesNames: State<Boolean>,
    useBiometrics: State<Boolean>,
    uiState: SettingsUiState,
    onWritePassword: (value: String) -> Unit,
    onWriteShowNotesNames: (value: Boolean) -> Unit,
    onWriteDisableScreenshots: (value: Boolean) -> Unit,
    onWriteSystemFont: (value: Boolean) -> Unit,
    onWriteUseBiometrics: (value: Boolean) -> Unit,
    onPort: () -> Unit
) {
    val context = LocalContext.current
    val count = if (password.value.isNotBlank()) 8 else 7

    val activity = LocalActivity.current!!
    val resources = LocalResources.current
    var onBiometricSuccess by remember { mutableStateOf({}) }
    var onBiometricError by remember { mutableStateOf({}) }

    val launcher = rememberAuthenticationLauncher(
        resultCallback = biometricCallback(
            activity = activity,
            onSuccess = {
                onBiometricSuccess()
            },
            onError = {
                if (password.value.isNotEmpty()) onBiometricError()
            }
        )
    )


    when {
        uiState.showChangePasswordDialog -> {
            ChangePasswordDialog(
                onDismissRequest = {
                    uiState.showChangePasswordDialog = false
                },
                onSuccess = { password ->
                    onWritePassword(password)

                    uiState.showChangePasswordDialog = false
                    Toast.makeText(context, R.string.password_set_successfully, Toast.LENGTH_SHORT)
                        .show()
                },
                password = password.value
            )
        }

        uiState.showRemovePasswordDialog -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showRemovePasswordDialog = false
                },
                onSuccess = {
                    onWritePassword("")

                    uiState.showRemovePasswordDialog = false
                    Toast.makeText(
                        context,
                        R.string.password_removed_successfully,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                password = password.value
            )
        }

        uiState.showConfirmPassswordDialog -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showConfirmPassswordDialog = false
                },
                onSuccess = {
                    uiState.showEncryptionKeys = true
                    uiState.showConfirmPassswordDialog = false
                },
                password = password.value
            )
        }

        uiState.showConfirmPassswordDialogForNotesNames -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showConfirmPassswordDialogForNotesNames = false
                },
                onSuccess = {
                    onWriteShowNotesNames(true)
                    uiState.showConfirmPassswordDialogForNotesNames = false
                },
                password = password.value
            )
        }

        uiState.showConfirmPasswordDialogForPort -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showConfirmPasswordDialogForPort = false
                },
                onSuccess = {
                    onPort()
                    uiState.showConfirmPasswordDialogForPort = false
                },
                password = password.value
            )
        }

        uiState.showConfirmPasswordDialogForBiometrics -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showConfirmPasswordDialogForBiometrics = false
                },
                onSuccess = {
                    onWriteUseBiometrics(true)
                    uiState.showConfirmPasswordDialogForBiometrics = false
                },
                password = password.value
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_pref_screenshots)) },
                trailing = {
                    Switch(
                        checked = disableScreenshots.value,
                        onCheckedChange = {
                            onWriteDisableScreenshots(it)
                        },
                    )
                },
                index = 0,
                count = count
            )
        }

        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_pref_font)) },
                trailing = {
                    Switch(
                        checked = useSystemFont.value,
                        onCheckedChange = {
                            onWriteSystemFont(it)
                        },
                    )
                },
                index = 1,
                count = count
            )
        }


        item {
            SettingsItem(
                text = { Text(stringResource(R.string.show_notes_names)) },
                trailing = {
                    Switch(
                        checked = showNotesNames.value,
                        onCheckedChange = {
                            onBiometricSuccess = { onWriteShowNotesNames(it) }
                            onBiometricError = { uiState.showConfirmPassswordDialogForNotesNames = true }

                            settingsItemOnClick(
                                onAction = onBiometricSuccess,
                                onActionWithPassword = onBiometricError,
                                password = password,
                                useBiometrics = useBiometrics.value,
                                resc = resources,
                                launcher = launcher
                            )
                        },
                    )
                },
                index = 2,
                count = count
            )
        }

        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_pref_use_biometrics)) },
                trailing = {
                    Switch(
                        checked = useBiometrics.value,
                        onCheckedChange = {
                            onBiometricSuccess = { onWriteUseBiometrics(it) }
                            onBiometricError = { uiState.showConfirmPasswordDialogForBiometrics = true }

                            settingsItemOnClick(
                                onAction = onBiometricSuccess,
                                onActionWithPassword = onBiometricError,
                                password = password,
                                useBiometrics = useBiometrics.value,
                                resc = resources,
                                launcher = launcher
                            )
                        },
                        enabled = Build.VERSION.SDK_INT >= 30 && checkBiometrics(context)
                    )
                },
                enabled = Build.VERSION.SDK_INT >= 30 && checkBiometrics(context),
                index = 3,
                count = count
            )
        }

        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_encryption_keys)) },
                onClick = {
                    when (uiState.showEncryptionKeys) {
                        true -> {
                            uiState.showEncryptionKeys = false
                        }

                        false -> {
                            onBiometricSuccess = { uiState.showEncryptionKeys = true }
                            onBiometricError = { uiState.showConfirmPassswordDialog = true }

                            settingsItemOnClick(
                                onAction = onBiometricSuccess,
                                onActionWithPassword = onBiometricError,
                                password = password,
                                useBiometrics = useBiometrics.value,
                                resc = resources,
                                launcher = launcher
                            )
                        }
                    }
                },
                index = 4,
                count = count
            )
        }

        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_pref_set_password)) },
                onClick = {
                    uiState.showChangePasswordDialog = true
                },
                index = 5,
                count = count
            )
        }

        if (password.value.isNotEmpty()) {
            item {
                SettingsItem(
                    text = { Text(stringResource(R.string.settings_pref_remove_password)) },
                    onClick = {
                        uiState.showRemovePasswordDialog = true
                    },
                    index = 6,
                    count = count
                )
            }
        }


        item {
            SettingsItem(
                text = { Text(stringResource(R.string.import_export_notes)) },
                onClick = {
                    onBiometricSuccess = { onPort() }
                    onBiometricError = { uiState.showConfirmPasswordDialogForPort = true }

                    settingsItemOnClick(
                        onAction = onBiometricSuccess,
                        onActionWithPassword = onBiometricError,
                        password = password,
                        useBiometrics = useBiometrics.value,
                        resc = resources,
                        launcher = launcher
                    )
                },
                index = if (password.value.isNotEmpty()) 7 else 6,
                count = count,
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
        }

        item {
            Column {
                Text(
                    text = stringResource(
                        R.string.build_info,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE,
                        Build.VERSION.SDK_INT
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (uiState.showEncryptionKeys) {
            item {
                Spacer(Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                6.dp, 6.dp, 6.dp, 6.dp
                            )
                        ),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    SelectionContainer(
                        Modifier
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        Text(
                            text = "Key: ${encryptionKey.value}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun settingsItemOnClick(
    onAction: () -> Unit?,
    onActionWithPassword: () -> Unit?,
    useBiometrics: Boolean,
    password: State<String>,
    resc: Resources,
    launcher: AuthenticationResultLauncher
) {
    when {
        useBiometrics -> {
            launcher.launch(
                biometricRequest(
                    title = resc.getString(R.string.biometric_title)
                ) {
                    setSubtitle(resc.getString(R.string.write_settings_item_pref))
                }
            )
        }

        !useBiometrics && password.value.isNotBlank() -> {
            onActionWithPassword()
        }

        !useBiometrics && password.value.isBlank() -> {
            onAction()
        }
    }
}