package com.byteutility.dev.quickfill.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

object ClipboardHelper {

    fun copyToClipboard(context: Context, label: String, text: String, showToast: Boolean = true) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        val clip = ClipData.newPlainText(label.ifBlank { "QuickFill" }, text)
        clipboard.setPrimaryClip(clip)

        performHaptic(context)

        // Android 13+ (API 33+) shows its own clipboard popup overlay, so only show toast on older versions if requested
        if (showToast && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(context, "Copied \"$label\" to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performHaptic(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(40)
                }
            }
        } catch (_: Exception) {
            // Haptic might be unavailable or disabled on some devices
        }
    }
}
