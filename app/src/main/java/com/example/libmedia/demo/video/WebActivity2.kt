package com.example.libmedia.demo.video

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient

import fr.maxcom.http.HttpServer

import java.security.GeneralSecurityException

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * A variant that plays an encrypted video.
 */
class WebActivity2 : AWebActivity() {

    override val cipher: Cipher?
        @Throws(GeneralSecurityException::class)
        get() {
            val c = Cipher.getInstance("ARC4")  // NoSuchAlgorithmException, NoSuchPaddingException
            c.init(Cipher.DECRYPT_MODE, SecretKeySpec("BrianIsInTheKitchen".toByteArray(), "ARC4"))  // InvalidKeyException
            return c
        }

    // to have a self-contained application, get the media from the assets directory.
    private val path = "asset://encrypted.mp4"

    /**
     * A demo with the JavaScript-driven design:
     * The HTML page has some embedded interaction widgets to allow the user to select a media.
     *
     * See the WebActivity1 for the Host-driven design.
     */
    @SuppressLint("AddJavascriptInterface")
    override fun demo(webview: WebView, server: HttpServer) {
        // this setting is only to simulate a user interaction for the demo
        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:setPath(); void(0);")
            }
        }

        webview.addJavascriptInterface(server.jsInterfaceObject, "serverObject")
        val page = """
<html><body>
 <script>
  function setURL(url) { document.getElementById('v_id').src = url; }
  function setPath() { setURL(serverObject.getURL('$path')); }
 </script>
 <video id="v_id" controls></video>
</body></html>
"""
        webview.loadData(page, "text/html", null)
    }
}
