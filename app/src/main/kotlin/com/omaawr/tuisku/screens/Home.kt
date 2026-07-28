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
    val context = LocalContext.current
    val files = context.filesDir.listFiles()!!.filter { it.name.contains(".txt") }

    var showNewFileDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var navigateToTextEditor by remember { mutableStateOf(false) }
    var showDeleteFileDialog by remember { mutableStateOf(false) }
    var showPasswordDeletionDialog by remember { mutableStateOf(false) }
    var showFirstLaunchDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedFile: File? by remember { mutableStateOf(null) }
    var selectedFilePath by remember { mutableStateOf("") }
    var selectedFileContents: ByteArray? by remember { mutableStateOf(null) }

    viewModel.checkKeys()

    showFirstLaunchDialog = firstLaunch.value

    when {
        navigateToTextEditor -> onTextEditor(selectedFileContents!!, selectedFilePath)

        showDeleteFileDialog -> {
            DeleteFileDialog(
                onDismissRequest = {
                    showDeleteFileDialog = false
                    selectedFileContents = null
                },
                file = selectedFile!!
            )
        }

        showNewFileDialog -> {
            NewFileDialog(
                onDismissRequest = {
                    showNewFileDialog = false
                }
            )
        }

        showPasswordDialog -> {
            PasswordDialog(
                onDismissRequest = {
                    showPasswordDialog = false
                },
                onSuccess = {
                    navigateToTextEditor = true
                    showPasswordDialog = false
                },
                password = password.value
            )
        }

        showPasswordDeletionDialog -> {
            PasswordDialog(
                onDismissRequest = {
                    showPasswordDeletionDialog = false
                },
                onSuccess = {
                    showDeleteFileDialog = true
                    showPasswordDeletionDialog = false
                },
                password = password.value
            )
        }

        showFirstLaunchDialog -> {
            FirstLaunchDialog(
                onConfirmation = {
                    viewModel.writeFirstLaunch(false)
                    showFirstLaunchDialog = false
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
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showNewFileDialog = true
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
                                        showPasswordDialog = true
                                    } else {
                                        navigateToTextEditor = true
                                    }
                                },
                                onLongClick = {
                                    selectedFile = file
                                    selectedFilePath = file.absolutePath
                                    selectedFileContents = file.readBytes()

                                    if (password.value.isNotBlank()) {
                                        showPasswordDeletionDialog = true
                                    } else {
                                        showDeleteFileDialog = true
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