package com.rakshasetu.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Encrypted backing store for all app preferences (shared keys, duress code,
 * SMS template, emergency number). First run migrates legacy plaintext prefs.
 */
object SecurePrefs {

    private const val SECURE_FILE = "rakshasetu_secure_prefs"
    private const val LEGACY_FILE = "rakshasetu_prefs"

    /** Pure decision the migration unit test pins. */
    fun shouldMigrate(plainExplainsData: Boolean, secureHasData: Boolean): Boolean =
        plainExplainsData && !secureHasData

    fun secureOf(context: Context): SharedPreferences = synchronized(this) {
        cached?.let { return@synchronized it }
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val secure = EncryptedSharedPreferences.create(
            context,
            SECURE_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        migrateIfNeeded(context, secure)
        cached = secure
        secure
    }

    @Volatile private var cached: SharedPreferences? = null

    private fun migrateIfNeeded(context: Context, secure: SharedPreferences) {
        val legacy = context.getSharedPreferences(LEGACY_FILE, Context.MODE_PRIVATE)
        val legacyHasData = legacy.all.isNotEmpty()
        val secureHasData = secure.all.isNotEmpty()
        if (!shouldMigrate(legacyHasData, secureHasData)) return
        legacy.all.forEach { (k, v) ->
            when (v) {
                is Boolean -> secure.edit().putBoolean(k, v).apply()
                is Int -> secure.edit().putInt(k, v).apply()
                is Long -> secure.edit().putLong(k, v).apply()
                is Float -> secure.edit().putFloat(k, v).apply()
                is String -> secure.edit().putString(k, v).apply()
                else -> Unit
            }
        }
        legacy.edit().clear().apply()
    }
}
