package com.cavacollective.sorrydeck

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper

/**
 * Loads and plays the short sound effects with low latency.
 *
 * Audio timing:
 * - Flip sound plays immediately on every draw and on history navigation.
 * - Card-specific sounds (2, 4, Sorry!) play AFTER a short delay so they
 *   layer in sequence rather than on top of the page-turn.
 */
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
    private val bonusId: Int = soundPool.load(context, R.raw.bonus, 1)

    private val handler = Handler(Looper.getMainLooper())

    /** Delay between the page-turn and the card-specific sound, in milliseconds. */
    private val accentDelayMs = 450L

    /** Page-turn swish (every draw and history step). */
    fun playFlip() {
        soundPool.play(flipId, 1f, 1f, 1, 0, 1f)
    }

    private fun playSorry() {
        soundPool.play(sorryId, 1f, 1f, 1, 0, 1f)
    }

    private fun playPowerUp() {
        soundPool.play(powerUpId, 1f, 1f, 1, 0, 1f)
    }

    private fun playBackward() {
        soundPool.play(backwardId, 1f, 1f, 1, 0, 1f)
    }

    private fun playBonus() {
        soundPool.play(bonusId, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Plays the page-turn immediately, then schedules the card-specific
     * accent sound after [accentDelayMs] for Sorry!, 2, and 4 cards.
     */
    fun playForCard(card: Card) {
        playFlip()
        val accent: (() -> Unit)? = when {
            card.isSorry -> ::playSorry
            card.label == "2" -> ::playPowerUp
            card.label == "4" -> ::playBackward
            card.label == "11" -> ::playBonus
            else -> null
        }
        if (accent != null) {
            handler.postDelayed({ accent.invoke() }, accentDelayMs)
        }
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        soundPool.release()
    }
}
