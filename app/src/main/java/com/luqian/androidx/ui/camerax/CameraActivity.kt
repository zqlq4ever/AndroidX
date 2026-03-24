package com.luqian.androidx.ui.camerax

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.Surface
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import coil3.load
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ScreenUtils
import com.luqian.androidx.R
import com.luqian.androidx.databinding.ActivityCameraBinding
import com.luqian.androidx.ext.dpToPx
import com.zqlq.common.base.BaseVmActivity
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraActivity : BaseVmActivity<CameraViewModel, ActivityCameraBinding>() {

    private var mCameraProvider: ProcessCameraProvider? = null

    private var mCamera: Camera? = null

    private var mImageCapture: ImageCapture? = null

    private var lensFacing = CameraSelector.LENS_FACING_BACK

    private var mPhotoFile: File? = null

    private var isFlashOn = false

    override fun getLayoutId(): Int {
        return R.layout.activity_camera
    }

    override fun initView(savedInstanceState: Bundle?) {
        bind.run {
            cameraPreview.post { setupCamera() }
            ivTakePhoto.setOnClickListener { viewmodel.showPhoto(mImageCapture) }
            ivSwitchCamera.setOnClickListener { switchCamera() }
            ivFlash.setOnClickListener { toggleFlash() }
            ivGallery.setOnClickListener { openGallery() }
        }
    }

    override fun initData() {
        FileUtils.deleteAllInDir(PHOTO_PATH)
        FileUtils.createOrExistsDir(PHOTO_PATH)
    }

    override fun initLiveData() {
        super.initLiveData()
        viewmodel.run {
            result.observe(this@CameraActivity, { })
            takePhoto.observe(this@CameraActivity, Observer {
                FileUtils.delete(mPhotoFile)
                mPhotoFile = File(it.path!!)
                currentActivity?.let { activity ->
                    bind.ivPreview.load(mPhotoFile) {
                        transformations(RoundedCornersTransformation(6.dpToPx()))
                    }
                }
            })
        }
    }

    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        bindPreview()
    }

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        mCamera?.cameraControl?.enableTorch(isFlashOn)
        bind.ivFlash.setImageResource(
            if (isFlashOn) R.drawable.ic_torch_on else R.drawable.ic_torch_off
        )
    }

    private fun openGallery() {

    }

    private fun getUriForFile(file: File): android.net.Uri {
        return androidx.core.content.FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                mCameraProvider = cameraProviderFuture.get()
                lensFacing = when {
                    hasBackCamera() -> {
                        CameraSelector.LENS_FACING_BACK
                    }

                    hasFrontCamera() -> {
                        CameraSelector.LENS_FACING_FRONT
                    }

                    else -> {
                        throw IllegalStateException("Back and front camera are unavailable")
                    }
                }
                bind.run {
                    cameraPreview.setOnTouchListener { _, event: MotionEvent ->
                        val action = FocusMeteringAction.Builder(
                            cameraPreview.meteringPointFactory
                                .createPoint(event.x, event.y)
                        ).build()
                        mCamera!!.cameraControl.startFocusAndMetering(action)
                        false
                    }
                }
                bindPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun hasBackCamera(): Boolean {
        return try {
            mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            false
        }
    }

    private fun hasFrontCamera(): Boolean {
        return try {
            mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            false
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height) / min(width, height)
        return if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }

    private fun bindPreview() {
        val rotation = Surface.ROTATION_0
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val screenAspectRatio = aspectRatio(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight())
        val preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
        mImageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
        mCameraProvider!!.unbindAll()
        mCamera = mCameraProvider!!.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            mImageCapture
        )
        preview.surfaceProvider = bind.cameraPreview.surfaceProvider
    }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        val PHOTO_PATH = PathUtils.getExternalAppFilesPath() + "/photo/"
    }
}