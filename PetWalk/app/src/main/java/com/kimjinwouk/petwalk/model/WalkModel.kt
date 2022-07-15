package a.jinkim.calculate.model
import android.location.Location
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
data class WalkModel (
    @ColumnInfo(name = "ItemId") val ItemId: Int?,
    @ColumnInfo(name = "Locate") val Locate: List<Location>?,
    @ColumnInfo(name = "SDate") val SDate: Long?,
    @ColumnInfo(name = "EDate") val EDate: Long?
)