/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.navigation

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import me.zhanghai.android.files.navigation.StandardDirectoriesLiveData.addSource
import me.zhanghai.android.files.settings.Settings

object StandardDirectoriesLiveData : MediatorLiveData<List<StandardDirectory>>() {
    init {
        // Initialize value before we have any active observer.
        loadValue()
        addSource(Settings.STANDARD_DIRECTORY_SETTINGS) { loadValue() }
    }

    private fun loadValue() {
        Log.e("输出路径",standardDirectories.toString())
        value = standardDirectories
    }
}
