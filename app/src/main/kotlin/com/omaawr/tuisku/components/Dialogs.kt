package com.omaawr.tuisku.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.omaawr.tuisku.R
import java.io.File

@Composable
fun NewFileDialog(
    onDismissRequest: () -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    val ctx = LocalContext.current
    val error = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.new_note))
        },
        text = {
            OutlinedTextField(
                state = textFieldState,
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
                        !textFieldState.text.contains("/") -> {
                            File(ctx.filesDir, "${textFieldState.text}.txt").writeText("")
                            onDismissRequest()
                        }
                        else -> {
                            error.value = true
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
            OutlinedTextField(
                state = textFieldState,
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

    val error = remember { mutableStateOf(false) }
    val previousPasswordError = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.set_password))
        },
        text = {
            Column {
                if (password.isNotBlank()) {
                    OutlinedTextField(
                        state = previousPasswordTextFieldState,
                        label = {
                            Text(stringResource(R.string.previous_password))
                        },
                        isError = previousPasswordError.value
                    )
                }

                OutlinedTextField(
                    state = newPasswordState,
                    label = {
                        Text(stringResource(R.string.new_password))
                    },
                    isError = error.value
                )

                OutlinedTextField(
                    state = confirmTextFieldState,
                    label = {
                        Text(stringResource(R.string.confirm_password))
                    },
                    isError = error.value
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        previousPasswordTextFieldState.text.toString() != password -> previousPasswordError.value =
                            true

                        newPasswordState.text.toString().isEmpty() || confirmTextFieldState.text.toString().isEmpty() -> {
                            error.value = true
                        }

                        confirmTextFieldState.text.toString() == newPasswordState.text.toString() -> onSuccess(
                            newPasswordState.text.toString()
                        )

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