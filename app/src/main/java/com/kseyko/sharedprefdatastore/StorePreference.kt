package com.kseyko.sharedprefdatastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**     Code with ❤
╔════════════════════════════╗
║   Created by Seyfi ERCAN   ║
╠════════════════════════════╣
║  seyfiercan35@hotmail.com  ║
╠════════════════════════════╣
║      02,January,2022      ║
╚════════════════════════════╝
 */
class StorePreference(private val context: Context) {

    // Create the dataStore and give it a name same as shared preferences

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_pref")

    // Create some keys we will use them to store and retrieve the data
    companion object {
        val USERNAME = stringPreferencesKey("USERNAME")
        val PASSWORD = stringPreferencesKey("PASSWORD")
    }

    // Store user data
    // refer to the data store and using edit
    // we can store values using the keys
    suspend fun storeUser(username: String, password: String) {
        context.dataStore.edit {
            it[USERNAME] = username
            it[PASSWORD] = password
            // here it refers to the preferences we are editing
        }
    }
    // Create an username flow to retrieve username from the preferences
    // flow comes from the kotlin coroutine
    val usernameFlow: Flow<String> = context.dataStore.data.map {
        it[USERNAME] ?: ""
    }

    // Create a name flow to retrieve name from the preferences
    val passwordFlow: Flow<String> = context.dataStore.data.map {
        it[PASSWORD] ?: ""
    }
}