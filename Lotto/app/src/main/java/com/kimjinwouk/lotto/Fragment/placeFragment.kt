package com.kimjinwouk.lotto.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kimjinwouk.lotto.Adapter.BottomSheetAdapter
import com.kimjinwouk.lotto.Kakao.Document
import com.kimjinwouk.lotto.Kakao.KakaoData
import com.kimjinwouk.lotto.MainActivity
import com.kimjinwouk.lotto.R
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager.KakaoService
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Response


class placeFragment : Fragment() {

    lateinit var _MainActivity: MainActivity
    lateinit var mapView: MapView
    lateinit var mKakaoData: MutableLiveData<KakaoData>
    lateinit var mKakaoDocument: List<Document>
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

    private fun setMarker() {


        mKakaoDocument.forEachIndexed { index, number ->

            val marker = MapPOIItem()

            marker.apply {
                tag = index
                itemName = mKakaoDocument[index].place_name
                mapPoint = MapPoint.mapPointWithGeoCoord( mKakaoDocument[index].y.toDouble(),mKakaoDocument[index].x.toDouble())
                markerType = MapPOIItem.MarkerType.CustomImage
                customImageResourceId = R.drawable.ic_place_locate
                isCustomImageAutoscale = false
                setCustomImageAnchor(0f, -1.0f)
                isShowDisclosureButtonOnCalloutBalloon = false

            }
            mapView.setCalloutBalloonAdapter(context?.let { CustomCalloutBalloonAdapter(it,mKakaoDocument) })
            mapView.addPOIItem(marker)

        }
        // 서울시청 마커 추가


    }

    class CustomCalloutBalloonAdapter(context: Context,mData : List<Document>) : CalloutBalloonAdapter {
        private val mCalloutBalloon: View
        private val mDatas : List<Document>

        override fun getCalloutBalloon(poiItem: MapPOIItem): View {
            (mCalloutBalloon.findViewById<View>(R.id.ct) as ConstraintLayout).setPadding(20)
            (mCalloutBalloon.findViewById<View>(R.id.name) as TextView).text = mDatas[poiItem.tag].place_name
            (mCalloutBalloon.findViewById<View>(R.id.address) as TextView).text = mDatas[poiItem.tag].address_name
            (mCalloutBalloon.findViewById<View>(R.id.distance) as TextView).text = mDatas[poiItem.tag].distance+"M"
            return mCalloutBalloon

        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem): View? {
            return null
        }

        init {
            mCalloutBalloon = LayoutInflater.from(context).inflate(R.layout.custom_callout_balloon, null)
            mDatas = mData
        }
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
                    setMarker()
                }

                override fun onFailure(call: Call<KakaoData>, t: Throwable) {
                    Toast.makeText(context, "실패 - " + t.printStackTrace(), Toast.LENGTH_LONG).show()
                }
            }
            )
    }

}
