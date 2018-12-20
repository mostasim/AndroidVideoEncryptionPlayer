package com.example.libmedia.demo.video

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView

import fr.maxcom.http.HttpServer
import fr.maxcom.http.LocalSingleHttpServer

import javax.crypto.Cipher

/**
 * This class centralizes the code shared between variants of WebView Activities.
 * Refer to WebActivity1 and WebActivity2 for runnable activities.
 */
abstract class AWebActivity : Activity() {

    private var mServer: LocalSingleHttpServer? = null

    // to be implemented in concrete activities
    protected abstract val cipher: Cipher?
    protected abstract fun demo(webview: WebView, server: HttpServer)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webview = WebView(this)
        setContentView(webview)

        webview.settings.javaScriptEnabled = true

        try {
            mServer = LocalSingleHttpServer()
            val c = cipher
            if (c != null) {  // null means a clear video ; no need to set a decryption processing
                mServer!!.setCipher(c)
            }
            mServer!!.start()
            demo(webview, mServer!!)
        } catch (e: Exception) {  // exception management is not implemented in this demo code
            // Auto-generated catch block
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        // may be, do some cleanup in case of paused or currently playing
        if (mServer != null) {  // explicit is better
            mServer!!.stop()
            mServer = null
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "PlayActivity"
    }
}
