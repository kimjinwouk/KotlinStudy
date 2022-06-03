package a.jinkim.frame

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    val buttonAddPhoto: Button by lazy {
        findViewById<Button>(R.id.buttonAddPhoto)
    }

    val buttonStartPhotoFrameMode: Button by lazy {
        findViewById<Button>(R.id.buttonStartPhotoFrameMode)
    }

    private val imageView: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.imageview11))
            add(findViewById(R.id.imageview12))
            add(findViewById(R.id.imageview13))
            add(findViewById(R.id.imageview21))
            add(findViewById(R.id.imageview22))
            add(findViewById(R.id.imageview23))
        }
    }
    private val imageUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initButtonAddPhoto()
        initButtonStartPhotoFrameMode()


    }

    private fun initButtonAddPhoto() {
        buttonAddPhoto.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //권한이 부여되었을 경우 갤러리에서 사진 선택
                    navigatePhotos()
                }

                shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == true -> {
                    //교육용 팜업을 확인 후 권한 팜업을 띄우는 기능
                    ShowPermissionContextPopup()
                }

                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사진가져오기
                    navigatePhotos()
                } else {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }

    /*
    * ActivityForResult 가 registerForActivityResult 로 변경되었다.
    * */
    var launcher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val selectImageUri: Uri? = it.data?.data
            if (selectImageUri != null) {
                if (imageUriList.size > 6) {
                    Toast.makeText(this, "사진을 가져오지 못하였습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    imageUriList.add(selectImageUri)
                    imageView[imageUriList.size - 1].setImageURI(selectImageUri)
                }

            } else {
                Toast.makeText(this, "사진을 가져오지 못하였습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "사진을 가져오지 못하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        //SAF
        intent.type = "image/*" //이미지만 가져오는 것.
        launcher.launch(intent)
    }

    private fun ShowPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기", { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            })
            .setNegativeButton("취소하기", { _, _ ->
            })
            .create()
            .show()
    }

    private fun initButtonStartPhotoFrameMode() {
        buttonStartPhotoFrameMode.setOnClickListener{
            val intent =Intent(this,PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed{ index, uri ->  
                intent.putExtra("photo$index",uri.toString())
            }
            intent.putExtra("photoListSize",imageUriList.size)
            startActivity(intent)
        }
    }
}