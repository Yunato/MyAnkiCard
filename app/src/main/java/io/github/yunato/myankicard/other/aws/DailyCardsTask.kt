package io.github.yunato.myankicard.other.aws

import android.os.AsyncTask
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.InvokeRequest
import com.google.gson.Gson
import io.github.yunato.myankicard.model.entity.AnkiCard
import org.json.JSONObject
import java.nio.charset.Charset

class DailyCardsTask : AsyncTask<Unit, Unit, String?>() {

    private var mListener: OnFinishListener? = null

    private lateinit var credential: BasicAWSCredentials

    private lateinit var lambda: AWSLambdaClient

    private lateinit var request: InvokeRequest

    override fun onPreExecute() {
        credential = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        lambda = AWSLambdaClient(credential)
        lambda.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1))
        request= InvokeRequest()
        request.apply{
            functionName = "GetHundredCardFunc"
            payload = Charset.forName("UTF-8").encode("")
        }
    }

    override fun doInBackground(vararg params: Unit?): String? {
        try {
            val result = lambda.invoke(request)
            return Charset.forName("UTF-8").decode(result.payload).toString()
        }catch (e: Exception) {
            Log.e("Error", e.toString())
            return null
        }
    }

    override fun onPostExecute(response: String?) {
        val rtnList = mutableListOf<AnkiCard>()
        if (response != null) {
            try {
                val gson = Gson()
                val jsonData = JSONObject(response)
                val subJsonData = jsonData.getJSONArray("Items")
                for (index in 0 until subJsonData.length()) {
                    val jsonString = subJsonData[index].toString()
                    val item = gson.fromJson(jsonString, AnkiCard::class.java)
                    item.isCorrect = true
                    rtnList.add(item)
                }
            } catch (e: org.json.JSONException) {
                Log.e("Error", e.toString())
            }
        }
        mListener?.onFinish(rtnList)
    }

    fun setOnFinishListener(listener: OnFinishListener){
        mListener = listener
    }

    interface OnFinishListener {
        fun onFinish(cardList :List<AnkiCard>)
    }

    companion object {
        @JvmStatic private val ACCESS_KEY = ""
        @JvmStatic private val SECRET_KEY = ""
    }
}
