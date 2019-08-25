package dev.trotrohailer.shared.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<R> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: MutableList<R>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: R)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: R)

    @Delete
    fun deleteAll(items: MutableList<R>)

    @Delete
    fun delete(item: R)
}