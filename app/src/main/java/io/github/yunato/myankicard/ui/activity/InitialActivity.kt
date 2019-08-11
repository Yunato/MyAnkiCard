package io.github.yunato.myankicard.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        if(App.preference.stamp < stamp) {
            App.preference.removePrimaryKey()
            launch {
                postResultCardToLambda()
                fetchAnkiCardFromLambda()
                App.preference.stamp = stamp
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
            set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            timeInMillis / 1000
        }
    }

    private suspend fun postResultCardToLambda() = withContext(context = Dispatchers.IO) {
        val dao = App.cardDataBase.ankiCardDao()
        val postResults = mutableListOf<PostResult>()
        for (card in dao.findAll()) {
            if (card.timestamp < getTodayStamp() - (24 * 60 * 60)) postResults.add(PostResult(card))
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

    private fun startMainActivity() = startActivity(MainActivity.intent(this@InitialActivity))
}
