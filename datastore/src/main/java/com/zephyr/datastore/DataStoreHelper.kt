package com.zephyr.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.zephyr.provider.Zephyr
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DataStoreHelper {

    /**
     * 获取 DataStore 实例
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = Zephyr.preferenceName
    )

    /**
     * 全局都可获取的 DataStore 实例
     */
    val dataStoreInstance: DataStore<Preferences> by lazy {
        Zephyr.application.dataStore
    }

    /**
     * 插入泛型值元素到 DataStore 中
     */
    suspend fun <T> Preferences.Key<T>.putValue(value: T) {
        dataStoreInstance.edit {
            it[this] = value
        }
    }

    /**
     * 插入泛型值元素到 DataStore 中
     */
    suspend fun <T> putPreference(
        preferencesKey: Preferences.Key<T>,
        value: T
    ) = preferencesKey.putValue(value)

    /**
     * 获取 DataStore 对应的泛型值
     */
    suspend fun <T> Preferences.Key<T>.getValue(default: T): T {
        return dataStoreInstance.data.map {
            it[this] ?: default
        }.first()
    }

    /**
     * 获取 DataStore 对应的泛型值
     */
    suspend fun <T> getPreference(
        preferencesKey: Preferences.Key<T>,
        default: T
    ): T = preferencesKey.getValue(default)
}