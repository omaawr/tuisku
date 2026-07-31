package com.omaawr.tuisku.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omaawr.tuisku.R
import com.omaawr.tuisku.components.ChangePasswordDialog
import com.omaawr.tuisku.components.PasswordDialog
import com.omaawr.tuisku.components.SettingsItem
import com.omaawr.tuisku.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                .padding(innerPadding)
                .padding(16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
fun Content(
    modifier: Modifier
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val context = LocalContext.current
    val buttonText = remember { mutableStateOf("Show") }
    val uiState = viewModel.uiState
    
    val encryptionKey = viewModel.encryptionKey.collectAsStateWithLifecycle(initialValue = "")
    val ivKey = viewModel.ivKey.collectAsStateWithLifecycle(initialValue = "")
    val useSystemFont = viewModel.useSystemFont.collectAsStateWithLifecycle(initialValue = false)
    val disableScreenshots = viewModel.disableScreenshots.collectAsStateWithLifecycle(initialValue = false)
    val password = viewModel.password.collectAsStateWithLifecycle(initialValue = "")

    when {
        uiState.showChangePasswordDialog -> {
            ChangePasswordDialog(
                onDismissRequest = {
                    uiState.showChangePasswordDialog = false
                },
                onSuccess = { password ->
                    viewModel.writePassword(password)
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
                    viewModel.writePassword("")
                    uiState.showRemovePasswordDialog = false
                    Toast.makeText(context, R.string.password_removed_successfully, Toast.LENGTH_SHORT)
                        .show()
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
                    buttonText.value = "Hide"
                },
                password = password.value
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_pref_screenshots)) },
                trailing = {
                    Switch(
                        checked = disableScreenshots.value,
                        onCheckedChange = {
                            viewModel.writeDisableScreenshots(it)
                        },
                    )
                }
            )
        }

        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_pref_font)) },
                trailing = {
                    Switch(
                        checked = useSystemFont.value,
                        onCheckedChange = {
                            viewModel.writeUseSystemFont(it)
                        },
                    )
                }
            )
        }

        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_encryption_keys)) },
                trailing = {
                    Button(
                        onClick = {
                            when (uiState.showEncryptionKeys) {
                                true -> {
                                    uiState.showEncryptionKeys = false
                                    buttonText.value = "Show"
                                }

                                false -> {
                                    if (password.value.isNotBlank()) {
                                        uiState.showConfirmPassswordDialog = true
                                    } else {
                                        uiState.showEncryptionKeys = true
                                        buttonText.value = "Hide"
                                    }
                                }
                            }
                        }
                    ) {
                        Text(buttonText.value)
                    }
                }
            )
        }

        item {
            SettingsItem(
                text = { Text(stringResource(R.string.settings_pref_set_password)) },
                trailing = {
                    Button(
                        onClick = {
                            uiState.showChangePasswordDialog = true
                        }
                    ) {
                        Text(stringResource(R.string.settings_pref_set_password_button))
                    }
                }
            )
        }

        if (password.value.isNotEmpty()) {
            item {
                SettingsItem(
                    text = { Text(stringResource(R.string.settings_pref_remove_password)) },
                    trailing = {
                        Button(
                            onClick = {
                                uiState.showRemovePasswordDialog = true
                            }
                        ) {
                            Text(stringResource(R.string.settings_pref_remove_password_button))
                        }
                    }
                )
            }
        }


        if (uiState.showEncryptionKeys) {
            item {
                Column {
                    Text(
                        "Key: ${encryptionKey.value}"
                    )
                    Text(
                        "IV: ${ivKey.value}"
                    )
                }
            }
        }
    }
}