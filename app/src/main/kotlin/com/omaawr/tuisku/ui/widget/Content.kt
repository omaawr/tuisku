package com.omaawr.tuisku.ui.widget

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.omaawr.tuisku.MainActivity
import com.omaawr.tuisku.R
import com.omaawr.tuisku.domain.manager.WidgetManager
import com.omaawr.tuisku.domain.manager.WidgetState
import org.koin.compose.koinInject

/**
 * Widget content, this usually displays notes, or an error if something went wrong
 *
 * @since 1.4.1
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Widget() {
    val widgetManager = koinInject<WidgetManager>()
    val uiState = widgetManager.uiState.collectAsState()
    val ctx = LocalContext.current
    val res = ctx.resources // no LocalResources in glance

    when (uiState.value) {
        is WidgetState.Loading -> {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = res.getString(R.string.widget_loading),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }
        }

        is WidgetState.Loaded -> {
            val map = (uiState.value as WidgetState.Loaded).notes
            val count = map.count()
            val index = remember { mutableIntStateOf(0) }
            val currentNoteSelected = map.entries.elementAt(index.intValue)

            Scaffold(
                titleBar = {
                    TitleBar(
                        title = currentNoteSelected.key,
                        actions = {
                            if (count > 1) {
                                CircleIconButton(
                                    imageProvider = ImageProvider(R.drawable.ic_auto_save),
                                    contentDescription = null,
                                    onClick = {
                                        val newIndex = index.intValue + 1

                                        if (newIndex < count) {
                                            index.intValue = newIndex
                                        } else {
                                            index.intValue = 0
                                        }
                                    }
                                )
                            }

                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.ic_edit),
                                contentDescription = null,
                                onClick = actionStartActivity<MainActivity>()
                            )
                        },
                        startIcon = ImageProvider(R.drawable.ic_file_present)
                    )
                }
            ) {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = currentNoteSelected.value,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface
                            )
                        )
                    }
                }
            }
        }

        is WidgetState.Error -> {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = res.getString(R.string.widget_error),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }
        }
    }
}