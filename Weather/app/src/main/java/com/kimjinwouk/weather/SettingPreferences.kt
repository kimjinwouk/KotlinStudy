package com.kimjinwouk.weather

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference


class SettingPreferences : PreferenceFragmentCompat(){

    lateinit var prefs: SharedPreferences
    lateinit var m_SwAddr1 : SwitchPreference
    lateinit var m_CkbAddr1 : CheckBoxPreference
    lateinit var m_CkbAddr2 : CheckBoxPreference
    lateinit var m_CkbAddr3 : CheckBoxPreference
    lateinit var m_CkbAddr4 : CheckBoxPreference
    lateinit var m_CkbAddr5 : CheckBoxPreference
    lateinit var m_CkbAddr6 : CheckBoxPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.preferences,rootKey)
        init()

    }


    private fun init()
    {

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        MyApp.prefs.setBoolean("key_edit",false)
        m_SwAddr1 = (preferenceScreen.findPreference<SwitchPreference>("key_locate") as SwitchPreference)
        m_CkbAddr1 = (preferenceScreen.findPreference<CheckBoxPreference>("key_address_1") as CheckBoxPreference)
        m_CkbAddr2 = (preferenceScreen.findPreference<CheckBoxPreference>("key_address_2") as CheckBoxPreference)
        m_CkbAddr3 = (preferenceScreen.findPreference<CheckBoxPreference>("key_address_3") as CheckBoxPreference)
        m_CkbAddr4 = (preferenceScreen.findPreference<CheckBoxPreference>("key_weather_thm") as CheckBoxPreference)
        m_CkbAddr5 = (preferenceScreen.findPreference<CheckBoxPreference>("key_weather_sky") as CheckBoxPreference)
        m_CkbAddr6 = (preferenceScreen.findPreference<CheckBoxPreference>("key_weather_rain") as CheckBoxPreference)



    }

    private fun UpdateUI()
    {
        val summary = prefs.getBoolean("key_locate", false)
        m_SwAddr1.title = if (summary)
        {
            "???????????? ??????"
        }else
        {
            "???????????? ??????"
        }
        m_SwAddr1.summary = if (summary)
        {
            "?????? ???????????? ????????? ????????????."
        }else
        {
            "????????? ????????? ????????????."
        }
    }


    val prefListener =
        SharedPreferences.OnSharedPreferenceChangeListener {
                sharedPreferences: SharedPreferences?, key: String? ->
            // key??? xml??? ????????? key??? ??????
            MyApp.prefs.setBoolean("key_edit",true)
            when (key) {
                "key_locate" -> {
                    // SharedPreferences??? ????????? ?????? ???????????? summary ??????
                    val summary = prefs.getBoolean("key_locate", false)
                    m_SwAddr1.title = if (summary)
                    {
                        "???????????? ??????"
                    }else
                    {
                        "???????????? ??????"
                    }
                    m_SwAddr1.summary = if (summary)
                    {
                        "?????? ???????????? ????????? ????????????."
                    }else
                    {
                        "????????? ????????? ????????????."
                    }
                }
            }
        }

    // ????????? ??????
    override fun onResume() {
        super.onResume()
        UpdateUI()
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
    }

    // ????????? ??????
    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

}