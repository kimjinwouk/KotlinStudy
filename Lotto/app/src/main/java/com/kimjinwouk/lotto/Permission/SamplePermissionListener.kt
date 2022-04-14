package com.kimjinwouk.lotto.Permission

import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.kimjinwouk.lotto.BaseActivity
import com.kimjinwouk.lotto.MainActivity

open class SamplePermissionListener(private val activity: BaseActivity) : PermissionListener {
    override fun onPermissionGranted(response: PermissionGrantedResponse) {
        //activity.showPermissionGranted(response.permissionName)
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse) {
        //activity.showPermissionDenied(response.permissionName, response.isPermanentlyDenied)
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest,
        token: PermissionToken
    ) {
        //activity.showPermissionRationale(token)
    }
}