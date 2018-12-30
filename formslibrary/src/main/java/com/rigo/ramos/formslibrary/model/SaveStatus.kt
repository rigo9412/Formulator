package com.rigo.ramos.formslibrary.model

import android.content.Context
import android.content.SharedPreferences

open class SaveStatus{


    companion object {

        private const val PREFS_FILENAME = "com.rigo.ramos.formslibrary.prefs"
        private const val INDEX_CURRENT_ACTION = "index_current_action"

        fun setCurrentIndex(context: Context, index: Int){
            val prefs: SharedPreferences? = context.getSharedPreferences(PREFS_FILENAME,0)
            val editor = prefs!!.edit()
            editor.putInt(INDEX_CURRENT_ACTION, index)
            editor.apply()
        }

        fun getCurrentIndex(context: Context): Int{
            val prefs: SharedPreferences? = context.getSharedPreferences(PREFS_FILENAME,0)
            return  prefs!!.getInt(INDEX_CURRENT_ACTION, -1)
        }


    }


}