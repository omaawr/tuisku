package com.omawr.tuisku

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import com.omawr.tuisku.settings.Preferences
import com.omawr.tuisku.ui.theme.TuiskuTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settings: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settings.getDisableScreenshots()
            .launchInLifecycle(lifecycle) {
                when (it) {
                    true -> {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE
                        )
                    }

                    else -> {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
            }

        setContent {
            val useSystemFont =
                settings.getUseSystemFont().collectAsStateWithLifecycle(initialValue = false)

            TuiskuTheme(
                useSystemFont = useSystemFont.value
            ) {
                App()
            }
        }
    }
}

// https://github.com/X1nto/Mauth/blob/67047ab457e98b09c6bdfc8c8406f2c3e8fb5a8b/app/src/main/java/com/xinto/mauth/util/Coroutines.kt#L20
fun <T> Flow<T>.launchInLifecycle(
    lifecycle: Lifecycle,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
): Job {
    return flowWithLifecycle(lifecycle, state)
        .onEach(action)
        .launchIn(lifecycle.coroutineScope)
}