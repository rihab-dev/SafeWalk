package tn.isetr.safewalk.data

import android.content.Context

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("safewalk_settings", Context.MODE_PRIVATE)

    var message: String
        get() = prefs.getString(KEY_MSG, DEFAULT_MSG) ?: DEFAULT_MSG
        set(v) = prefs.edit().putString(KEY_MSG, v).apply()

    var confirmBeforeSend: Boolean
        get() = prefs.getBoolean(KEY_CONFIRM, true)
        set(v) = prefs.edit().putBoolean(KEY_CONFIRM, v).apply()

    companion object {
        const val DEFAULT_MSG = "SOS ! J'ai besoin d'aide. Voici ma position :"
        private const val KEY_MSG = "message"
        private const val KEY_CONFIRM = "confirm"
    }
}