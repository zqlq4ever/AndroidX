package com.luqian.androidx.ui.other

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil3.load
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.luqian.androidx.R
import com.luqian.androidx.databinding.ActivityCornerBinding
import com.luqian.androidx.ext.dpToPx
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ForwardScope
import java.io.File
import com.zqlq.common.R as BaseR

class CornerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCornerBinding
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private var photoUri: Uri? = null

    companion object {
        private const val URL = "https://haowallpaper.com/link/common/file/previewFileImg/18046455252569472"
        const val PHOTO_NAME = "photo.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_corner)

        initLauncher()

        binding.ivPhoto.load(URL) {
            transformations(RoundedCornersTransformation(28.dpToPx()))
        }

        val clickListener = View.OnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA)
                .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        getString(BaseR.string.permission_camera),
                        getString(BaseR.string.btn_setting)
                    )
                }
                .request { allGranted: Boolean, _, _ ->
                    if (allGranted) {
                        photoUri?.let { uri -> takePhotoLauncher.launch(uri) }
                    }
                }
        }

        binding.ivTakePhoto.setOnClickListener(clickListener)
    }

    private fun initLauncher() {
        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let {
                    binding.ivPhoto.load(it) {
                        transformations(RoundedCornersTransformation(28.dpToPx()))
                    }
                }
            }
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, PHOTO_NAME)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/")
            } else {
                put(MediaStore.Images.Media.DATA, File("${filesDir.absolutePath}/picture/", PHOTO_NAME).absolutePath)
            }
        }
        photoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

}
