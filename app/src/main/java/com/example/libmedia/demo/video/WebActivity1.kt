package com.example.libmedia.demo.video

import android.webkit.WebView
import android.webkit.WebViewClient

import fr.maxcom.http.HttpServer

import javax.crypto.Cipher

/**
 * A variant that plays a clear video.
 */
class WebActivity1 : AWebActivity() {

    /**
     * No need to return a cipher for a clear video.
     */
    override val cipher: Cipher? = null

    // to have a self-contained application, get the media from the assets directory.
    private val path = "asset://clear.mp4"

    /**
     * A demo with the Host-driven design:
     * The host application is the master and controls the video source.
     *
     * See the WebActivity2 for the JavaScript-driven design.
     */
    override fun demo(webview: WebView, server: HttpServer) {
        var path = path
        path = server.getURL(path)
        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:setURL('$path'); void(0);")
            }
        }
        val page = """
<html><body>
 <script>
  function setURL(url) { document.getElementById('v_id').src = url; }
 </script>
 <video id="v_id" controls></video>
</body></html>
"""
        webview.loadData(page, "text/html", null)
    }
}
