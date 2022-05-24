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
        m_SwAddr1 = (preferenceScreen.findPreference<SwitchPreference>("key_locate") as SwitchPreference)
        m_CkbAddr1 = (preferenceScreen.findPreference<CheckBoxPreference>("key_address_1") as CheckBoxPreference)
        m_CkbAddr2 = (preferenceScreen.findPreference<CheckBoxPreference>("key_address_2") as CheckBoxPreference)
        m_CkbAddr3 = (preferenceScreen.findPreference<CheckBoxPreference>("key_address_3") as CheckBoxPreference)
        m_CkbAddr4 = (preferenceScreen.findPreference<CheckBoxPreference>("key_weather_thm") as CheckBoxPreference)
        m_CkbAddr5 = (preferenceScreen.findPreference<CheckBoxPreference>("key_weather_ksy") as CheckBoxPreference)
        m_CkbAddr6 = (preferenceScreen.findPreference<CheckBoxPreference>("key_weather_rain") as CheckBoxPreference)



    }


    val prefListener =
        SharedPreferences.OnSharedPreferenceChangeListener {
                sharedPreferences: SharedPreferences?, key: String? ->
            // key는 xml에 등록된 key에 해당
            when (key) {
                "key_locate" -> {
                    // SharedPreferences에 저장된 값을 가져와서 summary 설정
                    val summary = prefs.getBoolean("key_locate", false)
                    m_SwAddr1.title = if (summary)
                    {
                        "현재위치 표시"
                    }else
                    {
                        "설정위치 표시"
                    }
                    m_SwAddr1.summary = if (summary)
                    {
                        "현재 단말기의 위치로 설정한다."
                    }else
                    {
                        "지정된 위치로 설정한다."
                    }
                }
            }
        }

    // 리스너 등록
    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
    }

    // 리스너 해제
    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

}