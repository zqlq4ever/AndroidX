package com.luqian.androidx.model.room

import androidx.room.*
import com.luqian.androidx.model.bean.entity.Person

/**
 * 文件描述:DAO 文件,负责数据库增删改查操作
 * 作者:  luqian
 * 创建时间:  2021/1/8
 *
 */
@Dao
interface TestDao {
    @Query("select * from user_info where name = :name")
    fun getUserByName(name: String): Person

    @Query("select * from user_info where user_age >:age")
    fun getUserByAge(age: Int): Person

    @Query("select * from user_info")
    fun getAllUser(): List<Person>

    @Query("select count(*) from user_info")
    fun getDataCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: Person)

    @Delete
    fun deleteUser(user: Person)

    @Query("delete  from user_info where userId = :id ")
    fun deleteUserById(id: String)

    @Query("delete from user_info")
    fun deleteAll()

    @Update
    fun updateUser(user: Person)

    @Query("update  user_info set name = :updateName where userId =  :id")
    fun update(id: String, updateName: String)
}