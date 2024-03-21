package com.sendbird.uikit.internal.extensions

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import com.sendbird.uikit.R

private val uiThreadHandler by lazy { Handler(Looper.getMainLooper()) }

internal fun <T> T?.runOnUiThread(block: (T) -> Unit) {
    if (this != null) {
        uiThreadHandler.post { block(this) }
    }
}

// Returns Theme Context from defStyle

internal fun Context.toComponentListContextThemeWrapper(): Context {
    return this.toContextThemeWrapper(R.attr.sb_component_list)
}

internal fun Context.toContextThemeWrapper(defStyle: Int): Context {
    val values = TypedValue()
    this.theme.resolveAttribute(defStyle, values, true)
    return ContextThemeWrapper(this, values.resourceId)
}

/**
 * Function for Checking Media Volume Is '0'
 */
fun isVolumeMuted(context: Context): Boolean {
    return if ((context.getSystemService(Context.AUDIO_SERVICE) as
            AudioManager).getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
        Toast.makeText(context, R.string.sb_low_volume, Toast.LENGTH_SHORT).show();
        true
    } else false
}