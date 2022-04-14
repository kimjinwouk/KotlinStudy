package com.kimjinwouk.lotto.Permission

import android.os.Handler
import android.os.Looper
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.kimjinwouk.lotto.BaseActivity
import com.kimjinwouk.lotto.MainActivity

class SampleBackgroundThreadPermissionListener(activity: BaseActivity?) :
    SamplePermissionListener(activity!!) {
    private val handler = Handler(Looper.getMainLooper())
    override fun onPermissionGranted(response: PermissionGrantedResponse) {
        handler.post { super@SampleBackgroundThreadPermissionListener.onPermissionGranted(response) }
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse) {
        handler.post { super@SampleBackgroundThreadPermissionListener.onPermissionDenied(response) }
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest,
        token: PermissionToken
    ) {
        handler.post {
            super@SampleBackgroundThreadPermissionListener.onPermissionRationaleShouldBeShown(
                permission, token
            )
        }
    }
}