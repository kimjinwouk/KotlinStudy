package com.kimjinwouk.lotto.Fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kimjinwouk.lotto.Adapter.BottomSheetAdapter
import com.kimjinwouk.lotto.Kakao.Document
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
    lateinit var mKakaoData: MutableLiveData<KakaoData>
    lateinit var mKakaoDocument: List<Document>
    lateinit var mlvPlace: ListView
    lateinit var mAdapter: BottomSheetAdapter


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_place, container, false)
        mapView = MapView(activity)
        val mapViewContainer: ViewGroup = view.findViewById(R.id.map_view)
        mlvPlace = view.findViewById(R.id.lv_place)

        mapViewContainer.addView(mapView)
        _MainActivity = activity as MainActivity

        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheet))

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            /*
            *STATE_DRAGGING = 끝까지 올린상태
            * */
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> Toast.makeText(
                        context,
                        "STATE_COLLAPSED",
                        Toast.LENGTH_SHORT
                    ).show()
                    BottomSheetBehavior.STATE_EXPANDED -> Toast.makeText(
                        context,
                        "STATE_EXPANDED",
                        Toast.LENGTH_SHORT
                    ).show()
                    BottomSheetBehavior.STATE_DRAGGING -> Toast.makeText(
                        context,
                        "STATE_DRAGGING",
                        Toast.LENGTH_SHORT
                    ).show()
                    BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(
                        context,
                        "STATE_SETTLING",
                        Toast.LENGTH_SHORT
                    ).show()
                    BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(
                        context,
                        "STATE_HIDDEN",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(context, "OTHER_STATE", Toast.LENGTH_SHORT).show()
                }
            }
        })

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

    public fun setLocate() {
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
        mKakaoData = MutableLiveData<KakaoData>()

        KakaoService.getKakaoAddress(RetroifitManager.API_KEY, address, x, y)
            .enqueue(object : retrofit2.Callback<KakaoData> {
                override fun onResponse(call: Call<KakaoData>, response: Response<KakaoData>) {
                    mKakaoData.value = response.body()
                    mKakaoDocument = mKakaoData.value?.documents!!
                    mAdapter = BottomSheetAdapter(mKakaoDocument)
                    mlvPlace.adapter = mAdapter

                }

                override fun onFailure(call: Call<KakaoData>, t: Throwable) {
                    Toast.makeText(context, "실패 - " + t.printStackTrace(), Toast.LENGTH_LONG).show()
                }
            }
            )
    }

}
