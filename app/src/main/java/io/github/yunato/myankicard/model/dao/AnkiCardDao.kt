package io.github.yunato.myankicard.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.yunato.myankicard.model.entity.AnkiCard

@Dao
interface AnkiCardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCard(card: AnkiCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCard(card: AnkiCard)

    @Query("SELECT * FROM AnkiCard WHERE timestamp = :timestamp")
    fun findOneCard(timestamp: Long): AnkiCard

    @Query("SELECT * FROM AnkiCard")
    fun findAll(): List<AnkiCard>

    @Query("SELECT * FROM AnkiCard WHERE isDaily = :isDaily")
    fun findAllDaily(isDaily: Boolean = true): List<AnkiCard>

    @Query("SELECT COUNT(*) FROM AnkiCard")
    fun getCount(): Long

    @Query("DELETE FROM AnkiCard")
    fun deleteAll()
}
