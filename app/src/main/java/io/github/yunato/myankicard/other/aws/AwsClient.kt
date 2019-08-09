package io.github.yunato.myankicard.other.aws

import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.InvokeRequest
import com.google.gson.Gson
import io.github.yunato.myankicard.model.entity.PostResult
import java.nio.charset.Charset

class AwsClient {

    private val credential = BasicAWSCredentials(Credential.ACCESS_KEY, Credential.SECRET_KEY)
    private val lambda = AWSLambdaClient(credential).apply {
        setRegion(Region.getRegion(Regions.AP_NORTHEAST_1))
    }

    private fun invoke(funcName: String, content: String): String? {
        val request = InvokeRequest().apply {
            functionName = funcName
            payload = Charset.forName("UTF-8").encode(content)
        }

        return try {
            Charset.forName("UTF-8").decode(lambda.invoke(request).payload).toString()
        } catch (e: Exception) {
            Log.e("Error", e.toString())
            null
        }
    }

    fun getDailyCards(): String? = invoke("GetHundredCardFunc", "")

    fun postResult(postResults: List<PostResult>) = invoke("UpdateCardFunc", Gson().toJson(postResults))
}
