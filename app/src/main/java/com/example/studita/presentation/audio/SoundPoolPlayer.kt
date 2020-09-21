package com.example.studita.presentation.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.studita.R

class SoundPoolPlayer(val context: Context) {
    private var shortPlayer: SoundPool? = null
    private val sounds: HashMap<Int, Int> = HashMap()
    fun playShortResource(piResource: Int) {
        shortPlayer?.load(context, piResource, 1)?.let {
            sounds.put(
                piResource,
                it
            )
        }
        shortPlayer?.setOnLoadCompleteListener { soundPool, sampleId, status ->
            val soundId = sounds[piResource] as Int
            if ((sampleId == soundId) and (status == 0))
                soundPool.play(soundId, 1f, 1f, 0, 0, 1F)
        }
    }

    // Cleanup
    fun release() { // Cleanup
        shortPlayer?.release()
        shortPlayer = null
    }

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        shortPlayer = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(attributes)
            .build()
    }
}