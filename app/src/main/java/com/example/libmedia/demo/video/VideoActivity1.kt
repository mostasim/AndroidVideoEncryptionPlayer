package com.example.libmedia.demo.video

import javax.crypto.Cipher

/**
 * A PlayActivity variant that plays a clear video.
 */
class VideoActivity1 : AVideoActivity() {

    /**
     * No need to return a cipher for a clear video.
     */
    override val cipher: Cipher? = null

    // to have a self-contained application, get the media from the assets directory.
    override val path = "asset://clear.mp4"
}
