/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.settings

import android.os.Bundle
import android.os.Environment
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import me.zhanghai.android.files.compat.getDrawableCompat
import me.zhanghai.android.files.compat.setTintCompat
import me.zhanghai.android.files.navigation.StandardDirectoriesLiveData
import me.zhanghai.android.files.navigation.StandardDirectory
import me.zhanghai.android.files.navigation.getExternalStorageDirectory
import me.zhanghai.android.files.util.getColorByAttr
import me.zhanghai.android.files.util.valueCompat

class StandardDirectoriesPreferenceFragment : PreferenceFragmentCompatFixIssue201(), Preference.OnPreferenceClickListener {
    override fun onCreatePreferencesFix(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        StandardDirectoriesLiveData.observe(viewLifecycleOwner) { onStandardDirectoriesChanged(it) }
    }

    private fun onStandardDirectoriesChanged(
        standardDirectories: List<StandardDirectory>
    ) {
        val preferenceManager = preferenceManager
        val context = preferenceManager.context
        var preferenceScreen = preferenceScreen
        val oldPreferences = mutableMapOf<String, Preference>()
        if (preferenceScreen == null) {
            preferenceScreen = preferenceManager.createPreferenceScreen(context)
            setPreferenceScreen(preferenceScreen)
        } else {
            for (index in preferenceScreen.preferenceCount - 1 downTo 0) {
                val preference = preferenceScreen.getPreference(index)
                preferenceScreen.removePreference(preference)
                oldPreferences[preference.key] = preference
            }
        }
        val secondaryTextColor = context.getColorByAttr(android.R.attr.textColorSecondary)
        for (standardDirectory in standardDirectories) {
            val id = standardDirectory.id
            var preference = oldPreferences[id] as SwitchPreferenceCompat?
            if (preference == null) {
                preference = SwitchPreferenceCompat(context).apply {
                    key = id
                    isPersistent = false
                    onPreferenceClickListener = this@StandardDirectoriesPreferenceFragment
                }
            }
            preference.apply {
                icon = context.getDrawableCompat(standardDirectory.iconRes).apply {
                    mutate()
                    setTintCompat(secondaryTextColor)
                }
                title = standardDirectory.getTitle(context)
                summary = getExternalStorageDirectory(standardDirectory.relativePath)
                isChecked = standardDirectory.isEnabled
            }
            preferenceScreen.addPreference(preference)
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {


        preference as SwitchPreferenceCompat
        val id = preference.key
        val isEnabled = preference.isChecked
        val settingsList = Settings.STANDARD_DIRECTORY_SETTINGS.valueCompat.toMutableList()
        val index = settingsList.indexOfFirst { it.id == id }

        if (index != -1) {
            settingsList[index] = settingsList[index].copy(isEnabled = isEnabled)
        } else {
            val standardDirectory = StandardDirectoriesLiveData.valueCompat.find { it.id == id }!!
            settingsList += standardDirectory.toSettings().copy(isEnabled = isEnabled)
        }
        Settings.STANDARD_DIRECTORY_SETTINGS.putValue(settingsList)
        return true
    }
}
