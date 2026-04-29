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
    suspend fun getUserByName(name: String): Person

    @Query("select * from user_info where user_age >:age")
    suspend fun getUserByAge(age: Int): Person

    @Query("select * from user_info")
    suspend fun getAllUser(): List<Person>

    @Query("select count(*) from user_info")
    suspend fun getDataCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: Person)

    @Delete
    suspend fun deleteUser(user: Person)

    @Query("delete  from user_info where userId = :id ")
    suspend fun deleteUserById(id: String)

    @Query("delete from user_info")
    suspend fun deleteAll()

    @Update
    suspend fun updateUser(user: Person)

    @Query("update  user_info set name = :updateName where userId =  :id")
    suspend fun update(id: String, updateName: String)
}