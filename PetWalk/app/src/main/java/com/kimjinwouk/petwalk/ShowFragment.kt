package com.kimjinwouk.petwalk

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kimjinwouk.petwalk.chat.ChatFragment
import com.kimjinwouk.petwalk.home.HomeFragment
import com.kimjinwouk.petwalk.map.MapFragment
import com.kimjinwouk.petwalk.myinfo.MyInfoFragment


//https://dkss7413.tistory.com/23 참고

class ShowFragment {
    companion object {
        private val fragmentMap: HashMap<String, Fragment> = HashMap()

        fun show(fragmentName: String, activity: FragmentActivity) {
            fragmentMap.forEach{
                activity.supportFragmentManager.beginTransaction().hide(it.value).commit()
            }

            when (fragmentName) {
                "home" -> HomeFragment.set(fragmentName, activity)
                "map" -> MapFragment.set(fragmentName, activity)
                "chatList" -> ChatFragment.set(fragmentName, activity)
                "myInfo" -> MyInfoFragment.set(fragmentName, activity)
                //추가할 곳
                else -> {
                }
            }
        }

        private fun BaseFragment.set(fragmentName: String, activity: FragmentActivity) {
            if (fragmentMap[fragmentName] == null) {
                fragmentMap[fragmentName] = this.newInstance()
                activity.supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragmentMap[fragmentName]!!).commit()
            } else {
                activity.supportFragmentManager.beginTransaction()
                    .show(fragmentMap[fragmentName]!!)
                    .commit()
            }
        }

        fun remove(activity : FragmentActivity){
            fragmentMap.forEach{
                activity.supportFragmentManager.beginTransaction().remove(it.value).commit()
            }
        }
    }

}