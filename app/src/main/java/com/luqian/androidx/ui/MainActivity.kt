package com.luqian.androidx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.example.gesturescaleandscroll.R
import com.luqian.androidx.gesture.GestureScaleHelper
import com.luqian.androidx.model.bean.entity.Person
import com.luqian.androidx.model.room.TestDatabase
import com.luqian.androidx.uitls.WaterMarkUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  添加水印
        WaterMarkUtil.sInstance.show(this, "我是水印")

        //  缩放 view
        GestureScaleHelper.bind(this, root, child).run {
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