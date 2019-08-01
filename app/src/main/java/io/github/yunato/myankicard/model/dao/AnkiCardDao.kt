package io.github.yunato.myankicard.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.github.yunato.myankicard.model.entity.AnkiCard

@Dao
interface AnkiCardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCard(card: AnkiCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCard(card: AnkiCard)

    @Query("SELECT * FROM AnkiCard")
    fun findAll(): List<AnkiCard>

    @Query("DELETE FROM AnkiCard")
    fun deleteAll()
}
