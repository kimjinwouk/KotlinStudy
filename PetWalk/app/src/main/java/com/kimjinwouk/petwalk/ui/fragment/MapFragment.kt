package com.kimjinwouk.petwalk.ui.fragment


import a.jinkim.calculate.model.Walking
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.room.PrimaryKey
import com.google.android.gms.location.FusedLocationProviderClient
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.Service.PetWalkService
import com.kimjinwouk.petwalk.Service.Polyline
import com.kimjinwouk.petwalk.databinding.FragmentMapBinding
import com.kimjinwouk.petwalk.util.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.kimjinwouk.petwalk.util.Constants.Companion.ACTION_STOP_SERVICE
import com.kimjinwouk.petwalk.util.Constants.Companion.KEY_COLOR
import com.kimjinwouk.petwalk.util.Constants.Companion.POLYLINE_WIDTH
import com.kimjinwouk.petwalk.util.Constants.Companion.TAG
import com.kimjinwouk.petwalk.util.PetWalkUtil
import com.kimjinwouk.petwalk.viewmodel.walkViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PolylineOverlay
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {


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


    // FragmentMapBinding 선언
    private lateinit var binding: FragmentMapBinding

    // 뷰모델 생성
    private val viewModel by viewModels<walkViewModel>()

    // 네이버 맵 선언
    private var NaverMap: NaverMap? = null


    // 라이브 데이터를 받아온 값들
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeInMillis = 0L

    // SharedPreferences 주입
    @Inject
    lateinit var sharedPref: SharedPreferences


    // 총 이동거리
    private var sumDistance = 0f

    // 선 색상
    private var POLYLINE_COLOR = Color.RED

    // SharedPreferences 주입
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var runWalking = false
    private var locationManager: LocationManager? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)



        binding.apply {
            naverMap.getMapAsync {
                it.uiSettings.isZoomControlEnabled = false
                NaverMap = it

                // 알림 클릭 등으로 다시 생성되었을 때 경로 표시
                addAllPolylines()
                moveCameraToUser()

                // 거리 텍스트 변경 ( 알림 클릭 등으로 다시 생성되었을 때 초기화 방지)
                sumDistance = updateDistance()
            }
            naverMap.onCreate(savedInstanceState)

            // 알림창에서 불러 왔을 때 현재 레이아웃 불러오기
            if (PetWalkService.isTracking.value != null) {
                currentTimeInMillis = PetWalkService.timeRunInMillis.value!!
                updateTracking(PetWalkService.isTracking.value!!)
            }

            // 스타트 버튼 클릭 시 서비스를 시작함
            walkingFloatingButton.setOnClickListener {
                if (PetWalkUtil.hasLocationPermissions(requireContext()).not()) {
                    requestPermission()
                }

                // 이미 실행 중이면 일시 정지
                if (isTracking) {
                    //sendCommandToService(ACTION_PAUSE_SERVICE)
                    zoomToWholeTrack()
                    endRunAndSaveToDB()
                }
                // 아니라면 실행
                else {
                    sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                    Toast.makeText(requireContext(),"산책 시작!",Toast.LENGTH_SHORT).show()
                }
            }

            // 종료 버튼 클릭 시 저장하고 종료
//            finishButton.setOnClickListener {
//                zoomToWholeTrack()
//                endRunAndSaveToDB()
//            }

        }

        // 위치 추적 여부 관찰하여 updateTracking 호출
        PetWalkService.isTracking.observe(requireActivity(), Observer {
            updateTracking(it)
        })

        // 경로 변경 관찰

        PetWalkService.pathPoints.observe(requireActivity(), Observer {
            Log.d(TAG, "PetWalkService.pathPoints")
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
            // 거리 텍스트 변경
            //binding.distanceText.text = "${PetWalkUtil.getFormattedDistance(sumDistance)}Km"
        })

        // 시간(타이머) 경과 관찰
        PetWalkService.timeRunInMillis.observe(requireActivity(), Observer {
            currentTimeInMillis = it
            val formattedTime = PetWalkUtil.getFormattedStopWatchTime(it, true)
            //binding.timerText.text = formattedTime
        })
    }


    // 스냅샷 찍기 위하여 전체 경로가 다 보이게 줌
    private fun zoomToWholeTrack() {
        val bounds = LatLngBounds.Builder()
        lateinit var bitmap: Bitmap
        if (pathPoints != null && pathPoints.last().size > 1) {
            for (polyline in pathPoints) {
                for (point in polyline) {
                    bounds.include(point)
                }
            }
            NaverMap?.moveCamera(CameraUpdate.fitBounds(bounds.build(), 100))
        }
    }

    // 달리기 기록 저장
    private fun endRunAndSaveToDB() {

        val calendar = Calendar.getInstance()
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())

        val year = yearFormat.format(calendar.time)
        val month = monthFormat.format(calendar.time)
        val day = dayFormat.format(calendar.time)
        val title = "${year}년 ${month}월 ${day}일 산책"

        NaverMap?.takeSnapshot(object : NaverMap.SnapshotReadyCallback {
            override fun onSnapshotReady(bitmap: Bitmap) {
                val run = Walking(
                    0, PetWalkService.pathPoints.value, title, currentTimeInMillis.toString() ,bitmap ,sumDistance.toInt()               )
                viewModel.insertWalk(run)
                stopRun()
                Toast.makeText(requireContext(),"산책 종료 후 저장!",Toast.LENGTH_SHORT).show()

            }
        })
    }

    // 위치 추적 상태에 따른 레이아웃 변경
    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking

    }

    // 경로 전부 표시
    private fun addAllPolylines() {
        if (pathPoints.isNotEmpty() != null && pathPoints.size > 1) {
            polyline.map = null
            polyline.map = NaverMap
            polyline.apply {
                width = POLYLINE_WIDTH
                color = POLYLINE_COLOR
                joinType = PolylineOverlay.LineJoin.Round
                coords = pathPoints.last()
            }
            polyline.map = NaverMap
            Log.d(TAG, "addAllPolylines : ${pathPoints.last().toList().size}")
        }

    }

    // 지도 위치 이동
    private fun moveCameraToUser() {
        if (isTracking) {
            if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
                naverMapMove(pathPoints.last().last())
            }
        } else {
            setCurrentLocation()
        }
    }

    // 총 이동거리
    private fun updateDistance(): Float {
        var distanceInMeters = 0f
        for (polyline in pathPoints) {
            distanceInMeters += calculatePolylineLength(polyline)
        }
        return distanceInMeters
    }

    // 총 이동거리 계산
    private fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        // 두 경로 사이마다 거리를 계산하여 합함
        for (i in 0 until polyline.size - 1) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]
            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }

    // 경로 표시 (마지막 전, 마지막 경로 연결)
    val polyline = PolylineOverlay()
    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2] // 마지막 전 경로
            val lastLatLng = pathPoints.last().last() // 마지막 경로


            polyline.apply {
                width = POLYLINE_WIDTH
                color = POLYLINE_COLOR
                joinType = PolylineOverlay.LineJoin.Round
                coords = pathPoints.last().toList()
            }
            Log.d(TAG, "addLatestPolyline : ${pathPoints.last().toList().size}")
            polyline.map = NaverMap

            // 이동거리 계산
            val result = FloatArray(1)
            Location.distanceBetween(
                preLastLatLng.latitude,
                preLastLatLng.longitude,
                lastLatLng.latitude,
                lastLatLng.longitude,
                result
            )
            sumDistance += result[0]
        }
    }


    // 서비스에게 명령을 전달함
    private fun sendCommandToService(action: String) =
        Intent(requireContext(), PetWalkService::class.java).also {
            it.action = action
            requireActivity().startService(it)
        }

    // 달리기 종료
    private fun stopRun() {
        PetWalkService.pathPoints.removeObservers(requireActivity())
        sendCommandToService(ACTION_STOP_SERVICE)
        //finish()
    }

    // 색상 정보 불러오기
    private fun colorLoad() {
        val colorState = sharedPref.getInt(KEY_COLOR, 1)
        when (colorState) {
            1 -> POLYLINE_COLOR = Color.RED
            2 -> POLYLINE_COLOR = Color.BLUE
            3 -> POLYLINE_COLOR = Color.GREEN
            4 -> POLYLINE_COLOR = Color.BLACK
        }
    }

    /**
     * 라이프 사이클에 맞게 맵뷰를 처리해줌
     */
    override fun onResume() {
        binding.naverMap.onResume()
        // 백그라운드 상태에서 돌아왔을 때 경로 표시
        addAllPolylines()
        moveCameraToUser()

        // 거리 텍스트 동기화
        sumDistance = updateDistance()
        //binding.distanceText.text = "${TrackingUtility.getFormattedDistance(sumDistance)}Km"
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.naverMap.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.naverMap.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.naverMap.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.naverMap.onLowMemory()
    }


    // 권한 요청
    private fun requestPermission() {
        // 이미 권한이 있으면 그냥 리턴
        if (PetWalkUtil.hasLocationPermissions(requireContext())) {
            return
        } else {
            requestSinglePermission.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    // 백그라운드 권한 요청
    private fun permissionDialog(context: Context) {
        var builder = AlertDialog.Builder(context)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")
            .setPositiveButton("네") { _, _ ->
                backgroundPermission()
            }
            .setNegativeButton("아니오") { _, _ ->
                null
            }.create()

        builder.show()

    }

    // 안드로이드 API 30 버전부터는 backgroundPermission 을 직접 설정해야함
    private fun backgroundPermission() {
        requestSinglePermission.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        )
    }

    companion object {
        const val REQUESTCODE_ACCESS_FINE_LOCATION = 1
        const val REQUESTCODE_ACCESS_BACKGROUND_LOCATION = 2
    }

    var requestSinglePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { it ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when {
                    it.key == "android.permission.ACCESS_FINE_LOCATION" && it.value -> {
                        permissionDialog(requireContext())
                    }

                    it.key == "android.permission.ACCESS_BACKGROUND_LOCATION" && it.value -> {
                        setCurrentLocation()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setCurrentLocation() {
        if (PetWalkUtil.hasLocationPermissions(requireContext())) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { it ->
                it?.let { it ->
                    naverMapMove(LatLng(it.latitude, it.longitude))
                }
            }
        }
    }


    private fun naverMapMove(latlng: LatLng) {
        Log.d(TAG, "naverMapMove")
        NaverMap?.let {
            it.moveCamera(CameraUpdate.scrollTo(latlng))
            setMarker(it, latlng)
        }
    }

    val marker = Marker()
    private fun setMarker(naverMap: NaverMap, latlng: LatLng) {
        marker.map = null
        marker.apply {
            isIconPerspectiveEnabled = true
            icon = OverlayImage.fromResource(R.drawable.ic_baseline_run_circle_24)
            alpha = 0.8f
            position = latlng
            zIndex = 10
            map = naverMap
        }
    }

}