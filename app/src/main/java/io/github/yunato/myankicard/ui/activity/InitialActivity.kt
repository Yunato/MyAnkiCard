package io.github.yunato.myankicard.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.Gson
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.AnkiCard
import io.github.yunato.myankicard.model.entity.PostResult
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.aws.AwsClient
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.CoroutineContext

class InitialActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    private var stamp: Long = 0

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
    }

    override fun onResume() {
        super.onResume()

        stamp = getTodayStamp()
        if(getStamp() < stamp) {
            removePrimaryKeyForInterruption()
            launch {
                postResultCardToLambda()
                fetchAnkiCardFromLambda()
                startMainActivity()
            }
        } else {
            startMainActivity()
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
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

    private fun removePrimaryKeyForInterruption() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().remove(App.PRAM_PRIMARY_KEY).apply()
    }

    private suspend fun postResultCardToLambda() = withContext(context = Dispatchers.IO) {
        val dao = App.cardDataBase.ankiCardDao()
        val postResults = mutableListOf<PostResult>()
        for (card in dao.findAll()) {
            postResults.add(PostResult(card))
        }

        val client = AwsClient()
        client.postResult(postResults)
        dao.deleteAll()
    }

    private suspend fun fetchAnkiCardFromLambda() = withContext(context = Dispatchers.IO) {
        val client = AwsClient()
        val response = client.getDailyCards() ?: return@withContext

        val dao = App.cardDataBase.ankiCardDao()
        val jsonData = try {
             JSONObject(response).getJSONArray("Items")
        } catch (e: org.json.JSONException) {
            Log.e("Error", e.toString())
            return@withContext
        }
        for (index in 0 until jsonData.length()) {
            val jsonString = jsonData[index].toString()
            dao.insertCard( Gson().fromJson(jsonString, AnkiCard::class.java).apply {
                    isDaily = true
                    state = 0
            })
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
