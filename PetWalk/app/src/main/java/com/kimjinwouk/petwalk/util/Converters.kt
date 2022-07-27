package com.kimjinwouk.petwalk.util



import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.room.TypeConverter
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.naver.maps.geometry.LatLng
import java.io.ByteArrayOutputStream
import java.lang.reflect.Modifier
import java.time.LocalDateTime
import com.kimjinwouk.petwalk.Service.Polyline as Polyline

class Converters{

    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT)
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .create()

    @TypeConverter
    fun fromLocations(value: MutableList<LatLng>?): String {
        return Gson().toJson(value)
    }
    //Locate 받은걸 String으로 변환해서 저장.

    @TypeConverter
    fun toLocations(value: String) : MutableList<LatLng> {
        return gson.fromJson<MutableList<LatLng>>( value, object:TypeToken<MutableList<LatLng>>() {}.type )
    }

    @TypeConverter
    fun fromTime(value: LocalDateTime?): String {
        return Gson().toJson(value)
    }
    //Locate 받은걸 String으로 변환해서 저장.

    @TypeConverter
    fun toTime(value: String) : LocalDateTime {
        return gson.fromJson<LocalDateTime>( value, object:TypeToken<LocalDateTime>() {}.type )
    }



    // Bitmap -> ByteArray 변환
    @TypeConverter
    fun toByteArray(bitmap : Bitmap) : ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    // ByteArray -> Bitmap 변환
    @TypeConverter
    fun toBitmap(bytes : ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }



}