package com.example.exploregang.data.prefs

import android.content.Context

object UserPrefsManager {
    private const val keyIsLoggedIn = "isLoggedIn"
    private const val prefsName = "userpreff"
    fun saveUserSession(context:Context){
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(keyIsLoggedIn, true)
        editor.apply()
    }
    fun quitUserSession(context:Context){
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(keyIsLoggedIn, false)
        editor.apply()
    }
    fun isUserLogged(context: Context):Boolean{
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(keyIsLoggedIn, false)
    }
    fun isFirstTimeLaunch(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isFirstTime = sharedPrefs.getBoolean("isFirstTime", true)

        // Si es la primera vez, actualizar el valor a falso para indicar que la aplicación ya se ha iniciado
        if (isFirstTime) {
            val editor = sharedPrefs.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()
        }

        return isFirstTime
    }

}