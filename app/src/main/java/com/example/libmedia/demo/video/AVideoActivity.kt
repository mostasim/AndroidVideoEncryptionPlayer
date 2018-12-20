package com.example.libmedia.demo.video

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView

import fr.maxcom.http.LocalSingleHttpServer

import javax.crypto.Cipher

/**
 * This class centralizes the code shared between variants of VideoView Activities.
 * Refer to VideoActivity1 and VideoActivity2 for runnable activities.
 */
abstract class AVideoActivity : Activity(), MediaPlayer.OnCompletionListener {

    private lateinit var mVideoView: VideoView
    private var mServer: LocalSingleHttpServer? = null

    // to be implemented in concrete activities
    protected abstract val cipher: Cipher?
    protected abstract val path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        mVideoView = findViewById(R.id.vwVideo)
        mVideoView.setOnCompletionListener(this)
        mVideoView.setMediaController(MediaController(this))  // is optional
        try {
            mServer = LocalSingleHttpServer()
            val c = cipher
            if (c != null) {  // null means a clear video ; no need to set a decryption processing
                mServer!!.setCipher(c)
            }
            mServer!!.start()
            var path = path
            path = mServer!!.getURL(path)
            mVideoView.setVideoPath(path)
            mVideoView.start()
        } catch (e: Exception) {  // exception management is not implemented in this demo code
            // Auto-generated catch block
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        // do some cleanup in case of paused or currently playing
        mVideoView.stopPlayback()  // doesn't hurt if even !isPlaying()
        if (mServer != null) {  // onCompletion() may not have be called
            mServer!!.stop()
            mServer = null
        }
        super.onDestroy()
    }

    // MediaPlayer.OnCompletionListener interface
    override fun onCompletion(mp: MediaPlayer) {
        Log.d(TAG, "onCompletion")
        if (mServer != null) {
            mServer!!.stop()
            mServer = null
        }
        finish()  // or design a method like playNext()
    }

    companion object {
        private const val TAG = "PlayActivity"
    }
}
