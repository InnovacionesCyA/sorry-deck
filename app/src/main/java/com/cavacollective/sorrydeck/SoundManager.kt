package com.cavacollective.sorrydeck

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/** Loads and plays the short sound effects with low latency. */
class SoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val flipId: Int = soundPool.load(context, R.raw.card_flip, 1)
    private val sorryId: Int = soundPool.load(context, R.raw.sorry_sting, 1)

    fun playFlip() {
        soundPool.play(flipId, 1f, 1f, 1, 0, 1f)
    }

    fun playSorry() {
        soundPool.play(sorryId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
