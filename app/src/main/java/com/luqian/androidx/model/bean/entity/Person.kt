package com.luqian.androidx.model.bean.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 文件描述:Room 数据库表结构
 * 作者:  luqian
 * 创建时间:  2021/1/8
 *
 */
@Entity(tableName = "user_info")
class Person {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(defaultValue = "")
    var name: String = ""

    @ColumnInfo(name = "user_age")
    var age: Int = 0

    var userId: String = ""

    @Ignore
    var sex: String = "male"
}