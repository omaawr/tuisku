package com.omaawr.tuisku.screens

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.biometric.AuthenticationRequest.Companion.biometricRequest
import androidx.biometric.compose.rememberAuthenticationLauncher
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omaawr.tuisku.R
import com.omaawr.tuisku.components.AnotherNoticeDialog
import com.omaawr.tuisku.components.DeleteFileDialog
import com.omaawr.tuisku.components.FirstLaunchDialog
import com.omaawr.tuisku.components.NewFileDialog
import com.omaawr.tuisku.components.Note
import com.omaawr.tuisku.components.NoteBottomSheet
import com.omaawr.tuisku.components.NoticeDialog
import com.omaawr.tuisku.components.PasswordDialog
import com.omaawr.tuisku.components.RenameFileDialog
import com.omaawr.tuisku.components.biometricCallback
import com.omaawr.tuisku.managers.EncryptionManager
import com.omaawr.tuisku.viewmodels.HomeViewModel
import com.omaawr.tuisku.viewmodels.SelectedFileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.File
import java.text.SimpleDateFormat
import kotlin.io.encoding.Base64

// home page doesnt use the stateless Content() function format because notes list won't reload properly when doing that for some reason(?)
// its fine though
// disclaimer: this code is an absolute mess

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
    onTextEditor: (fileContents: ByteArray, path: String) -> Unit,
    onSettings: () -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val encryptionManager = koinInject<EncryptionManager>()
    val uiState = viewModel.uiState

    val ctx = LocalContext.current
    val locale = LocalLocale.current.platformLocale
    val resc = LocalResources.current
    val activity = LocalActivity.current!!
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val ivKey = viewModel.ivKey.collectAsStateWithLifecycle(initialValue = "")

    val filesWithUnencryptedFilename =
        ctx.filesDir.listFiles()!!.filter { it.name.contains(".txt") }
    var files = ctx.filesDir.listFiles()!!.filter { it.name.contains(".encrypted-note") }

    val password = viewModel.notePassword.collectAsStateWithLifecycle(initialValue = "")
    val firstLaunch = viewModel.firstLaunch.collectAsStateWithLifecycle(initialValue = false)
    val showNotesNames = viewModel.showNotesNames.collectAsStateWithLifecycle(initialValue = true)
    val useBiometrics = viewModel.useBiometrics.collectAsStateWithLifecycle(initialValue = false).value && Build.VERSION.SDK_INT >= 30

    var navigateToTextEditor by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf(SelectedFileState()) }
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    var openingBottomSheet by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        // somehow theres a ui bug where some dialogs and bottom sheet stay even after navigating to another page
        // causing a NullPointerException on them since home page and their state is cleared
        onDispose {
            uiState.clear()
        }
    }

    val launcher = rememberAuthenticationLauncher(
        resultCallback = biometricCallback(
            activity = activity,
            onSuccess = {
                when {
                    openingBottomSheet -> uiState.showBottomSheet = true
                    !openingBottomSheet -> navigateToTextEditor = true
                }
            },
            onError = {
                when {
                    openingBottomSheet && password.value.isNotBlank() -> {
                        uiState.showPasswordForBottomSheet = true
                    }

                    !openingBottomSheet && password.value.isNotBlank() -> {
                        uiState.showPasswordDialog = true
                    }
                }
            }
        )
    )

    val noteOnClick = {
        when {
            useBiometrics -> {
                launcher.launch(
                    biometricRequest(
                        title = resc.getString(R.string.biometric_title)
                    ) {
                        setSubtitle(resc.getString(R.string.unlock_decrypt_note))
                    }
                )
            }

            !useBiometrics && password.value.isNotBlank() -> {
                if (openingBottomSheet) uiState.showPasswordForBottomSheet = true else uiState.showPasswordDialog = true
            }

            !useBiometrics && password.value.isBlank() -> {
                if (openingBottomSheet) uiState.showBottomSheet = true else navigateToTextEditor = true
            }
        }
    }

    uiState.showFirstLaunchDialog = firstLaunch.value

    when {
        filesWithUnencryptedFilename.isNotEmpty() -> {
            LaunchedEffect(Unit) {
                filesWithUnencryptedFilename.forEach { file ->
                    val encryptedFilename =
                        encryptionManager.encryptFilename(file.nameWithoutExtension.toByteArray())

                    File(ctx.filesDir, "${file.nameWithoutExtension}.txt").renameTo(
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

    when {
        navigateToTextEditor -> onTextEditor(selectedFile.contents!!, selectedFile.path!!)

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
        if (uiState.showBottomSheet) {
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
                }
            )
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
                                    file.absolutePath,
                                    file.readBytes()
                                )

                                openingBottomSheet = false

                                noteOnClick()
                            },
                            onLongClick = {
                                selectedFile = SelectedFileState(
                                    file,
                                    file.absolutePath,
                                    file.readBytes()
                                )

                                openingBottomSheet = true

                                noteOnClick()
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