package a.jinkim.calculate.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* 산책에대한 정보
* uid - 산책 고유아이디
* Lng
* Lat - 좌표
* Date - 시간
*
* */
@Entity(tableName = "Walking")
data class Walking (

    @PrimaryKey(autoGenerate= true) val uid: Int = 0,
    @ColumnInfo(name = "ItemId") val ItemId: Int?,
    @ColumnInfo(name = "Lat") val Lat: Float?,
    @ColumnInfo(name = "Lng") val Lng: Float?,
    @ColumnInfo(name = "Date") val Date: Long?,

){
    constructor() : this(0,0,0.0f,0.0f,0)
}