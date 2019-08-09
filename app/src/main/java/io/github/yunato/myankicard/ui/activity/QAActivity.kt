package io.github.yunato.myankicard.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.google.gson.Gson
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.AnkiCard
import io.github.yunato.myankicard.model.entity.QACard
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.aws.AwsClient
import io.github.yunato.myankicard.ui.fragment.*
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class QAActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    private var phase: Int = PHASE_START
    private var startPos: Long = -1L
    private lateinit var qaFragment: QAFragment

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qa)

        qaFragment = when (intent.getIntExtra(MODE_EXTRA, MODE_LEARN_DAILY)) {
            MODE_LEARN_DAILY -> {
                startPos = App.preference.primaryKey
                LearnQAFragment.newInstance()
            }
            MODE_TEST_DAILY -> TestQAFragment.newInstance()
            MODE_LEARN_NEW -> {
                startPos = App.preference.primaryKey
                LearnQAFragment.newInstance()
            }
            else -> throw IllegalStateException("State is not correct")
        }
    }

    override fun onResume() {
        super.onResume()

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.fragment_container)
        if (fragment != null) return

        launch {
            val cardList = withContext(context = Dispatchers.IO) {
                 when (intent.getIntExtra(MODE_EXTRA, MODE_LEARN_DAILY)) {
                    MODE_LEARN_DAILY, MODE_TEST_DAILY -> {
                        fetchDailyFromDB(startPos)
                    }
                    MODE_LEARN_NEW -> fetchNewFromLabmda()
                    else -> throw IllegalStateException("State is not correct")
                }
            }
            if (cardList.isNotEmpty()) {
                qaFragment.setCardList(cardList)
                fm.beginTransaction().replace(R.id.fragment_container, StartFragment.newInstance()).commit()
            } else {
                Toast.makeText(this@QAActivity, getText(R.string.toast_message), Toast.LENGTH_SHORT).show()
                this@QAActivity.finish()
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    fun switchFragment(vararg values: Int) {
        val fm = supportFragmentManager
        when (phase) {
            PHASE_START -> {
                fm.beginTransaction().replace(R.id.fragment_container, qaFragment).commit()
                phase = PHASE_QA
            }
            PHASE_QA -> {
                try {
                    val fragment = EndFragment.newInstance(values[0], values[1], values[2])
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
                    phase = PHASE_END
                } catch (e: IndexOutOfBoundsException) {
                    Log.e("Error", e.toString())
                }
            }
            PHASE_END -> finish()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (PHASE_QA == phase) qaFragment.onPressBackKey()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    fun fetchDailyFromDB(stampForFirst: Long): List<QACard> {
        val dao = App.cardDataBase.ankiCardDao()
        val cardList = dao.findAllDaily()
        val qaCards = mutableListOf<QACard>()
        for (card in cardList) {
            if (card.timestamp == stampForFirst) qaCards.clear()
            qaCards.add(QACard(card))
        }
        return qaCards
    }

    fun fetchNewFromLabmda(): List<QACard> {
        val client = AwsClient()
        val response = client.getNewCards() ?: return mutableListOf()

        val dao = App.cardDataBase.ankiCardDao()
        val jsonData = try {
            JSONObject(response).getJSONArray("Items")
        } catch (e: org.json.JSONException) {
            Log.e("Error", e.toString())
            return mutableListOf()
        }
        val qaCards = mutableListOf<QACard>()
        for (index in 0 until jsonData.length()) {
            val jsonString = jsonData[index].toString()
            val ankiCard = Gson().fromJson(jsonString, AnkiCard::class.java).apply {
                isDaily = false
                state = 0
            }
            dao.insertCard(ankiCard)
            qaCards.add(QACard(ankiCard))
        }
        return qaCards
    }

    companion object{
        @JvmStatic private val MODE_EXTRA = "io.github.yunato.myankicard.ui.activity.STATE_MODE"
        @JvmStatic val MODE_LEARN_DAILY = 0
        @JvmStatic val MODE_TEST_DAILY = 1
        @JvmStatic val MODE_LEARN_NEW = 2

        @JvmStatic private val PHASE_START = 0
        @JvmStatic private val PHASE_QA = 1
        @JvmStatic private val PHASE_END = 2

        fun intent(context: Context, state: Int): Intent =
            Intent(context, QAActivity::class.java).putExtra(MODE_EXTRA, state)
    }
}
