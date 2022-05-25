package com.kimjinwouk.weather

import android.app.*
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import com.kimjinwouk.weather.BaseActivity.Companion.ACTION_STOP
import com.kimjinwouk.weather.Data.Item
import com.kimjinwouk.weather.Data.WeatherData

import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class WeatherSerivce : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private var timerTask: Timer? = null
    private var time = 0
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action!!.equals(
                ACTION_STOP
            )
        ) {
            stopForeground(true)
            stopSelf()
        } else {
            timerTask = timer(period = 5 * 60 * 1000) { // 20분단위수정
                Log.d("Weather_MainActivity", "timer돌아가는곳")
                time++
                setWeather()
            }
        }
        return START_STICKY
    }

    //Notififcation for ON-going
    private var iconNotification: Bitmap? = null
    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 123
    private var isSelected: Boolean = MyApp.prefs.getBoolean("key_locate", true)
    private fun generateForegroundNotification() {
        Log.d("Weather_MainActivity", "generateForegroundNotification()")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intentMainLanding = Intent(this, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intentMainLanding, FLAG_MUTABLE)
            iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            if (mNotificationManager == null) {
                mNotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                mNotificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("chats_group", "Chats")
                )
                val notificationChannel =
                    NotificationChannel(
                        "service_channel", "Service Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(this, "service_channel")

            /*
            - 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
            - 강수형태(PTY) 코드 : (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
            (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            */



            builder.apply {
                setContentTitle(getContentTitle())
                setTicker(
                    StringBuilder(resources.getString(R.string.app_name)).append("service is running")
                        .toString()
                )
                setContentText(getContentText())
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setPriority(NotificationCompat.PRIORITY_HIGH)
                setWhen(System.currentTimeMillis())
                setOnlyAlertOnce(true)
                setContentIntent(pendingIntent)
                setOngoing(true)
            }

            if (iconNotification != null) {
                builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
            }
            builder.color = resources.getColor(R.color.purple_200)
            notification = builder.build()
            startForeground(mNotificationId, notification)
            Log.d("Weather_MainActivity", "generateForegroundNotification() - startForeground")
        }

    }

    private fun getContentTitle(): String {
        /*
        *  key_address_1
        *  key_address_2
        *  key_address_3
        *
        *  저장값에 따라 어떻게 표시할지 선택.
        * */

        var Address: String? = ""

        if (isSelected) {

            Address +=
                if (MyApp.prefs.getBoolean("key_address_1", true)) {
                    MyApp.prefs.getString("설정_주소_1", "")
                } else {
                    ""
                }

            Address +=
                if (MyApp.prefs.getBoolean("key_address_2", true)) {
                    " "+MyApp.prefs.getString("설정_주소_2", "")
                } else {
                    ""
                }

            Address +=
                if (MyApp.prefs.getBoolean("key_address_3", true)) {
                    " "+MyApp.prefs.getString("설정_주소_3", "")
                } else {
                    ""
                }

        } else {


            Address +=
                if (MyApp.prefs.getBoolean("key_address_1", true)) {
                    MyApp.prefs.getString("선택_주소_1", "")
                } else {
                    ""
                }

            Address +=
                if (MyApp.prefs.getBoolean("key_address_2", true)) {
                    " "+MyApp.prefs.getString("선택_주소_2", "")
                } else {
                    ""
                }

            Address +=
                if (MyApp.prefs.getBoolean("key_address_3", true)) {
                    " "+MyApp.prefs.getString("선택_주소_3", "")
                } else {
                    ""
                }
        }
        if (MyApp.prefs.getBoolean("key_weather_thm", true))
        {
            Address += " : " + weatherArr[0].temp + "º"
        }
        return Address!!
    }

    private fun getContentText(): String {
        /*
       *  key_weather_sky
       *  key_weather_rain
       *
       *  저장값에 따라 어떻게 표시할지 선택.
       * */

        /*
- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
- 강수형태(PTY) 코드 : (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)

*/
        var Weather: String? = ""
        if (MyApp.prefs.getBoolean("key_weather_sky", true))
        {
            Weather += "하늘은 " +
                    when (weatherArr[0].sky) {
                        "1" -> "맑음"
                        "3" -> "구름많음"
                        "4" -> "흐림"
                        else -> ""
                    }
        }

        if (MyApp.prefs.getBoolean("key_weather_rain", true))
        {
            Weather +=
                    when (weatherArr[0].rainType) {
                        "1" -> " 그리고 비"
                        "2" -> " 그리고 비 또는 눈"
                        "3" -> " 그리고 눈"
                        "5" -> " 그리고 빗방울"
                        "6" -> " 그리고 빗방울 또는 눈날림"
                        "7" -> " 그리고 눈날림"
                        else -> ""
                    }
        }

        return Weather!!
    }

    private var base_date = "20210510"  // 발표 일자
    private var base_time = "1400"      // 발표 시각

    private val serviceKey: String = RetroifitManager.API_KEY
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
            serviceKey, dataType, base_date, base_time,
            if (isSelected) {
                MyApp.prefs.getString("설정_nx", "")
            } else {
                MyApp.prefs.getString("선택_nx", "")
            },
            if (isSelected) {
                MyApp.prefs.getString("설정_ny", "")
            } else {
                MyApp.prefs.getString("선택_ny", "")
            },
            numOfRows
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
                generateForegroundNotification()
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.d("Weather_MainActivity", "onFailure - " + t.stackTrace.toString())
            }

        }
        )
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