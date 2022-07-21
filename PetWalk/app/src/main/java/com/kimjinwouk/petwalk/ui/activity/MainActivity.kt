package com.kimjinwouk.petwalk.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.ui.fragment.ShowFragment
import com.kimjinwouk.petwalk.databinding.ActivityMainBinding
import com.kimjinwouk.petwalk.ui.fragment.HomeFragment
import com.kimjinwouk.petwalk.ui.fragment.MapFragment
import com.kimjinwouk.petwalk.ui.fragment.MyInfoFragment
import com.kimjinwouk.petwalk.ui.fragment.WalkingListFragment
import com.kimjinwouk.petwalk.util.Constants.Companion.ACTION_SHOW_TRACKING_ACTIVITY
import com.kimjinwouk.petwalk.util.Constants.Companion.KEY_FIRST_TIME_TOGGLE
import com.kimjinwouk.petwalk.util.Constants.Companion.KEY_NAME
import com.kimjinwouk.petwalk.util.Constants.Companion.TAG
import com.kimjinwouk.petwalk.viewmodel.walkViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject






@AndroidEntryPoint
class MainActivity : BaseActivity() {


    // ActivityMainBinding 선언
    private lateinit var binding: ActivityMainBinding

    // NavController 선언
    private lateinit var navController: NavController

    // SharedPreferences 주입
    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    // 처음 실행 여부
    private var firstTimeAppOpen: Boolean = true

    // 뷰모델 생성
    private val viewModel by viewModels<walkViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // 데이터 불러오기
        firstTimeAppOpen = sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
        android.util.Log.d(TAG, "onCreate: $firstTimeAppOpen")
        val name = sharedPref.getString(KEY_NAME,"")

        // 처음 실행했다면 세팅 화면으로
        if(firstTimeAppOpen && auth.currentUser == null){
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.apply {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
            navController = navHostFragment.findNavController()
            bottomNavigationView.setupWithNavController(navController)
        }

    }

    // foreground 상태에서 호출
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    // 알림 창 클릭시 메인 -> Tracking
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if(intent?.action == ACTION_SHOW_TRACKING_ACTIVITY) {
            navController.navigate(R.id.map)
        }
    }

}