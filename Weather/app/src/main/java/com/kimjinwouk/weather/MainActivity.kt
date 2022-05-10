package com.kimjinwouk.weather

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import com.kimjinwouk.weather.Adaper.AddressAdapter
import com.kimjinwouk.weather.Data.Item
import com.kimjinwouk.weather.Data.Kakao.Document
import com.kimjinwouk.weather.Data.Kakao.KakaoData
import com.kimjinwouk.weather.Data.WeatherData
import jxl.Workbook
import jxl.read.biff.BiffException
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity() {

    private var base_date = "20210510"  // 발표 일자
    private var base_time = "1400"      // 발표 시각
    private var nx = "55"               // 예보지점 X 좌표
    private var ny = "127"              // 예보지점 Y 좌표
    private val serviceKey: String = RetroifitManager.API_KEY
    private val KAKAOserviceKey: String = RetroifitManager.KAKA_API_KEY
    private val dataType: String = "json"
    private val numOfRows: String = "100"
    private val weatherArr = arrayOf(
        ModelWeather(),
        ModelWeather(),
        ModelWeather(),
        ModelWeather(),
        ModelWeather(),
        ModelWeather()
    )

    private lateinit var addCode_1: String
    private lateinit var addCode_2: String
    private lateinit var addCode_3: String

    private lateinit var btn_runService: Button
    private lateinit var btn_stopService: Button
    private lateinit var edt_search: EditText

    private lateinit var lv_address: ListView
    private lateinit var mAdapter: AddressAdapter
    private var addressData = ArrayList<String>()
    private var addressList = mutableListOf<String>()

    /*
    * 앱실행시
    * 1. 내 구역 좌표 가져오기.
    *
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //requestPermission()
        init()
        initListner()
    }

    private fun init() {
        btn_runService = findViewById(R.id.btn_runService)
        btn_stopService = findViewById(R.id.btn_stopService)
        lv_address = findViewById(R.id.lv_address)
        edt_search = findViewById(R.id.edt_search)

        initReadExcel()
        addressData.addAll(addressList)
        mAdapter = AddressAdapter(addressList)
        lv_address.adapter = mAdapter
    }

    private fun initListner() {
        btn_runService.setOnClickListener {
            startService(Intent(this, WeatherSerivce::class.java))
        }

        btn_stopService.setOnClickListener {
            val intentStop = Intent(this, WeatherSerivce::class.java)
            intentStop.action = ACTION_STOP
            startService(intentStop)
        }

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val text: String = edt_search.getText().toString()
                search(text)
            }
        }

        )
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
                if (addressData.get(i).contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    addressList.add(addressData.get(i))
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        mAdapter.notifyDataSetChanged()
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

    companion object {
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
    }

    override fun onLocationChanged(location: Location) {
        //getCurrentAddress(getYpos(),getXpos())
        setAddress()
    }


    fun getCurrentAddress(latitude: Double, longitude: Double): String? {

        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        return address.getAddressLine(0).toString().toString() + "\n"
    }

    private fun initReadExcel() {
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
                                    }
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
                                nx = sheet.getCell(3, row).contents
                                ny = sheet.getCell(4, row).contents
                                row = rowTotal
                                break;
                            } else if (addCode_2.equals(sheet.getCell(1, row).contents)) {
                                if (addCode_3.length == 0) {
                                    nx = sheet.getCell(3, row).contents
                                    ny = sheet.getCell(4, row).contents
                                    row = rowTotal
                                    break;
                                } else if (addCode_3.equals(sheet.getCell(2, row).contents)) {
                                    nx = sheet.getCell(3, row).contents
                                    ny = sheet.getCell(4, row).contents
                                    row = rowTotal
                                    break;
                                }
                            }
                        }
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

        setWeather()
    }

    private fun setWeather() {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        val cal = Calendar.getInstance()
        base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        val timeH = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시각
        val timeM = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 분
        // API 가져오기 적당하게 변환
        base_time = getBaseTime(timeH, timeM)
        // 현재 시각이 00시이고 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if (timeH == "00" && base_time == "2330") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }

        val callGetWeather = RetroifitManager.service.getWeather(
            serviceKey, dataType, base_date, base_time, nx, ny, numOfRows
        )


        callGetWeather.enqueue(object : Callback<WeatherData> {
            override fun onResponse(
                call: Call<WeatherData>,
                response: retrofit2.Response<WeatherData>
            ) {
                Log.d("Weather_MainActivity", "onResponse")
                if (response.isSuccessful) {
                    val it: List<Item> = response.body()!!.response.body.items.item

                    // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열

                    // 배열 채우기
                    var index = 0
                    val totalCount = response.body()!!.response.body.totalCount - 1
                    for (i in 0..totalCount) {
                        index %= 6
                        when (it[i].category) {
                            "PTY" -> weatherArr[index].rainType = it[i].fcstValue     // 강수 형태
                            "REH" -> weatherArr[index].humidity = it[i].fcstValue     // 습도
                            "SKY" -> weatherArr[index].sky = it[i].fcstValue          // 하늘 상태
                            "T1H" -> weatherArr[index].temp = it[i].fcstValue         // 기온
                            else -> continue
                        }
                        index++
                    }

                    // 각 날짜 배열 시간 설정
                    for (i in 0..5) weatherArr[i].fcstTime = it[i].fcstTime

                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.d("Weather_MainActivity", "onFailure")
            }
        })
    }

    private fun setAddress() {
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
                        }
                    }
                }
                readExcel();
            }

            override fun onFailure(call: Call<KakaoData>, t: Throwable) {
                Log.d("Weather_MainActivity", "onFailure")
            }
        })
    }

    class ModelWeather {
        var rainType = ""       // 강수 형태
        var humidity = ""       // 습도
        var sky = ""            // 하늘 상태
        var temp = ""           // 기온
        var fcstTime = ""       // 예보시각
    }

    // baseTime 설정하기
    private fun getBaseTime(h: String, m: String): String {
        var result = ""

        // 45분 전이면
        if (m.toInt() < 45) {
            // 0시면 2330
            if (h == "00") result = "2330"
            // 아니면 1시간 전 날씨 정보 부르기
            else {
                var resultH = h.toInt() - 1
                // 1자리면 0 붙여서 2자리로 만들기
                if (resultH < 10) result = "0" + resultH + "30"
                // 2자리면 그대로
                else result = resultH.toString() + "30"
            }
        }
        // 45분 이후면 바로 정보 받아오기
        else result = h + "30"

        return result
    }


}