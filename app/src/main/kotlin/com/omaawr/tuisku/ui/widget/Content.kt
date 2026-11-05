package com.omaawr.tuisku.ui.widget

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.omaawr.tuisku.R
import com.omaawr.tuisku.domain.manager.WidgetManager
import com.omaawr.tuisku.domain.manager.WidgetState
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Widget() {
    val widgetManager = koinInject<WidgetManager>()
    val uiState = widgetManager.uiState.collectAsState()

    when (uiState.value) {
        is WidgetState.Loading -> {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.widget_loading))
            }
        }

        is WidgetState.Loaded -> {
            Scaffold(
                titleBar = {
                    TitleBar(
                        title = (uiState.value as WidgetState.Loaded).decryptedFilename,
                        actions = {},
                        startIcon = ImageProvider(R.drawable.ic_file_present)
                    )
                }
            ) {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            (uiState.value as WidgetState.Loaded).decryptedContent
                        )
                    }
                }
            }
        }

        is WidgetState.Error -> {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.widget_error))
            }
        }
    }
}