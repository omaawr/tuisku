package com.omaawr.tuisku.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omaawr.tuisku.R
import com.omaawr.tuisku.viewmodels.PortViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Import/export page.. used for exporting/importing either unencrypted/encrypted notes (obviously)
 *
 * @param onBack - Upon going back to the Settings page
 * @since 1.3.0
 */
@Composable
fun Port(
    onBack: () -> Unit
) {
    val viewModel: PortViewModel = koinViewModel()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val exportNotesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
        onResult = { uri ->
            if (uri != null) viewModel.exportNotes(uri)
        }
    )

    val importNotesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) viewModel.importNotes(uri)
        }
    )

    val exportUnencryptedNotesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
        onResult = { uri ->
            if (uri != null) viewModel.exportUnencryptedNotes(uri)
        }
    )

    val importUnencryptedNotes = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) viewModel.importUnencryptedNotes(uri)
        }
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.import_export_notes)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Content(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            onImport = {
                importNotesLauncher.launch(arrayOf("application/zip"))
            },
            onExport = {
                exportNotesLauncher.launch("notes.zip")
            },
            onImportUnencrypted = {
                importUnencryptedNotes.launch(arrayOf("application/zip"))
            },
            onExportUnencrypted = {
                exportUnencryptedNotesLauncher.launch("notes-unencrypted.zip")
            }
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onImportUnencrypted: () -> Unit,
    onExportUnencrypted: () -> Unit
) {
    val context = LocalContext.current
    val notesAreEmpty = context.filesDir.listFiles()!!.none { it.name.contains(".encrypted-note") }

    val listItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        trailingContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            ListItem(
                onClick = {
                    onImport()
                },
                colors = listItemColors,
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 4),
                content = {
                    Text(stringResource(R.string.import_notes))
                },
            )
        }

        item {
            ListItem(
                onClick = {
                    onExport()
                },
                colors = listItemColors,
                shapes = ListItemDefaults.segmentedShapes(index = 1, count = 4),
                content = {
                    Text(stringResource(R.string.export_notes))
                },
                enabled = !notesAreEmpty
            )
        }

        item {
            ListItem(
                onClick = {
                    onImportUnencrypted()
                },
                colors = listItemColors,
                shapes = ListItemDefaults.segmentedShapes(index = 2, count = 4),
                content = {
                    Text(stringResource(R.string.import_notes_unencrypted))
                },
            )
        }

        item {
            ListItem(
                onClick = {
                    onExportUnencrypted()
                },
                colors = listItemColors,
                shapes = ListItemDefaults.segmentedShapes(index = 3, count = 4),
                content = {
                    Text(stringResource(R.string.export_notes_unencrypted))
                },
                enabled = !notesAreEmpty
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
        }

        item {
            Text(
                text = stringResource(R.string.import_export_disclaimer),
                fontSize = 12.sp
            )
        }
    }
}