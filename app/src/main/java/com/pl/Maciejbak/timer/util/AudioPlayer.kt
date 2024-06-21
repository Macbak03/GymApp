package com.pl.Maciejbak.timer.util

import android.content.Context
import android.media.MediaPlayer

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context, resourceId: Int) {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
            }
            mediaPlayer = null
        }

        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.start()
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
        }
    }

    fun stopSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
            mediaPlayer = null
        }
    }
}
