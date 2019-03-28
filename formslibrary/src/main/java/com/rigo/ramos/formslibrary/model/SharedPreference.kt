package com.rigo.ramos.formslibrary.model

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) {


    val sharedPref: SharedPreferences = context.getSharedPreferences(Companion.PREFS_NAME, Context.MODE_PRIVATE)


    fun setColor(field: String ,value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(field, value)
        editor.commit()
    }

    fun getColor(KEY_NAME: String): Int? {
        return sharedPref.getInt(KEY_NAME, 0)
    }

    companion object {
        const val PREFS_NAME = "formulator"
        const val COLOR_PRIMARY = "COLOR_PRIMARY"
        const val COLOR_BACKGROUND = "COLOR_BACKGROUND"
        const val COLOR_ACCENT = "COLOR_ACCENT"
        const val COLOR_TEXT = "COLOR_TEXT"
    }


}