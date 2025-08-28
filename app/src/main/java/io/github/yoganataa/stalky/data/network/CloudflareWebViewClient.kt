package io.github.yoganataa.stalky.data.network

import android.webkit.WebView
import android.webkit.WebViewClient

class CloudflareWebViewClient(
    private val onBypassComplete: (String) -> Unit
) : WebViewClient() {
    
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        
        // Extract cookies after page load
        view?.evaluateJavascript("document.cookie") { cookies ->
            if (!cookies.isNullOrEmpty() && cookies != "\"\"") {
                onBypassComplete(cookies.trim('"'))
            }
        }
    }
}