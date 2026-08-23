package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

object HapticController {

    fun performTactileClick(haptic: HapticFeedback?, context: Context? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        } catch (_: Exception) {}
        triggerVibration(context, 15, 60)
    }

    fun performSquadSwapSuccess(haptic: HapticFeedback?, context: Context? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) {}
        triggerVibration(context, 35, 180)
    }

    fun performGoalCelebration(haptic: HapticFeedback?, context: Context? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) {}
        triggerDoubleVibration(context)
    }

    fun performMatchKickoff(haptic: HapticFeedback?, context: Context? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) {}
        triggerVibration(context, 40, 200)
    }

    fun performTacticChange(haptic: HapticFeedback?, context: Context? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        } catch (_: Exception) {}
        triggerVibration(context, 20, 100)
    }

    private fun triggerVibration(context: Context?, durationMs: Long, amplitude: Int) {
        if (context == null) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val clampedAmplitude = amplitude.coerceIn(1, 255)
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, clampedAmplitude))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    private fun triggerDoubleVibration(context: Context?) {
        if (context == null) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 40, 60, 80)
                    val amplitudes = intArrayOf(0, 180, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 50, 70, 90), -1)
                }
            }
        } catch (_: Exception) {}
    }
}
