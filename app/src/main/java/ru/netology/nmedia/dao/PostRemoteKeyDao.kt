package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.netology.nmedia.entity.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {

    @Query("SELECT max(`key`) FROM PostRemoteKeyEntity")    // возвращение id самого свежего поста из DB
    suspend fun max(): Long?

    @Query("SELECT min(`key`) FROM PostRemoteKeyEntity")    // возвращение id самого старого поста из DB
    suspend fun min(): Long?

    @Upsert
    suspend fun insert(postRemoteKeyEntity: PostRemoteKeyEntity)

    @Upsert
    suspend fun insert(postRemoteKeyEntity: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM PostRemoteKeyEntity")   // очистка таблицы
    suspend fun clear()

    @Query("SELECT COUNT(*) == 0 FROM PostRemoteKeyEntity")
    suspend fun emptyList(): Boolean
}