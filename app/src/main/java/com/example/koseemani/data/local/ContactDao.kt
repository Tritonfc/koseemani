package com.example.koseemani.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)
    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)


    @Query("SELECT * from contacts ORDER BY id")
    fun getAllContacts(): Flow<List<Contact>>

}