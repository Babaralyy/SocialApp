package com.codecoy.mvpflycollab.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codecoy.mvpflycollab.dataconverter.UserSerializer
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserRegisterData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")
class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore
    private val userSerializer = UserSerializer()

    suspend fun saveUserData( value: UserLoginData) {
        val dataStoreKey = stringPreferencesKey("userData")
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = userSerializer.serialize(value)
        }
    }

    fun getUserData(defaultValue: UserLoginData): Flow<UserLoginData> {
        val dataStoreKey = stringPreferencesKey("userData")
        return dataStore.data.map { preferences ->
            val serializedUser = preferences[dataStoreKey]
            serializedUser?.let { userSerializer.deserialize(it) } ?:defaultValue
        }
    }
}