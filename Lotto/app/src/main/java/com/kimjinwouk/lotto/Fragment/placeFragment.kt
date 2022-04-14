package com.kimjinwouk.lotto.Fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kimjinwouk.lotto.MainActivity
import com.kimjinwouk.lotto.R
import net.daum.mf.map.api.MapView


class placeFragment : Fragment() {

    lateinit var _MainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_place, container, false)
        val mapView = MapView(activity)
        val mapViewContainer: ViewGroup = view.findViewById(R.id.map_view)
        mapViewContainer.addView(mapView)
        _MainActivity = activity as MainActivity

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                _MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: MutableMap<String, Boolean> ->
            val deniedList: List<String> = result.filter { !it.value }.map { it.key }
            when {
                deniedList.isNotEmpty() -> {
                    val map = deniedList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED"
                    }
                    map["DENIED"]?.let {
                        // request denied , request again
                        // 거부 한 번 했을경우 재요청
                    }
                    map["EXPLAINED"]?.let {
                        // request denied ,send to settings
                        // 거부 두 번 했을경우 설정
                    }
                }
                else -> { // All request are permitted
                }
            }
        }


    private fun startProcess() {

    }

}