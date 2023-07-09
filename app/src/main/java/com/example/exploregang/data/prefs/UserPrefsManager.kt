package com.example.exploregang.data.prefs

import android.content.Context

object UserPrefsManager {
    private const val keyEmail = "email"
    private const val prefsName = "userpref"


    fun setUserEmailPref(email: String?,context: Context) {
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(keyEmail, email)
        editor.apply()
    }

    fun removeUserEmailPref(context: Context) {
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(keyEmail)
        editor.apply()
    }

    fun getUserEmailPref(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(keyEmail, "")
    }
    fun isUserEmailPrefSaved(context: Context):Boolean{
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(keyEmail, "")?.isNotEmpty()!!
    }
}