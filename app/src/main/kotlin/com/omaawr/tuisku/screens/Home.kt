package com.omaawr.tuisku.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omaawr.tuisku.R
import com.omaawr.tuisku.components.DeleteFileDialog
import com.omaawr.tuisku.components.FirstLaunchDialog
import com.omaawr.tuisku.components.NewFileDialog
import com.omaawr.tuisku.components.Note
import com.omaawr.tuisku.components.PasswordDialog
import com.omaawr.tuisku.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    onTextEditor: (fileContents: ByteArray, path: String) -> Unit,
    onSettings: () -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val password = viewModel.notePassword.collectAsStateWithLifecycle(initialValue = "")
    val firstLaunch = viewModel.firstLaunch.collectAsStateWithLifecycle(initialValue = false)
    val locale = LocalLocale.current.platformLocale
    val files = LocalContext.current.filesDir.listFiles()!!.filter { it.name.contains(".txt") }
    var navigateToTextEditor by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedFile: File? by remember { mutableStateOf(null) }
    var selectedFilePath by remember { mutableStateOf("") }
    var selectedFileContents: ByteArray? by remember { mutableStateOf(null) }

    viewModel.checkKeys()

    uiState.showFirstLaunchDialog = firstLaunch.value

    when {
        navigateToTextEditor -> onTextEditor(selectedFileContents!!, selectedFilePath)

        uiState.showDeleteFileDialog -> {
            DeleteFileDialog(
                onDismissRequest = {
                    uiState.showDeleteFileDialog = false
                    selectedFileContents = null
                },
                file = selectedFile!!
            )
        }

        uiState.showNewFileDialog -> {
            NewFileDialog(
                onDismissRequest = {
                    uiState.showNewFileDialog = false
                }
            )
        }

        uiState.showPasswordDialog -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showPasswordDialog = false
                },
                onSuccess = {
                    navigateToTextEditor = true
                    uiState.showPasswordDialog = false
                },
                password = password.value
            )
        }

        uiState.showPasswordDeleteFileDialog -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showPasswordDeleteFileDialog = false
                },
                onSuccess = {
                    uiState.showDeleteFileDialog = true
                    uiState.showPasswordDeleteFileDialog = false
                },
                password = password.value
            )
        }

        uiState.showFirstLaunchDialog -> {
            FirstLaunchDialog(
                onConfirmation = {
                    viewModel.writeFirstLaunch(false)
                    uiState.showFirstLaunchDialog = false
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { onSettings() }) {
                        Icon(
                            painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    uiState.showNewFileDialog = true
                },
            ) {
                Icon(painterResource(R.drawable.ic_edit), stringResource(R.string.new_note))
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                files.isEmpty() -> {
                    item {
                        Text(
                            "           __..--''``---....___   _..._    __\n" +
                                    " /// //_.-'    .-/\";  `        ``<._  ``.''_ `. / // /\n" +
                                    "///_.-' _..--.'_    \\                    `( ) ) // //\n" +
                                    "/ (_..-' // (< _     ;_..__               ; `' / ///\n" +
                                    " / // // //  `-._,_)' // / ``--...____..-' /// / //"
                        )

                        Text(stringResource(R.string.no_notes_found))
                    }
                }

                else -> {
                    for (file in files) {
                        val date = SimpleDateFormat("dd/MM/yyyy", locale).format(file.lastModified())

                        item {
                            Note(
                                onClick = {
                                    selectedFile = file
                                    selectedFilePath = file.absolutePath
                                    selectedFileContents = file.readBytes()

                                    if (password.value.isNotBlank()) {
                                        uiState.showPasswordDialog = true
                                    } else {
                                        navigateToTextEditor = true
                                    }
                                },
                                onLongClick = {
                                    selectedFile = file
                                    selectedFilePath = file.absolutePath
                                    selectedFileContents = file.readBytes()

                                    if (password.value.isNotBlank()) {
                                        uiState.showPasswordDeleteFileDialog = true
                                    } else {
                                        uiState.showDeleteFileDialog = true
                                    }
                                },
                                file = file,
                                date = date
                            )
                        }
                    }
                }
            }
        }
    }
}