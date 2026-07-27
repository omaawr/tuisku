package com.omawr.tuisku.screens

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
import com.omawr.tuisku.R
import com.omawr.tuisku.components.DeleteFileDialog
import com.omawr.tuisku.components.NewFileDialog
import com.omawr.tuisku.components.Note
import com.omawr.tuisku.components.PasswordDialog
import com.omawr.tuisku.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    onTextEditor: (file: File) -> Unit,
    onSettings: () -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val locale = LocalLocale.current.platformLocale
    val context = LocalContext.current
    val files = context.filesDir.listFiles()!!.filter { it.name.contains(".txt") }

    var showDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var navigateToTextEditor by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDeletionDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedFile: File? by remember { mutableStateOf(null) }
    val password = viewModel.notePassword.collectAsStateWithLifecycle(initialValue = "")

    viewModel.checkKeys()

    when {
        navigateToTextEditor -> onTextEditor(selectedFile!!)

        showDeleteDialog -> {
            DeleteFileDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedFile = null
                },
                file = selectedFile!!
            )
        }

        showDialog -> {
            NewFileDialog(
                onDismissRequest = {
                    showDialog = false
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
                    showDeleteDialog = true
                    showPasswordDeletionDialog = false
                },
                password = password.value
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
                    showDialog = true
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
                                    if (password.value.isNotBlank()) {
                                        showPasswordDialog = true
                                    } else {
                                        navigateToTextEditor = true
                                    }
                                },
                                onLongClick = {
                                    selectedFile = file
                                    if (password.value.isNotBlank()) {
                                        showPasswordDeletionDialog = true
                                    } else {
                                        showDeleteDialog = true
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