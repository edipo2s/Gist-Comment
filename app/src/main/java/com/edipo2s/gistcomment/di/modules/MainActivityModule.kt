package com.edipo2s.gistcomment.di.modules

import android.content.Context
import com.edipo2s.gistcomment.di.ActivityScope
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import dagger.Module
import dagger.Provides

@Module
internal class MainActivityModule {

    @ActivityScope
    @Provides
    fun provideQrCodeDetector(context: Context): BarcodeDetector {
        return BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build()
    }

    @ActivityScope
    @Provides
    fun provideCameraSource(context: Context, barcodeDetector: BarcodeDetector): CameraSource {
        return CameraSource.Builder(context, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build()
    }

}