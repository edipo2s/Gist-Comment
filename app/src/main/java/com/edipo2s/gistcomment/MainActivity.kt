package com.edipo2s.gistcomment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

internal class MainActivity : AppCompatActivity() {

    private var cameraSource: CameraSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            checkForCameraHardware()
        }
    }

    override fun onResume() {
        super.onResume()
        startCameraPreview()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == RC_CAMERA_PERMISSION) {
            checkForCameraPermission()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkForCameraHardware() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            checkForCameraPermission()
        } else {
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkForCameraPermission() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            startCameraPreview()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestCameraPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(CAMERA_PERMISSION)) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requestPermissions(arrayOf(CAMERA_PERMISSION), RC_CAMERA_PERMISSION)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        ActivityCompat.finishAfterTransition(this)
                    }
                    .create()
        } else {
            requestPermissions(arrayOf(CAMERA_PERMISSION), RC_CAMERA_PERMISSION)
        }
    }

    private fun startCameraPreview() {
        val barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build()
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() > 0) {
                    val qrCodeText = barcodes.valueAt(0).displayValue
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, qrCodeText, Toast.LENGTH_SHORT).show()
                    }
                    cameraSource?.stop()
                }
            }

            override fun release() {}

        })
        cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build()
        camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource?.start(holder)
                } catch (ie: IOException) {
                    Log.e("CAMERA SOURCE", ie.message)
                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })
    }

    companion object {

        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val RC_CAMERA_PERMISSION = 122

    }

}