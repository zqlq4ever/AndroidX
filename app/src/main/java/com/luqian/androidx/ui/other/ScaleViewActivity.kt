package com.luqian.androidx.ui.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil.load
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.luqian.androidx.R
import com.luqian.androidx.databinding.ActivityScaleBinding
import com.luqian.androidx.gesture.GestureScaleHelper
import com.luqian.androidx.model.bean.entity.Person
import com.luqian.androidx.model.room.TestDatabase
import com.luqian.androidx.uitls.WaterMarkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScaleViewActivity : AppCompatActivity() {

    lateinit var bind: ActivityScaleBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_scale)

        //  添加水印
        WaterMarkUtil.sInstance.show(this, TimeUtils.date2String(TimeUtils.getNowDate()))

        bind.child.load("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1977046168,368269341&fm=26&gp=0.jpg")

        //  缩放 view
        GestureScaleHelper.bind(this, bind.root, bind.child).run {
            isFullGroup = true
        }

        CoroutineScope(Dispatchers.Main).launch {
            testRoom()
        }
    }


    private suspend fun testRoom() = withContext(Dispatchers.IO) {
        //  room 使用
        val testDao = TestDatabase.database.getTestDao()
        testDao.addUser(Person().also {
            it.name = "kotlin"
            it.age = 8
        })

        val dataCount = testDao.getDataCount()
        LogUtils.d(dataCount)
        if (dataCount > 100) {
            testDao.deleteAll()
        }
    }
}