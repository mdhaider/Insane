
package dev.nehal.insane.shared

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "InsanePref"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val USER_ID = Pair("user_id", "")
    private val IS_ADMIN=Pair("is_admin",false)


    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var userid: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(USER_ID.first, USER_ID.second)

        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putString(USER_ID.first, value)
        }

    var isAdmin: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getBoolean(IS_ADMIN.first, IS_ADMIN.second)

        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putBoolean(IS_ADMIN.first, value)
        }
}