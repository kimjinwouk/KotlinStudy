package com.kimjinwouk.petwalk.Service

import a.jinkim.calculate.model.Walking
import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.kimjinwouk.petwalk.DB.Data.myLocation
import com.kimjinwouk.petwalk.DB.Data.petWalkDB
import com.kimjinwouk.petwalk.MainActivity
import java.util.concurrent.TimeUnit


internal class BackgroundLocationUpdateService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /* Declare in manifest
    <service android:name=".BackgroundLocationUpdateService"/>
    */
    private val TAG = "BackgroundLocationUpdateService"
    private val TAG_LOCATION = "TAG_LOCATION"
    private var context: Context? = null
    private var stopService = false

    /* For Google Fused API */
    protected var mGoogleApiClient: GoogleApiClient? = null
    protected var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var latitude = "0.0"
    private var longitude = "0.0"
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null

    /* For Google Fused API */


    override fun onCreate() {
        super.onCreate()
        context = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        StartForeground()
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                try {
                    if (!stopService) {
                        //Perform your task here
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (!stopService) {
                        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(10))
                    }
                }
            }
        }
        handler.postDelayed(runnable, 2000)
        buildGoogleApiClient()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.e(TAG, "Service Stopped")
        stopService = true
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            Log.e(TAG_LOCATION, "Location Update Callback Removed")
        }
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun StartForeground() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val CHANNEL_ID = "1000"
        val CHANNEL_NAME = "PetWalk"
        var builder: NotificationCompat.Builder? = null
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
            builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
            builder.setChannelId(CHANNEL_ID)
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
        } else {
            builder = NotificationCompat.Builder(this, CHANNEL_ID)
        }
        builder.setContentTitle("PetWalk")
        builder.setContentText("산책중 정보를 기록중입니다.")
        val notificationSound: Uri =
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(notificationSound)
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.ic_menu_mylocation)
        builder.setContentIntent(pendingIntent)
        val notification: Notification = builder.build()
        startForeground(101, notification)
    }

    override fun onLocationChanged(location: Location) {
        location ?: return
        Log.e(
            TAG_LOCATION,
            "Location Changed Latitude : " + location.getLatitude()
                .toString() + "\tLongitude : " + location.getLongitude()
        )
        myLocation = location
        latitude = java.lang.String.valueOf(location.getLatitude())
        longitude = java.lang.String.valueOf(location.getLongitude())
        if (latitude.equals("0.0", ignoreCase = true) && longitude.equals(
                "0.0",
                ignoreCase = true
            )
        ) {
            requestLocationUpdate()
        } else {
            /*
            * 실제 위치좌표 갱신 ROOM 저장.
            *
            * */

            saveWalkData(location)
            Log.e(
                TAG_LOCATION,
                "Latitude : " + location.getLatitude()
                    .toString() + "\tLongitude : " + location.getLongitude()
            )
        }
    }

    private fun saveWalkData(location: Location) {
        /*
        * 1. WALKING DB ITEMID MAX 값 조회
        * 2. ITEM MAX 값이 있다면 해당 좌표를 MAX+1값으로 저장 지속적으로.
        *
        * */
        Thread {
            maxId = petWalkDB.walkingDao().getId()
            // MaxID 값을 구하고 아래 MaxId값 + 1 과 좌표값을 Room DB에 저장
        }.start()
        maxId++
        if (maxId > 0) {
            //maxId값이 있으면 해당 값을 저장.
            Thread {
                petWalkDB.walkingDao().insertWalk(
                    Walking(
                        0,
                        maxId + 1,
                        location.latitude.toFloat(),
                        location.longitude.toFloat(),
                        System.currentTimeMillis()
                    )
                )
            }.start()
        }
    }

    var maxId: Int = 0

    fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    fun onProviderEnabled(provider: String?) {}
    fun onProviderDisabled(provider: String?) {}
    override fun onConnected(@Nullable bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.apply {
            setInterval(10 * 1000)
            setFastestInterval(5 * 1000)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        mLocationSettingsRequest = builder.build()
        mSettingsClient!!
            .checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener {
                Log.e(TAG_LOCATION, "GPS Success")
                requestLocationUpdate()
            }.addOnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val REQUEST_CHECK_SETTINGS = 214
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(
                            (context as AppCompatActivity?)!!,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sie: SendIntentException) {
                        Log.e(TAG_LOCATION, "Unable to execute request.")
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                        TAG_LOCATION,
                        "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                    )
                }
            }.addOnCanceledListener { Log.e(TAG_LOCATION, "checkLocationSettings -> onCanceled") }
    }

    override fun onConnectionSuspended(i: Int) {
        connectGoogleClient()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        buildGoogleApiClient()
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        mSettingsClient = LocationServices.getSettingsClient(applicationContext)
        mGoogleApiClient = GoogleApiClient.Builder(applicationContext)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        connectGoogleClient()
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.e(TAG_LOCATION, "Location Received")
                mCurrentLocation = locationResult.lastLocation
                onLocationChanged(mCurrentLocation!!)
            }
        }
    }

    private fun connectGoogleClient() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val resultCode = googleAPI.isGooglePlayServicesAvailable(applicationContext)
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient!!.connect()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }
}