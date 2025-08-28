package io.github.yoganataa.stalky.utils

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import io.github.yoganataa.stalky.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class StalkyError(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    
    object NetworkError : StalkyError("Network connection failed")
    object CloudflareError : StalkyError("Cloudflare protection detected")
    class ScriptError(message: String) : StalkyError("Script error: $message")
    class ParseError(message: String) : StalkyError("Parse error: $message")
    class NotFoundError(message: String) : StalkyError("Not found: $message")
}

@Composable
fun HandleError(
    error: String?,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onErrorShown: () -> Unit = {}
) {
    val context = LocalContext.current
    
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getErrorMessage(context, it),
                    withDismissAction = true
                )
                onErrorShown()
            }
        }
    }
}

private fun getErrorMessage(context: Context, error: String): String {
    return when {
        error.contains("network", ignoreCase = true) -> "Network connection failed"
        error.contains("cloudflare", ignoreCase = true) -> "Site protection detected"
        error.contains("script", ignoreCase = true) -> "Script execution failed"
        error.contains("parse", ignoreCase = true) -> "Failed to parse content"
        else -> error.take(100) + if (error.length > 100) "..." else ""
    }
}