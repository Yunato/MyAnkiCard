package io.github.yunato.myankicard.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.AnkiCard
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.aws.DailyCardsTask
import io.github.yunato.myankicard.other.aws.PostResultTask
import java.util.*
import kotlin.concurrent.thread

class InitialActivity : AppCompatActivity() {

    private var stamp: Long = 0
    lateinit var mCardList: List<AnkiCard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
    }

    override fun onResume() {
        super.onResume()

        stamp = getTodayStamp()
        if(getStamp() != stamp) {
            // TODO Check Interrupt
            postResultCardToLambda()
        } else {
            postResultCardToLambda()
        }
    }

    private fun getTodayStamp(): Long {
        return Calendar.getInstance().run {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            timeInMillis / 1000
        }
    }

    private fun setStamp(stamp: Long) {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().putLong(STATE_STAMP, stamp).apply()
    }

    private fun getStamp(): Long {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getLong(STATE_STAMP, PREFERENCE_INIT)
    }

    private fun postResultCardToLambda() {
        val postTask = PostResultTask()
        postTask.setOnFinishListener(object: PostResultTask.OnFinishListener {
            override fun onFinish() {
                fetchAnkiCardFromLambda()
            }
        })
        postTask.execute()
    }

    private fun fetchAnkiCardFromLambda() {
        val getTask = DailyCardsTask()
        getTask.setOnFinishListener(object: DailyCardsTask.OnFinishListener {
            override fun onFinish(cardList: List<AnkiCard>) {
                mCardList = cardList
                insertCardListToDB()
            }
        })
        getTask.execute()
    }

    private fun insertCardListToDB() {
        val dao = App.cardDataBase.ankiCardDao()
        thread {
            for (card in mCardList) {
                dao.insertCard(card)
            }
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        setStamp(stamp)
        startActivity(MainActivity.intent(this@InitialActivity))
    }

    companion object {
        @JvmStatic private val STATE_STAMP: String = "io.github.yunato.myankicard.ui.activity.STATE_STAMP"
        @JvmStatic private val PREFERENCE_INIT: Long = 0
    }
}
