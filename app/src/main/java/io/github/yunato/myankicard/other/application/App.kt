package io.github.yunato.myankicard.other.application

import android.app.Application
import android.arch.persistence.room.Room
import io.github.yunato.myankicard.model.database.CardDataBase
import io.github.yunato.myankicard.other.preference.Preference

class App : Application() {

    private inline fun <reified T : Any> objectOf() = T::class.java

    override fun onCreate() {
        super.onCreate()

        cardDataBase = Room.databaseBuilder(this, objectOf<CardDataBase>(), CARD_DB_NAME).build()
        preference = Preference(this)
    }

    companion object {
        @JvmStatic private val CARD_DB_NAME = "CardDataBase.db"

        lateinit var cardDataBase: CardDataBase
        lateinit var preference: Preference
    }
}
