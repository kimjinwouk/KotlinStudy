package com.kimjinwouk.lotto.Fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.kimjinwouk.lotto.Kakao.KakaoApi
import com.kimjinwouk.lotto.Kakao.KakaoData
import com.kimjinwouk.lotto.MainActivity
import com.kimjinwouk.lotto.R
import com.kimjinwouk.lotto.Retrofit.Interface.RetrofitService
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager.KakaoService
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Response


class placeFragment : Fragment() {

    lateinit var _MainActivity: MainActivity
    lateinit var mapView: MapView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_place, container, false)
        mapView = MapView(activity)
        val mapViewContainer: ViewGroup = view.findViewById(R.id.map_view)
        mapViewContainer.addView(mapView)
        _MainActivity = activity as MainActivity

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithCONGCoord(_MainActivity.getXpos(),_MainActivity.getYpos()),7,true)
        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(
                37.517235,
                127.047325
            ), 5, true
        )
        mapView.zoomIn(true);
        mapView.zoomOut(true);

    }

    private fun startProcess() {

    }

    public fun setLocate()
    {
        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(
                _MainActivity.getYpos(),
                _MainActivity.getXpos()
            ), 5, true
        )
        callKakaoKeyword(
            "로또",
            _MainActivity.getXpos().toString(),
            _MainActivity.getYpos().toString()
        )
    }


    private fun callKakaoKeyword(
        address: String,
        x: String,
        y: String,
    ) {
        val kakao = MutableLiveData<KakaoData>()

        KakaoService.getKakaoAddress(RetroifitManager.API_KEY, address, x, y)
            .enqueue(object : retrofit2.Callback<KakaoData> {
                override fun onResponse(call: Call<KakaoData>, response: Response<KakaoData>) {
                    kakao.value = response.body()
                }

                override fun onFailure(call: Call<KakaoData>, t: Throwable) {
                    Toast.makeText(context, "실패 - " + t.printStackTrace(), Toast.LENGTH_LONG).show()
                }
            }
            )
    }

}
