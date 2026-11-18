package tn.isetr.safewalk.ui

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapView(location: Location?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var pageLoaded by remember { mutableStateOf(false) }

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            WebView.setWebContentsDebuggingEnabled(true)

            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(m: ConsoleMessage): Boolean {
                    Log.d("SafeWalkMap", "JS: ${m.message()} (${m.sourceId()}:${m.lineNumber()})")
                    return true
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)

                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.d("SafeWalkMap", "Page chargée: $url")
                    pageLoaded = true
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    Log.e("SafeWalkMap", "Erreur: ${request.url} -> ${error.description}")
                }

                override fun onReceivedHttpError(
                    view: WebView,
                    request: WebResourceRequest,
                    errorResponse: WebResourceResponse
                ) {
                    Log.e("SafeWalkMap", "HTTP ${errorResponse.statusCode}: ${request.url}")
                }
            }

            loadUrl("https://appassets.androidplatform.net/assets/map.html")
        }
    }

    AndroidView(factory = { webView }, modifier = modifier)

    LaunchedEffect(location, pageLoaded) {
        if (pageLoaded && location != null) {
            webView.evaluateJavascript(
                "updatePosition(${location.latitude}, ${location.longitude})",
                null
            )
        }
    }
}