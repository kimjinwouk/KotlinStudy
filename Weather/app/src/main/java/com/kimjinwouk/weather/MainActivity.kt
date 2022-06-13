package com.kimjinwouk.weather

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import com.kimjinwouk.weather.Adaper.AddressAdapter
import com.kimjinwouk.weather.Data.ItemAddress
import jxl.Workbook
import jxl.read.biff.BiffException
import net.daum.mf.map.api.*
import java.io.IOException
import java.io.InputStream


class MainActivity : BaseActivity() {


    private var addressData = ArrayList<ItemAddress>()
    private var addressList = mutableListOf<ItemAddress>()


    private val KAKAOserviceKey: String = RetroifitManager.KAKA_API_KEY
    private lateinit var addCode_1: String
    private lateinit var addCode_2: String
    private lateinit var addCode_3: String
    private lateinit var fab_runService: FloatingActionButton
    private lateinit var edt_myAddress: EditText
    private lateinit var lv_address: ListView

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


    }


    override fun onResume() {
        super.onResume()

        if (MyApp.prefs.getBoolean("key_edit", false)) {
            //수정된 이력이 있으면
            startService(Intent(this@MainActivity, WeatherSerivce::class.java))
            //서비스를 재싱핼하여 수정.
            MyApp.prefs.setBoolean("key_edit", false)
        }

        UpdateUI()

    }

    private fun KakaoMap() {
        KakaoMapView = MapView(this)
        KakaoMapContainer.addView(KakaoMapView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.menu, menu)
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
        fab_runService = findViewById(R.id.fab_runService)
        lv_address = findViewById(R.id.lv_address)
        edt_myAddress = findViewById(R.id.edt_myAddress)
        KakaoMapContainer = findViewById(R.id.kakaoMapView)

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

            MyApp.prefs.getString("선택_주소", "")

        )


        //내위치 마커표시.
        val marker = MapPOIItem()
        KakaoMapView.removeAllPOIItems()
        if(!MyApp.prefs.getString("선택_주소", "").toString().equals(""))
        {
            marker.apply {
                tag = 0
                itemName =
                    MyApp.prefs.getString("선택_주소", "").toString()

                mapPoint = MapPoint.mapPointWithGeoCoord(
                    MyApp.prefs.getString("선택_ypos", "").toDouble(),
                    MyApp.prefs.getString("선택_xpos", "").toDouble()
                )
                markerType = MapPOIItem.MarkerType.CustomImage
                customImageResourceId = R.drawable.ic_place_locate
                isCustomImageAutoscale = false
                setCustomImageAnchor(0f, -1.0f)
                isShowDisclosureButtonOnCalloutBalloon = false


            }
        }

        val CameraUpdateFactory = CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(
            MyApp.prefs.getString("선택_ypos", "").toDouble(),
            MyApp.prefs.getString("선택_xpos", "").toDouble()
        ))


        KakaoMapView.addPOIItem(marker)
        KakaoMapView.selectPOIItem(marker, false)
        KakaoMapView.moveCamera(CameraUpdateFactory)


        SetListViewVisibleState()
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if(view == null) view = View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun runService() {

        MaterialAlertDialogBuilder(
            this,
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )

            .setTitle("위치 설정")
            .setMessage(
                MyApp.prefs.getString("선택_주소", "")

            )
            .setPositiveButton("실행") { dialog, which ->
                startService(Intent(this@MainActivity, WeatherSerivce::class.java))
                UpdateUI()
            }
            .setNegativeButton(
                if (isMyServiceRunning(WeatherSerivce::class.java)) {
                    "서비스 중지"
                } else {
                    "취소"
                }
            ) { dialog, which ->
                // 취소 버튼 클릭 시
                val IntentStop = Intent(this@MainActivity, WeatherSerivce::class.java)
                IntentStop.action = ACTION_STOP
                startService(IntentStop)
                UpdateUI()
            }.show()
    }

    override fun onBackPressed() {
        if (ListViewVisibleState) {
            lv_address.visibility = View.GONE
            ListViewVisibleState = !ListViewVisibleState
            return
        }
        super.onBackPressed()
    }

    private fun initListner() {
        fab_runService.setOnClickListener {
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
                            "선택_주소",
                            ""
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


            MaterialAlertDialogBuilder(
                this,
                R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
            )

                .setTitle("위치 설정")
                .setMessage((adapterView.getItemAtPosition(i) as ItemAddress).address_full + "\n지역으로 설정할까요?")
                .setPositiveButton("확인") { dialog, which ->
                    MyApp.prefs.setBoolean("key_locate", false)
                    MyApp.prefs.getPrefs().edit(true) {
                        putString(
                            "선택여부",
                            "선택"
                        )
                        putString(
                            "선택_주소",
                            (adapterView.getItemAtPosition(i) as ItemAddress).address_full
                        )
                        putString(
                            "선택_주소_1",
                            (adapterView.getItemAtPosition(i) as ItemAddress).address_1
                        )
                        putString(
                            "선택_주소_2",
                            (adapterView.getItemAtPosition(i) as ItemAddress).address_2
                        )
                        putString(
                            "선택_주소_3",
                            (adapterView.getItemAtPosition(i) as ItemAddress).address_3
                        )
                        putString(
                            "선택_nx",
                            (adapterView.getItemAtPosition(i) as ItemAddress).nx.toString()
                        )
                        putString(
                            "선택_ny",
                            (adapterView.getItemAtPosition(i) as ItemAddress).ny.toString()
                        )
                        putString(
                            "선택_xpos",
                            (adapterView.getItemAtPosition(i) as ItemAddress).xpos.toString()
                        )
                        putString(
                            "선택_ypos",
                            (adapterView.getItemAtPosition(i) as ItemAddress).ypos.toString()
                        )
                    }


                    UpdateUI()
                    hideKeyboard()
                }
                .setNegativeButton("취소") { dialog, which ->
                }.show()
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


}