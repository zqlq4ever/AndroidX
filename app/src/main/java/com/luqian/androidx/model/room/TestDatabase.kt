package com.luqian.androidx.model.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gesturescaleandscroll.model.bean.Person
import com.luqian.androidx.App

/**
 * 文件描述:
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
                App.context,
                TestDatabase::class.java,
                "database_test"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}