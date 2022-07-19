package a.jinkim.calculate.model

import android.graphics.Bitmap
import android.location.Location
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kimjinwouk.petwalk.Service.Polyline

/*
* 산책에대한 정보
* uid - 산책 고유아이디
* Lng
* Lat - 좌표
* Date - 시간
*
* */

@Entity(tableName = "Walking")
data class Walking(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    var Locations: MutableList<Polyline>?, //이동한좌표
    var Date : String = "", // 저장한날짜
    var Time : String = "", // 걸린시간
    var Bitmap : Bitmap,    // 네이버맵 스냅샷
    var Distance : Int = 0  // 총 이동거리
)

