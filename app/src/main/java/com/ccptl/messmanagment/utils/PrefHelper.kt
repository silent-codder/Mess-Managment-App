package com.ccptl.messmanagment.utils

import android.content.Context
import android.content.SharedPreferences
import com.ccptl.messmanagment.utils.Constants.Companion.PREFS_NAME

class PrefHelper(context: Context) {
    private var mSharedPref: SharedPreferences
    private val mEditor: SharedPreferences.Editor

    init {
        mSharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        mEditor = mSharedPref.edit()
    }

    fun put(key: String, value: String) {
        mEditor.putString(key, value).apply()
    }

    fun put(key: String, value: Boolean) {
        mEditor.putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return mSharedPref.getBoolean(key, defaultValue)
    }

    fun getString(key: String): String? {
        return mSharedPref.getString(key, null)
    }

    fun clearValue(key: String) {
        mEditor.remove(key).apply()
    }

    fun clear() {
        mEditor.clear().apply()
    }

}