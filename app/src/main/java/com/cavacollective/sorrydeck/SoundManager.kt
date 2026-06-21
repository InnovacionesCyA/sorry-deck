package com.cavacollective.sorrydeck

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/** Loads and plays the short sound effects with low latency. */
class SoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val flipId: Int = soundPool.load(context, R.raw.card_flip, 1)
    private val sorryId: Int = soundPool.load(context, R.raw.sorry_sting, 1)
    private val powerUpId: Int = soundPool.load(context, R.raw.power_up, 1)
    private val backwardId: Int = soundPool.load(context, R.raw.backward, 1)

    /** Default card flip swoosh. */
    fun playFlip() {
        soundPool.play(flipId, 1f, 1f, 1, 0, 1f)
    }

    /** Sorry! card whomp-whomp. */
    fun playSorry() {
        soundPool.play(sorryId, 1f, 1f, 1, 0, 1f)
    }

    /** "Draw again" arcade chime for card 2. */
    fun playPowerUp() {
        soundPool.play(powerUpId, 1f, 1f, 1, 0, 1f)
    }

    /** Descending tones for card 4 (going backward). */
    fun playBackward() {
        soundPool.play(backwardId, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Picks the appropriate sound for the drawn card. Sorry! / 2 / 4 get their own
     * effects layered after the flip swoosh; all other cards get just the swoosh.
     */
    fun playForCard(card: Card) {
        // Always play the flip swoosh on every draw
        playFlip()
        when {
            card.isSorry -> playSorry()
            card.label == "2" -> playPowerUp()
            card.label == "4" -> playBackward()
        }
    }

    fun release() {
        soundPool.release()
    }
}
