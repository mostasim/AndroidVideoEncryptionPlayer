package com.example.libmedia.demo.video

import java.security.GeneralSecurityException

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * A PlayActivity variant that plays an encrypted video.
 */
class VideoActivity2 : AVideoActivity() {

    override val cipher: Cipher?
        @Throws(GeneralSecurityException::class)
        get() {
            val c = Cipher.getInstance("ARC4")  // NoSuchAlgorithmException, NoSuchPaddingException
            c.init(Cipher.DECRYPT_MODE, SecretKeySpec("BrianIsInTheKitchen".toByteArray(), "ARC4"))  // InvalidKeyException
            return c
        }

    // to have a self-contained application, get the media from the assets directory.
    override val path = "asset://encrypted.mp4"
}
