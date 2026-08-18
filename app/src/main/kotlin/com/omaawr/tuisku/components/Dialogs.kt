package com.omaawr.tuisku.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.omaawr.tuisku.R
import com.omaawr.tuisku.managers.EncryptionManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import java.security.SecureRandom

@Composable
fun NewFileDialog(
    onDismissRequest: () -> Unit
) {
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val encryptionManager = koinInject<EncryptionManager>()
    val ctx = LocalContext.current
    val error = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.new_note))
        },
        text = {
            OutlinedTextField(
                state = textFieldState,
                inputTransformation = InputTransformation.maxLength(120),
                isError = error.value
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        textFieldState.text.isBlank() || textFieldState.text.contains("/") -> {
                            error.value = true
                        }

                        else -> {
                            scope.launch {
                                val secureRandom = SecureRandom()
                                val nonce = ByteArray(12)

                                secureRandom.nextBytes(nonce)

                                val filename = encryptionManager.encryptFilename("${textFieldState.text}".toByteArray())

                                if (File(ctx.filesDir, "$filename.encrypted-note").exists()) {
                                    error.value = true
                                } else {
                                    File(ctx.filesDir, "$filename.encrypted-note").writeBytes(nonce)
                                    onDismissRequest()
                                }
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun DeleteFileDialog(
    onDismissRequest: () -> Unit,
    file: File
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.delete_note))
        },
        text = {
            Text(
                text = stringResource(R.string.delete_note_desc)
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    file.delete()
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun RenameFileDialog(
    onDismissRequest: () -> Unit,
    file: File
) {
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val encryptionManager = koinInject<EncryptionManager>()
    val ctx = LocalContext.current
    val error = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.rename_note_bottom_sheet))
        },
        text = {
            OutlinedTextField(
                state = textFieldState,
                inputTransformation = InputTransformation.maxLength(120),
                isError = error.value
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        textFieldState.text.isBlank() || textFieldState.text.contains("/") -> {
                            error.value = true
                        }

                        else -> {
                            scope.launch {
                                val filename = encryptionManager.encryptFilename("${textFieldState.text}".toByteArray())

                                file.renameTo(
                                    File(ctx.filesDir, "$filename.encrypted-note")
                                )
                                onDismissRequest()

                                Toast.makeText(ctx, R.string.note_renamed_successfully, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun NoticeDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.notice_dialog_title))
        },
        text = {
            Text(
                text = stringResource(R.string.notice_dialog_desc)
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun AnotherNoticeDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.another_notice_dialog_title))
        },
        text = {
            Text(
                text = stringResource(R.string.another_notice_dialog_desc)
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun PasswordDialog(
    onDismissRequest: () -> Unit,
    onSuccess: () -> Unit,
    password: String,
) {
    val textFieldState = rememberTextFieldState()
    val error = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.enter_password))
        },
        text = {
            OutlinedSecureTextField(
                state = textFieldState,
                isError = error.value,
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        textFieldState.text.toString() == password -> onSuccess()
                        else -> error.value = true
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    onDismissRequest: () -> Unit,
    onSuccess: (String) -> Unit,
    password: String,
) {
    val previousPasswordTextFieldState = rememberTextFieldState()
    val newPasswordState = rememberTextFieldState()
    val confirmTextFieldState = rememberTextFieldState()

    val newPasswordError = remember { mutableStateOf(false) }
    val confirmPasswordError = remember { mutableStateOf(false) }
    val previousPasswordError = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.set_password))
        },
        text = {
            Column {
                if (password.isNotBlank()) {
                    OutlinedSecureTextField(
                        state = previousPasswordTextFieldState,
                        label = {
                            Text(stringResource(R.string.previous_password))
                        },
                        isError = previousPasswordError.value
                    )
                }

                OutlinedSecureTextField(
                    state = newPasswordState,
                    label = {
                        Text(stringResource(R.string.new_password))
                    },
                    isError = newPasswordError.value
                )

                OutlinedSecureTextField(
                    state = confirmTextFieldState,
                    label = {
                        Text(stringResource(R.string.confirm_password))
                    },
                    isError = confirmPasswordError.value
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    previousPasswordError.value = false
                    newPasswordError.value = false
                    confirmPasswordError.value = false

                    when {
                        previousPasswordTextFieldState.text != password -> previousPasswordError.value = true

                        newPasswordState.text.isEmpty() -> {
                            newPasswordError.value = true
                        }

                        confirmTextFieldState.text.isEmpty() -> {
                            confirmPasswordError.value = true
                        }

                        newPasswordState.text != confirmTextFieldState.text -> {
                            newPasswordError.value = true
                            confirmPasswordError.value = true
                        }

                        confirmTextFieldState.text == newPasswordState.text -> onSuccess(
                            newPasswordState.text.toString()
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun FirstLaunchDialog(
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.introduction_header))
        },
        text = {
            Text(text = stringResource(R.string.introduction_text))
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Okay")
            }
        }
    )
}