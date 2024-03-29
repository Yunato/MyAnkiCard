package io.github.yunato.myankicard.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.yunato.myankicard.model.dao.AnkiCardDao
import io.github.yunato.myankicard.model.entity.AnkiCard

@Database(entities = [AnkiCard::class], version = 1)
abstract class CardDataBase : RoomDatabase() {
    abstract fun ankiCardDao(): AnkiCardDao
}
