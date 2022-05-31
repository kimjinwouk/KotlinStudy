package com.kimjinwouk.weather

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import com.kimjinwouk.weather.Adaper.AddressAdapter
import com.kimjinwouk.weather.Data.ItemAddress
import com.kimjinwouk.weather.Data.Kakao.Document
import com.kimjinwouk.weather.Data.Kakao.KakaoData
import com.kimjinwouk.weather.Dialog.AddressConfirmDailog
import jxl.Workbook
import jxl.read.biff.BiffException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.io.InputStream


class MainActivity : BaseActivity() {


    private var addressData = ArrayList<ItemAddress>()
    private var addressList = mutableListOf<ItemAddress>()


    private val KAKAOserviceKey: String = RetroifitManager.KAKA_API_KEY
    private lateinit var addCode_1: String
    private lateinit var addCode_2: String
    private lateinit var addCode_3: String
    private lateinit var btn_runService: Button
    private lateinit var edt_myAddress: EditText
    private lateinit var lv_address: ListView
    private lateinit var toolbar: Toolbar
    private lateinit var mAdapter: AddressAdapter
    private lateinit var KakaoMapContainer: RelativeLayout

    private lateinit var KakaoMapView: MapView
    private var ListViewVisibleState: Boolean = false


    /*
    * 앱실행시
    * 1. 내 구역 좌표 가져오기.
    *
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //requestPermission()
        init() // 맵핑 초기화
        KakaoMap() // 카카오맵 초기화
        initListner() // 리스너 초기화
        UpdateUI() // UI 업데이트

        setSupportActionBar(toolbar)    //툴바 사용 설정
        supportActionBar!!.setDisplayShowTitleEnabled(false)        //타이틀 보이게 설정

    }


    override fun onResume() {
        super.onResume()

        if(MyApp.prefs.getBoolean("key_edit",false)){
            //수정된 이력이 있으면
            startService(Intent(this@MainActivity, WeatherSerivce::class.java))
            //서비스를 재싱핼하여 수정.
            MyApp.prefs.setBoolean("key_edit",false)
        }

        UpdateUI()

    }
    private fun KakaoMap() {
        KakaoMapView = MapView(this)
        KakaoMapContainer.addView(KakaoMapView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_btn1 -> {
                Log.d("Main", "설정클릭")
                doSetting();
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun doSetting() {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }


    private fun init() {
        btn_runService = findViewById(R.id.btn_runService)
        lv_address = findViewById(R.id.lv_address)
        edt_myAddress = findViewById(R.id.edt_myAddress)
        KakaoMapContainer = findViewById(R.id.kakaoMapView)
        toolbar = findViewById(R.id.toolbar)
        initReadExcel()
        addressData.addAll(addressList)
        mAdapter = AddressAdapter(addressList)
        lv_address.adapter = mAdapter
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        try {
            val manager =
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(
                Int.MAX_VALUE
            )) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }


    private fun SetListViewVisibleState() {
        lv_address.visibility = if (ListViewVisibleState)
            View.VISIBLE
        else
            View.GONE
    }


    private fun UpdateUI() {
        Log.d("Weather_MainActivity", "UpdateUI()")
        var isSelected: Boolean = MyApp.prefs.getBoolean("key_locate", true)
        edt_myAddress.setText(
            if (isSelected) {
                MyApp.prefs.getString("설정_주소", "현재 위치 찾는중..")
            } else {
                MyApp.prefs.getString("선택_주소", "현재 위치 찾는중..")
            }

        )
        //카카오지도 현재위치 셋팅.
        KakaoMapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(
                if (isSelected) {
                    getYpos()
                } else {
                    MyApp.prefs.getString("선택_ypos", "").toDouble()
                },
                if (isSelected) {
                    getXpos()
                } else {
                    MyApp.prefs.getString("선택_xpos", "").toDouble()
                }
            ), 5, true
        )

        //내위치 마커표시.
        val marker = MapPOIItem()
        KakaoMapView.removeAllPOIItems()
        marker.apply {
            tag = 0
            itemName = if (isSelected) {
                "현재 내 위치"
            } else {
                "설정한 위치"
            }
            mapPoint = MapPoint.mapPointWithGeoCoord(
                if (isSelected) {
                    getYpos()
                } else {
                    MyApp.prefs.getString("선택_ypos", "").toDouble()
                },
                if (isSelected) {
                    getXpos()
                } else {
                    MyApp.prefs.getString("선택_xpos", "").toDouble()
                }
            )
            markerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.ic_place_locate
            isCustomImageAutoscale = false
            setCustomImageAnchor(0f, -1.0f)
            isShowDisclosureButtonOnCalloutBalloon = false


        }
        KakaoMapView.addPOIItem(marker)
        KakaoMapView.selectPOIItem(marker, false)

        SetListViewVisibleState()

        CoroutineScope(Dispatchers.Main).launch {
            delay(100L)
            btn_runService.setText(
                if (isMyServiceRunning(WeatherSerivce::class.java)) {
                    "서비스 위치변경 또는 중지"
                } else {
                    "서비스 실행"
                }
            )
        }
    }

    private fun runService() {


        val dialog = AddressConfirmDailog.CustomDialogBuilder()
            .setTitle("위치 설정")
            .setDescription(
                if (MyApp.prefs.getBoolean("key_locate", true)) {
                    MyApp.prefs.getString("설정_주소", "")
                } else {
                    MyApp.prefs.getString("선택_주소", "")
                }
            )
            .setPositiveBtnText(
                if (isMyServiceRunning(WeatherSerivce::class.java)) {
                    "서비스 위치변경"
                } else {
                    "실행"
                }
            )
            .setNegativeBtnText(
                if (isMyServiceRunning(WeatherSerivce::class.java)) {
                    "서비스 중지"
                } else {
                    "취소"
                }
            )
            .setBtnClickListener(object : AddressConfirmDailog.CustomDialogListener {

                override fun onClickPositiveBtn() {
                    startService(Intent(this@MainActivity, WeatherSerivce::class.java))
                    UpdateUI()

                }

                override fun onClickNegativeBtn() {
                    // 취소 버튼 클릭 시
                    val IntentStop = Intent(this@MainActivity, WeatherSerivce::class.java)
                    IntentStop.action = ACTION_STOP
                    startService(IntentStop)
                    UpdateUI()
                }
            })
            .create()
        dialog.show(supportFragmentManager, dialog.tag)
    }

    override fun onBackPressed() {
        if(ListViewVisibleState) {
            lv_address.visibility = View.GONE
            ListViewVisibleState = !ListViewVisibleState
            return
        }
        super.onBackPressed()
    }
    private fun initListner() {
        btn_runService.setOnClickListener {
            runService()

        }


        edt_myAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                ListViewVisibleState = if (edt_myAddress.text.toString().equals(
                        MyApp.prefs.getString(
                            "설정_주소",
                            "현재 위치 찾는중.."
                        )
                    ) || edt_myAddress.text.toString().equals(
                        MyApp.prefs.getString(
                            "선택_주소",
                            "현재 위치 찾는중.."
                        )
                    )
                ) {
                    false
                } else {
                    true
                }

                val text: String = edt_myAddress.getText().toString()
                search(text)
                SetListViewVisibleState()
            }
        }

        )


        lv_address.setOnItemClickListener { adapterView, view, i, l ->
            ListViewVisibleState = false;
            //adapterView.getItemAtPosition(i) 값을 저장.
            val dialog = AddressConfirmDailog.CustomDialogBuilder()
                .setTitle("설정")
                .setDescription((adapterView.getItemAtPosition(0) as ItemAddress).address_full + "\n지역으로 설정할까요?")
                .setPositiveBtnText("확인")
                .setNegativeBtnText("취소")
                .setBtnClickListener(object : AddressConfirmDailog.CustomDialogListener {

                    override fun onClickPositiveBtn() {
                        // 확인 버튼 클릭 시
                        MyApp.prefs.setBoolean("key_locate",false)
                        MyApp.prefs.getPrefs().edit(true) {
                            putString(
                                "선택여부",
                                "선택"
                            )
                            putString(
                                "선택_주소",
                                (adapterView.getItemAtPosition(0) as ItemAddress).address_full
                            )
                            putString(
                                "선택_주소_1",
                                (adapterView.getItemAtPosition(0) as ItemAddress).address_1
                            )
                            putString(
                                "선택_주소_2",
                                (adapterView.getItemAtPosition(0) as ItemAddress).address_2
                            )
                            putString(
                                "선택_주소_3",
                                (adapterView.getItemAtPosition(0) as ItemAddress).address_3
                            )
                            putString(
                                "선택_nx",
                                (adapterView.getItemAtPosition(0) as ItemAddress).nx.toString()
                            )
                            putString(
                                "선택_ny",
                                (adapterView.getItemAtPosition(0) as ItemAddress).ny.toString()
                            )
                            putString(
                                "선택_xpos",
                                (adapterView.getItemAtPosition(0) as ItemAddress).xpos.toString()
                            )
                            putString(
                                "선택_ypos",
                                (adapterView.getItemAtPosition(0) as ItemAddress).ypos.toString()
                            )
                        }


                        UpdateUI()
                    }

                    override fun onClickNegativeBtn() {
                        // 취소 버튼 클릭 시

                    }
                })
                .create()
            dialog.show(supportFragmentManager, dialog.tag)

        }
    }

    fun search(charText: String) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        addressList.clear()

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length == 0) {
            addressList.addAll(addressData)
        } else {
            // 리스트의 모든 데이터를 검색한다.
            for (i in 0 until addressData.size) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (addressData.get(i).address_full.contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    addressList.add(addressData.get(i))
                }
            }
        }

        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        mAdapter.notifyDataSetChanged()
    }


    companion object {
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
    }

    override fun onLocationChanged(location: Location) {
        setAddress()
    }

    private fun initReadExcel() {
        Log.d("Weather_MainActivity", "initReadExcel()")
        try {
            val localFile: InputStream = baseContext.resources.assets.open("local_code.xls")
            val wb: Workbook = Workbook.getWorkbook(localFile)
            if (wb != null) {
                val sheet = wb.getSheet(0) // 시트 불러오기
                if (sheet != null) {
                    val colTotal = sheet.columns // 전체 컬럼
                    val rowIndexStart = 1 // row 인덱스 시작
                    val rowTotal = sheet.getColumn(colTotal - 1).size
                    var row = rowIndexStart
                    while (row < rowTotal) {
                        addressList.add(
                            ItemAddress(
                                if (sheet.getCell(0, row).contents.length > 0) {
                                    sheet.getCell(0, row).contents
                                } else {
                                    ""
                                }
                                        +
                                        if (sheet.getCell(1, row).contents.length > 0) {
                                            " " + sheet.getCell(1, row).contents
                                        } else {
                                            ""
                                        }
                                        +
                                        if (sheet.getCell(2, row).contents.length > 0) {
                                            " " + sheet.getCell(2, row).contents
                                        } else {
                                            ""
                                        },
                                sheet.getCell(0, row).contents,
                                sheet.getCell(1, row).contents,
                                sheet.getCell(2, row).contents,
                                sheet.getCell(3, row).contents.toInt(),
                                sheet.getCell(4, row).contents.toInt(),
                                sheet.getCell(5, row).contents.toDouble(),
                                sheet.getCell(6, row).contents.toDouble()
                            )
                        )
                        row++
                    }
                }
            }
        } catch (e: IOException) {
            Log.i("READ_EXCEL1", e.message!!)
            e.printStackTrace()
        } catch (e: BiffException) {
            Log.i("READ_EXCEL1", e.message!!)
            e.printStackTrace()
        }

        //setWeather()
    }


    private fun readExcel() {
        try {
            val localFile: InputStream = baseContext.resources.assets.open("local_code.xls")
            val wb: Workbook = Workbook.getWorkbook(localFile)
            if (wb != null) {
                val sheet = wb.getSheet(0) // 시트 불러오기
                if (sheet != null) {
                    val colTotal = sheet.columns // 전체 컬럼
                    val rowIndexStart = 1 // row 인덱스 시작
                    val rowTotal = sheet.getColumn(colTotal - 1).size
                    var row = rowIndexStart
                    while (row < rowTotal) {
                        val contents = sheet.getCell(0, row).contents
                        if (addCode_1.length != 0 && addCode_1.equals(
                                sheet.getCell(
                                    0,
                                    row
                                ).contents
                            )
                        ) {
                            if (addCode_2.length == 0) {
                                MyApp.prefs.setString(
                                    "설정_nx",
                                    sheet.getCell(3, row).contents
                                )
                                MyApp.prefs.setString(
                                    "설정_ny",
                                    sheet.getCell(4, row).contents
                                )

                                row = rowTotal
                                break;
                            } else if (addCode_2.equals(sheet.getCell(1, row).contents)) {
                                if (addCode_3.length == 0) {
                                    MyApp.prefs.setString(
                                        "설정_nx",
                                        sheet.getCell(3, row).contents
                                    )
                                    MyApp.prefs.setString(
                                        "설정_ny",
                                        sheet.getCell(4, row).contents
                                    )
                                    row = rowTotal
                                    break;
                                } else if (addCode_3.equals(sheet.getCell(2, row).contents)) {
                                    MyApp.prefs.setString(
                                        "설정_nx",
                                        sheet.getCell(3, row).contents
                                    )
                                    MyApp.prefs.setString(
                                        "설정_ny",
                                        sheet.getCell(4, row).contents
                                    )
                                    row = rowTotal
                                    break;
                                }
                            }
                        }
                        row++
                    }
                    UpdateUI()
                }
            }
        } catch (e: IOException) {
            Log.i("READ_EXCEL1", e.message!!)
            e.printStackTrace()
        } catch (e: BiffException) {
            Log.i("READ_EXCEL1", e.message!!)
            e.printStackTrace()
        }


    }


    private fun setAddress() {
        Log.d("Weather_MainActivity", "setAddress()")
        val callGetAddress = RetroifitManager.KakaoService.getKakaoAddress(
            KAKAOserviceKey, getXpos(), getYpos()
        )
        callGetAddress.enqueue(object : Callback<KakaoData> {
            override fun onResponse(
                call: Call<KakaoData>,
                response: retrofit2.Response<KakaoData>
            ) {
                Log.d("Weather_MainActivity", "onResponse")
                if (response.isSuccessful) {
                    val it: List<Document> = response.body()!!.documents

                    val totalCount = response.body()!!.meta.total_count - 1
                    for (i in 0..totalCount) {
                        if (it[i].region_type.equals("H")) {
                            addCode_1 = it[i].region_1depth_name
                            addCode_2 = it[i].region_2depth_name
                            addCode_3 = it[i].region_3depth_name

                            MyApp.prefs.setString(
                                "설정_주소",
                                addCode_1 + " " + addCode_2 + " " + addCode_3
                            )

                            MyApp.prefs.setString(
                                "설정_주소_1",
                                addCode_1
                            )

                            MyApp.prefs.setString(
                                "설정_주소_2",
                                addCode_2
                            )

                            MyApp.prefs.setString(
                                "설정_주소_3",
                                addCode_3
                            )
                        }
                    }
                    readExcel()
                }
            }

            override fun onFailure(call: Call<KakaoData>, t: Throwable) {
                Log.d("Weather_MainActivity", "onFailure")
            }
        })
    }
}