package com.luqian.androidx.model.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.luqian.androidx.model.bean.entity.Person
import com.luqian.androidx.App

/**
 * 文件描述:
 *    Room 数据库
 *    创建数据库
 *    定义并获取 dao
 *
 * 作者:  luqian
 * 创建时间:  2021/1/8
 *
 */
@Database(entities = [Person::class], version = 11)
abstract class TestDatabase : RoomDatabase() {

    abstract fun getTestDao(): TestDao

    companion object {
        val database by lazy {
            Room.databaseBuilder(
                App.mContext,
                TestDatabase::class.java,
                "database_test"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}