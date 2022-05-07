package com.kimjinwouk.lotto.Permission

import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kimjinwouk.weather.BaseActivity


class SampleMultiplePermissionListener(private val activity: BaseActivity) :
    MultiplePermissionsListener {
    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
        for (response in report.grantedPermissionResponses) {
            activity.showPermissionGranted(response.permissionName)
        }
        for (response in report.deniedPermissionResponses) {
            activity.showPermissionDenied(response.permissionName, response.isPermanentlyDenied)
        }
    }

    override fun onPermissionRationaleShouldBeShown(
        permissions: List<PermissionRequest>,
        token: PermissionToken
    ) {
        activity.showPermissionRationale(token)
    }
}