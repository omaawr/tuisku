package com.omaawr.tuisku.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omaawr.tuisku.R
import com.omaawr.tuisku.components.handleEncryptedData
import com.omaawr.tuisku.components.shareFile
import com.omaawr.tuisku.viewmodels.TextEditorViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditor(
    modifier: Modifier = Modifier,
    bytes: ByteArray,
    path: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val viewModel: TextEditorViewModel = koinViewModel()

    val encryptionKey = viewModel.encryptionKey.collectAsStateWithLifecycle(initialValue = null)
    val ivKey = viewModel.ivKey.collectAsStateWithLifecycle(initialValue = null)

    val textFieldValue = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(encryptionKey.value, ivKey.value) {
        when {
            encryptionKey.value != null && ivKey.value != null -> {
                val decryptedData = handleEncryptedData(
                    bytes = bytes,
                    decrypt = true,
                    key = encryptionKey.value!!.toByteArray(),
                    iv = ivKey.value!!.toByteArray(),
                    filePath = path
                )

                textFieldValue.value = decryptedData!!
                isLoading.value = false
            }
            else -> {
                isLoading.value = true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    if (!isLoading.value) {
                        IconButton(onClick = {
                            handleEncryptedData(
                                data = textFieldValue.value,
                                bytes = bytes,
                                decrypt = false,
                                key = encryptionKey.value!!.toByteArray(),
                                iv = ivKey.value!!.toByteArray(),
                                filePath = path
                            )
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_save),
                                contentDescription = stringResource(R.string.save_note)
                            )
                        }

                        IconButton(onClick = {
                            shareFile(textFieldValue.value, context, activity)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_share),
                                contentDescription = stringResource(R.string.share_note)
                            )
                        }

                    }
                },
            )
        }
    ) { innerPadding ->
        Content(
            textFieldValue,
            isLoading,
            innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Content(
    textFieldValue: MutableState<String>,
    isLoading: MutableState<Boolean>,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding).fillMaxSize()
    ) {
        when (isLoading.value) {
            true -> {
                item {
                    Column(
                        modifier = Modifier.fillParentMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ContainedLoadingIndicator()
                    }
                }
            }

            false -> {
                item {
                    TextField(
                        modifier = Modifier.fillParentMaxSize(),
                        placeholder = {
                            Text(stringResource(R.string.type_here))
                        },
                        value = textFieldValue.value,
                        onValueChange = { textFieldValue.value = it },
                        colors = TextFieldDefaults.colors(
                            // i should probably do something about this
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}