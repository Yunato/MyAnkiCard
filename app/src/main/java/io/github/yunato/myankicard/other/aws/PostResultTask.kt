package io.github.yunato.myankicard.other.aws

import android.os.AsyncTask
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.InvokeRequest
import com.google.gson.Gson
import io.github.yunato.myankicard.model.entity.PostResult
import io.github.yunato.myankicard.other.application.App
import java.nio.charset.Charset

class PostResultTask : AsyncTask<Unit, Unit, Unit>() {

    private var mListener: OnFinishListener? = null

    private lateinit var credential: BasicAWSCredentials

    private lateinit var lambda: AWSLambdaClient

    private lateinit var request: InvokeRequest

    override fun onPreExecute() {
        credential = BasicAWSCredentials(Credential.ACCESS_KEY, Credential.SECRET_KEY)
        lambda = AWSLambdaClient(credential)
        lambda.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1))
    }

    override fun doInBackground(vararg params: Unit?) {
        val gson = Gson()
        val dao = App.cardDataBase.ankiCardDao()
        val cardList = dao.findAll()
        val postResults = mutableListOf<PostResult>()
        for (card in cardList) {
            val postResult = PostResult (
                card.timestamp,
                card.nextDate,
                card.consecutive,
                card.state
            )
            postResults.add(postResult)
        }

        request= InvokeRequest()
        request.apply{
            functionName = "UpdateCardFunc"
            payload = Charset.forName("UTF-8").encode(gson.toJson(postResults))
        }

        try {
            lambda.invoke(request)
        }catch (e: Exception) {
            Log.e("Error", e.toString())
        }
        dao.deleteAll()
    }

    override fun onPostExecute(result: Unit?) {
        mListener?.onFinish()
    }

    fun setOnFinishListener(listener: OnFinishListener){
        mListener = listener
    }

    interface OnFinishListener {
        fun onFinish()
    }
}
