/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omaawr.tuisku.R
import com.omaawr.tuisku.components.AnotherNoticeDialog
import com.omaawr.tuisku.components.DeleteFileDialog
import com.omaawr.tuisku.components.FirstLaunchDialog
import com.omaawr.tuisku.components.NewFileDialog
import com.omaawr.tuisku.components.Note
import com.omaawr.tuisku.components.NoteBottomSheet
import com.omaawr.tuisku.components.NoticeDialog
import com.omaawr.tuisku.components.RenameFileDialog
import com.omaawr.tuisku.domain.manager.EncryptionManager
import com.omaawr.tuisku.ui.viewmodels.HomeUiState
import com.omaawr.tuisku.ui.viewmodels.HomeViewModel
import com.omaawr.tuisku.ui.viewmodels.SelectedFileState
import com.omaawr.tuisku.widget.AppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.io.encoding.Base64
import kotlin.io.path.readAttributes

// disclaimer: this code can be an absolute mess
/**
 * Home page, usually containing the notes to navigate to
 *
 * @param modifier - Modifier for the hoem page (usually unused by default)
 * @param onTextEditor - Upon navigation to the text editor page.. usually having bytes and the file path passed, of course
 * @param onSettings - Upon navigation to settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    onTextEditor: (path: String) -> Unit,
    onSettings: () -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val encryptionManager = koinInject<EncryptionManager>()
    val uiState = viewModel.uiState

    val ctx = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val ivKey = viewModel.ivKey.collectAsStateWithLifecycle(initialValue = "")

    val filesWithUnencryptedFilename =
        ctx.filesDir.listFiles()!!.filter { it.name.contains(".txt") }
    var files = ctx.filesDir.listFiles()!!.filter { it.name.contains(".encrypted-note") }

    val firstLaunch = viewModel.firstLaunch.collectAsStateWithLifecycle(initialValue = false)
    val showNotesNames = viewModel.showNotesNames.collectAsStateWithLifecycle(initialValue = true)
    val notesToUseOnWidget = viewModel.notesToUseOnWidget.collectAsStateWithLifecycle(initialValue = emptyList())

    DisposableEffect(Unit) {
        // somehow theres a ui bug where some dialogs and bottom sheet stay even after navigating to another page
        // causing a NullPointerException on them since home page and their state is cleared
        onDispose {
            uiState.clear()
        }
    }

    when {
        filesWithUnencryptedFilename.isNotEmpty() -> {
            LaunchedEffect(Unit) {
                filesWithUnencryptedFilename.forEach { file ->
                    val encryptedFilename =
                        encryptionManager.encryptFilename(file.nameWithoutExtension.toByteArray())

                    File(
                        ctx.filesDir,
                        "${file.nameWithoutExtension}.txt"
                    ).renameTo(
                        File(ctx.filesDir, "$encryptedFilename.encrypted-note")
                    )
                }
            }

            uiState.showNoticeDialog = true
        }

        files.isEmpty() && ivKey.value.isNotEmpty() -> {
            viewModel.writeIvKey("")
        }

        files.isNotEmpty() && ivKey.value.isNotEmpty() -> {
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    files.forEachIndexed { index, file ->
                        encryptionManager.migrateFile(
                            index,
                            file
                        )
                    }

                    files = ctx.filesDir.listFiles()?.filter { it.name.contains(".encrypted-note") }
                        ?: emptyList()
                }

                uiState.showAnotherNoticeDialog = true
                viewModel.writeIvKey("")
            }
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
                            painter = painterResource(R.drawable.ic_settings),
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
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.new_note)
                )
            }
        },
    ) { innerPadding ->
        Content(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(16.dp),
            uiState = uiState,
            firstLaunch = firstLaunch,
            showNotesNames = showNotesNames,
            notesToUseOnWidget = notesToUseOnWidget.value,
            onTextEditor = { path ->
                onTextEditor(path)
            },
            encryptionManager = encryptionManager,
            onWriteFirstLaunch = { value ->
                viewModel.writeFirstLaunch(value)
            },
            onUseNoteOnWidget = { value ->
                viewModel.writeUseNoteOnWidget(value)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier,
    uiState: HomeUiState,
    firstLaunch: State<Boolean>,
    showNotesNames: State<Boolean>,
    notesToUseOnWidget: List<String>,
    onTextEditor: (path: String) -> Unit,
    encryptionManager: EncryptionManager,
    onWriteFirstLaunch: (value: Boolean) -> Unit,
    onUseNoteOnWidget: (value: List<String>) -> Unit
) {
    val ctx = LocalContext.current
    val resc = LocalResources.current
    val scope = rememberCoroutineScope()

    val files = ctx.filesDir.listFiles()!!.filter { it.name.contains(".encrypted-note") }

    var navigateToTextEditor by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf(SelectedFileState()) }
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    val localNotesList: MutableList<String> = notesToUseOnWidget.toMutableList()

    uiState.showFirstLaunchDialog = firstLaunch.value

    when {
        navigateToTextEditor -> onTextEditor(selectedFile.path!!)

        uiState.showAnotherNoticeDialog -> {
            AnotherNoticeDialog(
                onDismissRequest = {
                    uiState.showAnotherNoticeDialog = false
                }
            )
        }

        uiState.showNoticeDialog -> {
            NoticeDialog(
                onDismissRequest = {
                    uiState.showNoticeDialog = false
                }
            )
        }

        uiState.showDeleteFileDialog -> {
            LaunchedEffect(Unit) {
                sheetState.hide()
                uiState.showBottomSheet = false
            }

            DeleteFileDialog(
                onDismissRequest = {
                    uiState.showDeleteFileDialog = false
                },
                file = selectedFile.file!!,
                onRemoveFromWidgetList = {
                    if (localNotesList.contains(selectedFile.path!!)) {
                        localNotesList -= selectedFile.path!!
                        onUseNoteOnWidget(localNotesList)

                        scope.launch {
                            AppWidget().updateAll(ctx)
                        }
                    }
                }
            )
        }

        uiState.showNewFileDialog -> {
            NewFileDialog(
                onDismissRequest = {
                    uiState.showNewFileDialog = false
                }
            )
        }

        uiState.showRenameNoteDialog -> {
            LaunchedEffect(Unit) {
                sheetState.hide()
                uiState.showBottomSheet = false
            }

            RenameFileDialog(
                onDismissRequest = {
                    uiState.showRenameNoteDialog = false
                },
                file = selectedFile.file!!
            )

        }

        uiState.showFirstLaunchDialog -> {
            FirstLaunchDialog(
                onConfirmation = {
                    onWriteFirstLaunch(false)
                    uiState.showFirstLaunchDialog = false
                }
            )
        }

        uiState.showBottomSheet -> {
            NoteBottomSheet(
                onDismissRequest = {
                    uiState.showBottomSheet = false
                },
                sheetState = sheetState,
                onRenameNoteClick = {
                    uiState.showRenameNoteDialog = true
                },
                onDeleteNoteClick = {
                    uiState.showDeleteFileDialog = true
                },
                onUseNoteOnWidgetClick = {
                    if (!localNotesList.contains(selectedFile.path!!)) {
                        localNotesList += selectedFile.path!!
                        onUseNoteOnWidget(localNotesList)

                        Toast.makeText(ctx, resc.getString(R.string.added_note_to_widgets), Toast.LENGTH_SHORT).show()
                    } else {
                        localNotesList -= selectedFile.path!!
                        onUseNoteOnWidget(localNotesList)

                        Toast.makeText(ctx, resc.getString(R.string.removed_note_from_widgets), Toast.LENGTH_SHORT).show()
                    }

                    scope.launch {
                        sheetState.hide()
                        uiState.showBottomSheet = false
                        AppWidget().updateAll(ctx)
                    }
                }
            )
        }
    }

    LazyColumn(
        modifier = modifier,
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
                    items = files,
                    key = { file ->
                        val attr = file.toPath().readAttributes("fileKey").toString()
                        attr.substring(attr.indexOf("ino=") + 4, attr.indexOf(")"))
                    }
                ) { file ->
                    val timestamp = Instant.ofEpochMilli(file.lastModified())
                    val unFormattedDate = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault())
                    val date = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(unFormattedDate)

                    val decodedFilename = if (showNotesNames.value) {
                        produceState(initialValue = "", key1 = file.nameWithoutExtension) {
                            value = try {
                                encryptionManager.decryptBytes(
                                    Base64.UrlSafe.decode(file.nameWithoutExtension)
                                )
                            } catch (_: Exception) {
                                resc.getString(R.string.filename_error)
                            }
                        }
                    } else {
                        remember { mutableStateOf("***********") }
                    }

                    Note(
                        onClick = {
                            selectedFile = SelectedFileState(
                                file,
                                file.absolutePath
                            )
                            navigateToTextEditor = true
                        },
                        onLongClick = {
                            selectedFile = SelectedFileState(
                                file,
                                file.absolutePath
                            )
                            uiState.showBottomSheet = true
                        },
                        filename = decodedFilename.value,
                        date = date
                    )
                }
            }
        }
    }
}