package com.omaawr.tuisku.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.omaawr.tuisku.components.NoticeDialog
import com.omaawr.tuisku.components.PasswordDialog
import com.omaawr.tuisku.components.RenameFileDialog
import com.omaawr.tuisku.managers.EncryptionManager
import com.omaawr.tuisku.viewmodels.HomeViewModel
import com.omaawr.tuisku.viewmodels.SelectedFileState
import kotlinx.coroutines.flow.flow
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.File
import java.text.SimpleDateFormat
import kotlin.io.encoding.Base64

// home page doesnt use the stateless Content() function format because notes list won't reload properly when doing that for some reason(?)
// its fine though
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    onTextEditor: (fileContents: ByteArray, path: String) -> Unit,
    onSettings: () -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val encryptionManager = koinInject<EncryptionManager>()
    val uiState = viewModel.uiState

    val ctx = LocalContext.current
    val locale = LocalLocale.current.platformLocale
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val filesWithUnencryptedFilename = ctx.filesDir.listFiles()!!.filter { it.name.contains(".txt") }
    val files = ctx.filesDir.listFiles()!!.filter { it.name.contains(".encrypted-note") }

    val password = viewModel.notePassword.collectAsStateWithLifecycle(initialValue = "")
    val firstLaunch = viewModel.firstLaunch.collectAsStateWithLifecycle(initialValue = false)
    val showNotesNames = viewModel.showNotesNames.collectAsStateWithLifecycle(initialValue = true)

    var navigateToTextEditor by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf(SelectedFileState()) }
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)

    uiState.showFirstLaunchDialog = firstLaunch.value

    if (filesWithUnencryptedFilename.isNotEmpty()) {
        LaunchedEffect(Unit) {
            filesWithUnencryptedFilename.forEach { file ->
                val encryptedFilename = encryptionManager.encryptFilename(file.nameWithoutExtension.toByteArray())

                File(ctx.filesDir, "${file.nameWithoutExtension}.txt").renameTo(
                    File(ctx.filesDir, "$encryptedFilename.encrypted-note")
                )
            }
        }

        uiState.showNoticeDialog = true
    }

    when {
        navigateToTextEditor -> onTextEditor(selectedFile.contents!!, selectedFile.path!!)

        uiState.showNoticeDialog -> {
            NoticeDialog(
                onDismissRequest = {
                    uiState.showNoticeDialog = false
                }
            )
        }

        uiState.showDeleteFileDialog -> {
            DeleteFileDialog(
                onDismissRequest = {
                    uiState.showDeleteFileDialog = false
                    uiState.showBottomSheet = false

                    selectedFile.contents = null
                },
                file = selectedFile.file!!
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

        uiState.showPasswordForBottomSheet -> {
            PasswordDialog(
                onDismissRequest = {
                    uiState.showPasswordForBottomSheet = false
                },
                onSuccess = {
                    uiState.showBottomSheet = true
                    uiState.showPasswordForBottomSheet = false
                },
                password = password.value
            )
        }

        uiState.showRenameNoteDialog -> {
            RenameFileDialog(
                onDismissRequest = {
                    uiState.showRenameNoteDialog = false
                    uiState.showBottomSheet = false
                },
                file = selectedFile.file!!
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
        val colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            trailingIconColor = MaterialTheme.colorScheme.onSurface,
            headlineColor = MaterialTheme.colorScheme.onSurface
        )

        if (uiState.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    uiState.showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ListItem(
                        content = {
                            Text(stringResource(R.string.rename_note_bottom_sheet))
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            uiState.showRenameNoteDialog = true
                        },
                        colors = colors
                    )

                    ListItem(
                        content = {
                            Text(
                                text = stringResource(R.string.delete_note_bottom_sheet),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            uiState.showDeleteFileDialog = true
                        },
                        colors = colors
                    )
                }
            }
        }

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
                    items(
                        items = files
                    ) { file ->
                        val date =
                            SimpleDateFormat("dd/MM/yyyy", locale).format(file.lastModified())

                        val decodedFilename = if (showNotesNames.value) {
                            flow {
                                emit(
                                    encryptionManager.decryptFile(
                                        Base64.UrlSafe.decode(file.nameWithoutExtension)
                                    )
                                )
                            }.collectAsStateWithLifecycle(initialValue = "")
                        } else {
                            remember { mutableStateOf("***********") }
                        }

                        Note(
                            onClick = {
                                selectedFile = SelectedFileState(
                                    file,
                                    file.absolutePath,
                                    file.readBytes()
                                )

                                if (password.value.isNotBlank()) uiState.showPasswordDialog =
                                    true else navigateToTextEditor = true
                            },
                            onLongClick = {
                                selectedFile = SelectedFileState(
                                    file,
                                    file.absolutePath,
                                    file.readBytes()
                                )

                                if (password.value.isNotBlank()) uiState.showPasswordForBottomSheet =
                                    true else uiState.showBottomSheet = true
                            },
                            filename = decodedFilename.value,
                            date = date
                        )
                    }
                }
            }
        }
    }
}