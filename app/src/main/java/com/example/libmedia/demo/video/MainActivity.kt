package com.example.libmedia.demo.video

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.view.View

import fr.maxcom.libmedia.Licensing

/**
 * The main activity of the application.
 * Is used to start another activity.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Libmedia requirement
        Licensing.allow(applicationContext)
        // optional
        Licensing.setDeveloperMode(true)
    }

    //----- onClick Handlers -----
    fun onClickPlayVideo1(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, VideoActivity1::class.java))
    }

    fun onClickPlayVideo2(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, VideoActivity2::class.java))
    }

    fun onClickPlayWeb1(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, WebActivity1::class.java))
    }

    fun onClickPlayWeb2(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, WebActivity2::class.java))
    }

    fun onClickStorage(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, StorageActivity::class.java))
    }
}
