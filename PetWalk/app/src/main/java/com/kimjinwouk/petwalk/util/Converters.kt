package com.kimjinwouk.petwalk.util



import android.location.Location
import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters{
    @TypeConverter
    fun fromLocate(value: Location?): String {
        return Gson().toJson(value)
    }
    //Locate 받은걸 String으로 변환해서 저장.

    @TypeConverter
    fun toLocate(value: String) : Location {
        return Gson().fromJson(value, Location::class.java)
    }

}