package com.luqian.androidx.ui.other

import android.Manifest
import android.content.ContentValues
import android.graphics.Outline
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewOutlineProvider
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil.load
import com.bumptech.glide.Glide
import com.luqian.androidx.R
import com.luqian.androidx.databinding.ActivityCornerBinding
import com.luqian.androidx.ext.dpToPx
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ForwardScope
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import java.io.File

class CornerActivity : AppCompatActivity() {

    private lateinit var bind: ActivityCornerBinding

    lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>

    var mPhotoUri: Uri? = null

    companion object {
        const val photoName = "33.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UltimateBarX.with(this)
            .transparent()
            .applyStatusBar()
        bind = DataBindingUtil.setContentView(this, R.layout.activity_corner)

        initLauncher()

        bind.ivPhoto.load("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1977046168,368269341&fm=26&gp=0.jpg")
        setViewCorner(bind.ivPhoto, 20.dpToPx())

        bind.ivPhoto.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
                .onForwardToSettings { scope: ForwardScope, deniedList: List<String?>? ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "请在设置中开启授权",
                        "去设置权限"
                    )
                }
                .request { allGranted: Boolean, _, _ ->
                    if (allGranted) {
                        takePhotoLauncher.launch(mPhotoUri)
                    }
                }
        }
    }


    private fun initLauncher() {
        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { aBoolean: Boolean ->
            if (aBoolean) {
                Glide.with(this).load(mPhotoUri).into(bind.ivPhoto)
            }
        }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, photoName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$photoName")
        } else {
            contentValues.put(
                MediaStore.Images.Media.DATA,
                File(
                    "${filesDir.absolutePath}/picture/",
                    photoName
                ).absolutePath
            )
        }
        mPhotoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }


    /**
     * 给 view 设置圆角
     * @param radius 圆角大小 单位是 px
     */
    private fun setViewCorner(view: View, radius: Float) {

        val provider: ViewOutlineProvider = object : ViewOutlineProvider() {

            override fun getOutline(view: View, outline: Outline) {
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                val leftMargin = 0
                val topMargin = 0
                val selfRect = Rect(
                    leftMargin, topMargin,
                    rect.right - rect.left - leftMargin,
                    rect.bottom - rect.top - topMargin
                )
                outline.setRoundRect(selfRect, radius)
            }
        }
        view.outlineProvider = provider
        view.clipToOutline = true
    }

}