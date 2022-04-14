package com.kimjinwouk.lotto

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.kimjinwouk.lotto.Permission.SampleErrorListener
import com.kimjinwouk.lotto.Permission.SampleMultiplePermissionListener
import com.kimjinwouk.lotto.Permission.SamplePermissionListener


abstract class BaseActivity : AppCompatActivity() {


    private var locationPermissionListener: MultiplePermissionsListener? = null
    private var errorListener: PermissionRequestErrorListener? = null
    val HOME: Int = 0
    val PLACE: Int = 1
    val RUN: Int = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocate()
        createPermissionListeners()

    }

    private fun initLocate() {


    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun showPermissionRationale(token: PermissionToken) {
        AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
                token.cancelPermissionRequest()
            }
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                dialog.dismiss()
                token.continuePermissionRequest()
            }
            .setOnDismissListener { token.cancelPermissionRequest() }
            .show()
    }

    open fun showPermissionGranted(permission: String) {
        Toast.makeText(this, permission, Toast.LENGTH_LONG).show()
    }

    open fun showPermissionDenied(permission: String, isPermanentlyDenied: Boolean) {
        Toast.makeText(this,permission,Toast.LENGTH_LONG).show()
    }

    open fun permissionResult(int: Int) {

    }

    private fun createPermissionListeners() {
        var contentView: View? = findViewById(android.R.id.content)
        val feedbackViewPermissionListener: PermissionListener = SamplePermissionListener(this)
        val feedbackViewMultiplePermissionListener: MultiplePermissionsListener =
            SampleMultiplePermissionListener(this)

        locationPermissionListener = CompositeMultiplePermissionsListener(
            feedbackViewMultiplePermissionListener,
            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(
                contentView,
                R.string.location_permission_denied_feedback
            )
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .withCallback(object : Snackbar.Callback() {
                    override fun onShown(snackbar: Snackbar) {
                        super.onShown(snackbar)
                        //permissionResult(RUN)
                    }

                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        super.onDismissed(snackbar, event)
                        //permissionResult(HOME)

                    }
                })
                .build()
        )

        errorListener = SampleErrorListener()
    }


    fun requestPermission() {
        Dexter.withContext(applicationContext)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(locationPermissionListener)
            .withErrorListener(errorListener)
            .check()
    }
}