package io.github.yunato.myankicard.other.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Preference(context: Context) {

    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var stamp: Long = sp.getLong(STATE_STAMP, PREFERENCE_INIT)
        set(value) {
            sp.edit().putLong(STATE_STAMP, value).apply()
            field = value
        }

    var primaryKey: Long = sp.getLong(PRAM_PRIMARY_KEY, -1L)
        set(value) {
            sp.edit().putLong(PRAM_PRIMARY_KEY, value).apply()
            field = value
        }


    fun removePrimaryKey() {
        sp.edit().remove(PRAM_PRIMARY_KEY).apply()
        primaryKey = -1L
    }

    companion object {
        @JvmStatic private val STATE_STAMP: String = "io.github.yunato.myankicard.ui.activity.STATE_STAMP"
        @JvmStatic private val PREFERENCE_INIT: Long = 0
        @JvmStatic val PRAM_PRIMARY_KEY = "io.github.yunato.myankicard.other.application.PRAM_PRIMARY_KEY"
    }
}
