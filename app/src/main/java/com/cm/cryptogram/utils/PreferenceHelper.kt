package com.cm.cryptogram.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


class PreferenceHelper(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    var loginState : Boolean
        get() = preferences.getBoolean("KEY_LOGIN_STATE", false)
        set(value) = preferences.edit() {
            putBoolean("KEY_LOGIN_STATE", value)
        }

    var loginInfo : String?
        get() = preferences.getString("KEY_USER", null)
        set(value) = preferences.edit() {
            putString("KEY_USER", value)
        }

    var userPrefInfo : String?
        get() = preferences.getString("KEY_USER_PREF", null)
        set(value) = preferences.edit() {
            putString("KEY_USER_PREF", value)
        }

    var keyWord : String?
        get() = preferences.getString("KEY_KEYWORD", null)
        set(value) = preferences.edit() {
            putString("KEY_KEYWORD", value)
        }


}