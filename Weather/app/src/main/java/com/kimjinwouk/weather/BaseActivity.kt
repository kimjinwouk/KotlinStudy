package com.kimjinwouk.weather

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter

import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.kimjinwouk.lotto.Permission.SampleErrorListener
import com.kimjinwouk.lotto.Permission.SampleMultiplePermissionListener
import com.kimjinwouk.lotto.Permission.SamplePermissionListener


abstract class BaseActivity : AppCompatActivity() {


    private var locationPermissionListener: MultiplePermissionsListener? = null
    private var errorListener: PermissionRequestErrorListener? = null
    val HOME: Int = 0
    val PLACE: Int = 1
    val RUN: Int = 2


    private var mFusedLocationProviderClient: FusedLocationProviderClient? =
        null // 현재 위치를 가져오기 위한 변수
    private var mLastLocation: Location? = null // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocate()
        createPermissionListeners()
        startLocationUpdates()
    }

    private fun initLocate() {
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }


    public fun getXpos(): Double {
        var returnValue: Double

        returnValue = if (mLastLocation == null)
            127.047325
        else
            mLastLocation!!.longitude


        return returnValue
    }

    public fun getYpos(): Double {
        var returnValue: Double

        returnValue = if (mLastLocation == null)
            37.517235
        else
            mLastLocation!!.latitude


        return returnValue
    }


    public fun startLocationUpdates() {

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationProviderClient!!.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    mLastLocation = location
                }
            }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()!!
        )
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    open fun onLocationChanged(location: Location) {
        mLastLocation = location
        //text2.text = "위도 : " + mLastLocation.latitude // 갱신 된 위도
        //text1.text = "경도 : " + mLastLocation.longitude // 갱신 된 경도
        CallbackLocate()
    }


    open fun CallbackLocate()
    {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun showPermissionRationale(token: PermissionToken) {
        AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
                token.cancelPermissionRequest()
            }
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                dialog.dismiss()
                token.continuePermissionRequest()
            }
            .setOnDismissListener { token.cancelPermissionRequest() }
            .show()
    }

    open fun showPermissionGranted(permission: String) {
        Toast.makeText(this, permission, Toast.LENGTH_LONG).show()
        startLocationUpdates()
    }

    open fun showPermissionDenied(permission: String, isPermanentlyDenied: Boolean) {
        Toast.makeText(this, permission, Toast.LENGTH_LONG).show()
    }

    open fun permissionResult(int: Int) {

    }

    private fun createPermissionListeners() {
        var contentView: View? = findViewById(android.R.id.content)
        val feedbackViewPermissionListener: PermissionListener = SamplePermissionListener(this)
        val feedbackViewMultiplePermissionListener: MultiplePermissionsListener =
            SampleMultiplePermissionListener(this)

        locationPermissionListener = CompositeMultiplePermissionsListener(
            feedbackViewMultiplePermissionListener,
            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(
                contentView,
                R.string.location_permission_denied_feedback
            )
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .withCallback(object : Snackbar.Callback() {
                    override fun onShown(snackbar: Snackbar) {
                        super.onShown(snackbar)
                        //permissionResult(RUN)
                    }

                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        super.onDismissed(snackbar, event)
                        //permissionResult(HOME)

                    }
                })
                .build()
        )

        errorListener = SampleErrorListener()
    }


    fun requestPermission() {
        Dexter.withContext(applicationContext)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(locationPermissionListener)
            .withErrorListener(errorListener)
            .check()
    }
}