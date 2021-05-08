package com.luqian.androidx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.luqian.androidx.R
import com.luqian.androidx.databinding.ActivityMainBinding
import com.luqian.androidx.gesture.GestureScaleHelper
import com.luqian.androidx.model.bean.entity.Person
import com.luqian.androidx.model.room.TestDatabase
import com.luqian.androidx.uitls.WaterMarkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var bind: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //  添加水印
        WaterMarkUtil.sInstance.show(this, TimeUtils.date2String(TimeUtils.getNowDate()))

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