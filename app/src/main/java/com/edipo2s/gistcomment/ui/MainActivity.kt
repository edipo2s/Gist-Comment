package com.edipo2s.gistcomment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import javax.inject.Inject

internal class MainActivity : BaseActivity(R.layout.activity_main) {

    @Inject
    lateinit var cameraSource: CameraSource

    @Inject
    lateinit var barcodeDetector: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configView()
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_LONG).show()
        }
    }

    private fun configView() {
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() > 0) {
                    val qrCodeText = barcodes.valueAt(0).displayValue
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, qrCodeText, Toast.LENGTH_SHORT).show()
                    }
                    cameraSource.stop()
                }
            }

            override fun release() {}

        })
        camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                checkForCameraPermission()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == RC_CAMERA_PERMISSION) {
            checkForCameraPermission()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkForCameraPermission() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            startCameraPreview()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestCameraPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), RC_CAMERA_PERMISSION)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        ActivityCompat.finishAfterTransition(this)
                    }
                    .show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), RC_CAMERA_PERMISSION)
        }
    }

    private fun startCameraPreview() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraSource.start(camera_preview.holder)
            } catch (ie: IOException) {
                Log.e("CAMERA SOURCE", ie.message)
            }
        }
    }

    companion object {

        private const val RC_CAMERA_PERMISSION = 122

    }

}