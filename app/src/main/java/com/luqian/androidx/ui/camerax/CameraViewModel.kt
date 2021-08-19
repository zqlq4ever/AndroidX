package com.luqian.androidx.ui.camerax

import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.TimeUtils
import com.fubao.baselibrary.base.BaseViewModel
import java.io.File
import java.util.concurrent.Executors

/**
 * @author LUQIAN
 * @date 2021/8/19
 */
class CameraViewModel : BaseViewModel() {

    val result = MutableLiveData(false)

    val takePhoto = MutableLiveData<Uri>()

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    fun showPhoto(imageCapture: ImageCapture?) {
        if (imageCapture == null) {
            return
        }
        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = false
        val path = CameraActivity.PHOTO_PATH + TimeUtils.getNowString() + ".jpg"
        val file = File(path)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file)
            .setMetadata(metadata)
            .build()
        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    var uri = outputFileResults.savedUri
                    if (uri == null && Uri.fromFile(file) == null) {
                        return
                    }
                    uri = Uri.fromFile(file)
                    takePhoto.postValue(uri!!)
                }

                override fun onError(exception: ImageCaptureException) {

                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}