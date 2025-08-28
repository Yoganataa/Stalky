package io.github.yoganataa.stalky.data.network

import android.content.Context
import android.webkit.WebView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudflareInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    
    private val bypassedUrls = mutableSetOf<String>()
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        if (isCloudflareChallenge(response)) {
            return bypassCloudflare(request, chain)
        }
        
        return response
    }
    
    private fun isCloudflareChallenge(response: Response): Boolean {
        return response.code in 400..499 &&
                (response.header("server")?.contains("cloudflare", true) == true ||
                 response.header("cf-ray") != null ||
                 response.body?.string()?.contains("Checking your browser", true) == true)
    }
    
    private fun bypassCloudflare(request: Request, chain: Interceptor.Chain): Response {
        val url = request.url.toString()
        val domain = request.url.host
        
        if (bypassedUrls.contains(domain)) {
            return chain.proceed(request)
        }
        
        return runBlocking {
            val deferred = CompletableDeferred<String>()
            
            val webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.userAgentString = request.header("User-Agent") ?: 
                    "Mozilla/5.0 (Android 13; Mobile; rv:109.0) Gecko/120.0 Firefox/120.0"
                
                webViewClient = CloudflareWebViewClient { cookies ->
                    bypassedUrls.add(domain)
                    deferred.complete(cookies)
                }
            }
            
            webView.loadUrl(url)
            val cookies = deferred.await()
            
            val newRequest = request.newBuilder()
                .header("Cookie", cookies)
                .build()
                
            chain.proceed(newRequest)
        }
    }
}