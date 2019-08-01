package io.github.yunato.myankicard.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.github.yunato.myankicard.model.dao.AnkiCardDao

@Database(entities = [AnkiCardDao::class], version = 1)
abstract class CardDataBase: RoomDatabase() {
    abstract fun ankiCardDao(): AnkiCardDao
}
