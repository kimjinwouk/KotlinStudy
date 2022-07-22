package com.kimjinwouk.petwalk.ui.fragment

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kimjinwouk.petwalk.R


//https://dkss7413.tistory.com/23 참고

class ShowFragment {
    /*
    companion object {
        private val fragmentMap: HashMap<String, Fragment> = HashMap()

        fun show(fragmentName: String, activity: FragmentActivity) {
            Log.d(activity.toString(), fragmentMap.toString())
            IsNullCheck(fragmentMap.size, activity.supportFragmentManager.fragments.size , activity)
            Log.d(activity.toString(), activity.supportFragmentManager.fragments.toString())
            fragmentMap.forEach {
                Log.d(activity.toString(), "${it.key} hide fragment")
                activity.supportFragmentManager.beginTransaction().hide(it.value).commit()

            }
            Log.d(activity.toString(), "${fragmentName} show fragment")
            /*
            when (fragmentName) {
                "home" -> HomeFragment.set(fragmentName, activity)
                "map" -> MapFragment.set(fragmentName, activity)
                "chatList" -> WalkingListFragment.set(fragmentName, activity)
                "myInfo" -> MyInfoFragment.set(fragmentName, activity)
                //추가할 곳
                else -> {
                }
            }
            */
        }

        private fun IsNullCheck(size: Int, size1: Int, activity: FragmentActivity) {
            if (size != size1) {
                /*
                * activity.supportFragmentmanger 가 사라지는현상이 발생하여 수정.
                * */

                if(activity.supportFragmentManager.fragments.size == 0){
                    fragmentMap.forEach{
                        activity.supportFragmentManager.beginTransaction()
                            .add(R.id.fragmentContainer,it.value).commit()
                    }
                    return
                }

                fragmentMap.forEach { Map ->
                    activity.supportFragmentManager.fragments.forEachIndexed { index, it ->
                        if(Map.value == it){
                              return
                        }
                        if (index == size1){
                            //마지막까지 리턴하지못하면 추가
                            activity.supportFragmentManager.beginTransaction()
                                .add(R.id.fragmentContainer,Map.value).commit()
                        }
                    }
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

        fun remove(activity: FragmentActivity) {
            Log.d(activity.toString(), fragmentMap.toString())
            IsNullCheck(fragmentMap.size, activity.supportFragmentManager.fragments.size , activity)
            Log.d(activity.toString(), activity.supportFragmentManager.fragments.toString())
            fragmentMap.forEach {
                activity.supportFragmentManager.beginTransaction().remove(it.value).commit()
                Log.d(activity.toString(), "${it.key} remove fragment")
            }
            fragmentMap.clear()
        }
    }

     */

}