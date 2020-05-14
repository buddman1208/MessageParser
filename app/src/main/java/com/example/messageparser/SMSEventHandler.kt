package com.example.messageparser

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.example.messageparser.db.Failure
import com.example.messageparser.db.FailureDB
import com.example.messageparser.db.Set
import com.example.messageparser.db.SetDB
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsyncResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.concurrent.TimeUnit


object SMSEventHandler {
    fun handle(from: String, message: String) {
        val target = findTarget(
            from
                .replace(" ", "")
                .replace("-", "")
                .replace("+", "")
        )
        Log.e("asdf", "target size ${target?.size ?: 0}")
        target?.forEach {
            sendToServer(it, from, message)
        }
    }

    private fun findTarget(number: String): List<Set>? {
        return doAsyncResult {
            SetDB.getInstance(AppController.context)?.setDao()?.loadAllSets()
                ?.filter { number.contains(it.from) }
        }.get()
    }

    public fun sendToServer(set: Set, from : String, message: String) {

        val tMgr = AppController.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val mPhoneNumber = try {
            tMgr.line1Number
        } catch (e : SecurityException) {
            ""
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl("https://your.api.url/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(SendService::class.java)
        val model = PostModel(
            sender = from,
            received = mPhoneNumber,
            message = URLEncoder.encode(message, "UTF-8"),
            key = set.key
        )
        service.postToServer(
            set.sendUrl,
            model
        ).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("asdf", "responseBody ${t.message ?: ""}")
                addFailure(set, model)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.e("asdf", "statusCode ${response.code()} responseBody ${URLDecoder.decode(response.body()?.string() ?: "", "UTF-8")}")
            }
        })
    }


    private fun addFailure(set : Set, model: PostModel) {
        doAsyncResult {
            FailureDB.getInstance(AppController.context)?.failureDao()?.insertFailures(
                Failure(
                    sender = set.from,
                    received = model.received,
                    message = model.message,
                    key = model.key,
                    createDate = System.currentTimeMillis(),
                    url = set.sendUrl
                )
            )
        }.get()
    }
}