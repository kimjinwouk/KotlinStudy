package com.kimjinwouk.petwalk.map


import a.jinkim.calculate.model.Walking
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.kimjinwouk.petwalk.BaseFragment
import com.kimjinwouk.petwalk.DB.Data
import com.kimjinwouk.petwalk.MainActivity
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.Service.BackgroundLocationUpdateService
import com.kimjinwouk.petwalk.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.kimjinwouk.petwalk.DB.Data.myLocation

class MapFragment : Fragment(R.layout.fragment_map), View.OnClickListener, OnMapReadyCallback{


/*
* 지도에서 보여줄 컨텐츠
* 1. 주변에 있는 강아지 사진을 마커로 표시
* 1-1 주변에 있는 강아지 모두를 보여줄순없으니 일정  반경안에있는 강아지를 보여줘한다.
* 2. 뷰페이저로 하단에 좌우 스크롤 표시
* 3. 뷰페이저 + 마커 연동
* 4. 뷰페이저 클릭시에 해당 강아지와 채팅방 생성
* ------------------------------------------------------------------------------------
* 변경된부분
* 지도 하단에 플로팅 버튼 하나 설정.
* 버튼 클릭시 부터 정지할때까지 내 위치 저장 후 보고.
* 실시간 보고
*
* 둘중 하나 선택해야함.
*
* 그리고 해당
* 경북대학교 산책정보보* */

    private lateinit var binding: FragmentMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var runWalking = false
    private var locationManager: LocationManager? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
        binding.walkingFloatingButton.setOnClickListener(this)
        binding.naverMap.getMapAsync(this)
        binding.naverMap.onCreate(savedInstanceState)
        locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager?

    }

    companion object : BaseFragment {
        override fun newInstance(): MapFragment {
            return MapFragment()
        }

        const val TAG = "MapFragment"
        const val LOCATION_PERMISSION_REQUEST_CODE: Int = 1000
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.walkingFloatingButton.id -> walkStart()
        }
    }

    private fun walkStart() {
        /*
        * 1. 위치좌표가 얻기.
        * 2. 위치좌표가 있다면 해당 UID / WALKING_시간 / 좌표 저장.
        * 저장을 어떻게 할것인가에 대한 고민.
        * 1. 로컬DB에 산책에 관련된 정보를 저장후 한번에 업로드 할것이냐
        * 2. 실시간으로 디비에 업로드 할것이냐
        *
        * */



        // 1. 권한 체크
        if (permissionCheck().not()) {
            return
        }
        // 2. 포그라운드 위치 정보 갱신
        //https://stackoverflow.com/questions/28535703/best-way-to-get-user-gps-location-in-background-in-android



        if (runWalking.not()) {
            //runWalking이 false 경우 true로 변경하고 해당시점부터 좌표수집을하여 Room에 기록한다.
            Toast.makeText(requireContext(),"산책 서비스 시작",Toast.LENGTH_SHORT).show()
            runWalking = true
            //서비스 시작
            requireContext().startService((Intent(requireContext(), BackgroundLocationUpdateService::class.java)))
        }else{
            runWalking = false
            Toast.makeText(requireContext(),"산책 서비스 정지",Toast.LENGTH_SHORT).show()
            requireContext().stopService((Intent(requireContext(), BackgroundLocationUpdateService::class.java)))
        }
        
        
    }

    private var maxId: Int = 0

    private fun permissionCheck(): Boolean {
        when {
            (ContextCompat.checkSelfPermission(
                requireContext(),
                PERMISSIONS[0]
            )) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionContextPopup()
                return false
            }

            else -> {
                requestPermissions(
                    PERMISSIONS,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                return false
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한이 필요합니다.")
            .setMessage("산책을 시작하기 위해서는 위치정보가 필요합니다.")
            .setPositiveButton("동의", { _, _ ->
                requestPermissions(
                    PERMISSIONS,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            })
            .create()
            .show()
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        locationSource =
            FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    private fun hasPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(requireContext(), PERMISSIONS[0]) ==
                PermissionChecker.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        binding.naverMap.onStart()

    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        binding.naverMap.onResume()
        if(this::naverMap.isInitialized) {
            naverMap?.let {
                val coord = LatLng(myLocation)

                val locationOverlay = it.locationOverlay
                locationOverlay.isVisible = true
                locationOverlay.position = coord
                locationOverlay.bearing = myLocation.bearing
                it.moveCamera(CameraUpdate.scrollTo(coord))
                Log.d(TAG, "" + coord)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.naverMap.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.naverMap.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        binding.naverMap.onStop()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.naverMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.naverMap.onLowMemory()
    }
}