package com.omaawr.tuisku.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
import com.omaawr.tuisku.components.ShareFile
import com.omaawr.tuisku.viewmodels.TextEditorUiState
import com.omaawr.tuisku.viewmodels.TextEditorViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TextEditor(
    modifier: Modifier = Modifier,
    bytes: ByteArray,
    path: String,
    onBack: () -> Unit
) {
    val viewModel: TextEditorViewModel = koinViewModel()
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val decryptedData = viewModel.decrypt(bytes).collectAsStateWithLifecycle(initialValue = null)

    val textFieldState = rememberTextFieldState()
    val shareFile = remember { mutableStateOf(false) }

    when {
        decryptedData.value !== null -> {
            LaunchedEffect(Unit) {
                textFieldState.setTextAndPlaceCursorAtEnd(decryptedData.value!!)
            }

            uiState.loading = false
            uiState.loaded = true
        }
        decryptedData.value == null -> {
            uiState.loading = true
        }
    }

    if (shareFile.value) ShareFile(textFieldState.text.toString(), context)

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
                    if (!uiState.loading) {
                        IconButton(onClick = {
                            viewModel.encrypt(textFieldState.text.toString(), path)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_save),
                                contentDescription = stringResource(R.string.save_note)
                            )
                        }

                        IconButton(onClick = {
                            shareFile.value = true
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
            textFieldState,
            uiState,
            innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Content(
    state: TextFieldState,
    uiState: TextEditorUiState,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        when {
            uiState.loading -> {
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

            uiState.loaded -> {
                item {
                    TextField(
                        modifier = Modifier.fillParentMaxSize(),
                        placeholder = {
                            Text(stringResource(R.string.type_here))
                        },
                        state = state,
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