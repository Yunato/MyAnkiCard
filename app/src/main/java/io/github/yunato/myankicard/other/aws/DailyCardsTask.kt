package io.github.yunato.myankicard.other.aws

import android.os.AsyncTask
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.InvokeRequest
import java.nio.charset.Charset

class DailyCardsTask : AsyncTask<Unit, Unit, Unit>() {

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

    override fun doInBackground(vararg params: Unit?) {
        try {
            val result = lambda.invoke(request)
            val response = Charset.forName("UTF-8").decode(result.payload)
        }catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

    override fun onPostExecute(result: Unit?) {

    }

    companion object {
        @JvmStatic private val ACCESS_KEY = ""
        @JvmStatic private val SECRET_KEY = ""
    }
}
