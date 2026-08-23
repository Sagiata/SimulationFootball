package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sin

object AudioEffectManager {

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    val isMuted: Boolean
        get() = !_isSoundEnabled.value

    private val _isMusicEnabled = MutableStateFlow(true)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            Log.e("AudioEffectManager", "Could not initialize ToneGenerator", e)
        }
    }

    fun toggleMute(): Boolean {
        _isSoundEnabled.value = !_isSoundEnabled.value
        return !_isSoundEnabled.value
    }

    fun toggleSound() {
        _isSoundEnabled.value = !_isSoundEnabled.value
    }

    fun toggleMusic() {
        _isMusicEnabled.value = !_isMusicEnabled.value
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
    }

    fun playClick() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            } catch (e: Exception) {
                // Ignore tone error
            }
        }
    }

    fun playConfirm() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
            } catch (e: Exception) {
                // Ignore tone error
            }
        }
    }

    fun playWhistle() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            try {
                playToneSweep(startFreq = 2600f, endFreq = 2900f, durationMs = 280)
            } catch (e: Exception) {
                toneGen?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 200)
            }
        }
    }

    fun playGoalCheer() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            try {
                // Synthesize goal fanfare chords
                playToneSequence(
                    listOf(
                        Triple(523f, 100, 0.4f), // C5
                        Triple(659f, 100, 0.5f), // E5
                        Triple(784f, 120, 0.6f), // G5
                        Triple(1046f, 350, 0.7f) // C6
                    )
                )
            } catch (e: Exception) {
                toneGen?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_L, 300)
            }
        }
    }

    fun playCardAlert() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 180)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun playNotification() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun playToneSweep(startFreq: Float, endFreq: Float, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)

        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val angle = 2.0 * Math.PI * currentFreq / sampleRate
            phase += angle
            // Add vibrato and envelope
            val envelope = when {
                progress < 0.1f -> progress / 0.1f
                progress > 0.8f -> (1.0f - progress) / 0.2f
                else -> 1.0f
            }
            val sample = (sin(phase) * 0.45 * envelope * Short.MAX_VALUE).toInt().toShort()
            buffer[i] = sample
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        Thread.sleep(durationMs.toLong() + 20)
        track.release()
    }

    private fun playToneSequence(tones: List<Triple<Float, Int, Float>>) {
        val sampleRate = 44100
        val totalMs = tones.sumOf { it.second }
        val numSamples = (totalMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)

        var offset = 0
        for ((freq, durationMs, amp) in tones) {
            val count = (durationMs * sampleRate) / 1000
            var phase = 0.0
            val angle = 2.0 * Math.PI * freq / sampleRate
            for (i in 0 until count) {
                if (offset + i < numSamples) {
                    phase += angle
                    val progress = i.toFloat() / count
                    val envelope = when {
                        progress < 0.1f -> progress / 0.1f
                        progress > 0.8f -> (1.0f - progress) / 0.2f
                        else -> 1.0f
                    }
                    buffer[offset + i] = (sin(phase) * amp * envelope * Short.MAX_VALUE).toInt().toShort()
                }
            }
            offset += count
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        Thread.sleep(totalMs.toLong() + 30)
        track.release()
    }
}
