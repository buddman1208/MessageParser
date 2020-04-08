package com.example.messageparser

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.jetbrains.anko.doAsyncResult
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder


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

    private fun sendToServer(set: Set, from : String, message: String) {

        val tMgr = AppController.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val mPhoneNumber = try {
            tMgr.line1Number
        } catch (e : SecurityException) {
            ""
        }

        val client = AsyncHttpClient()
        val params = JSONObject()
        params.put("from", from)
        params.put("received", mPhoneNumber)
        params.put("message", URLEncoder.encode(message, "UTF-8"))
        params.put("key", set.key)

        val entity = StringEntity(params.toString())
        client.post(
            AppController.context,
            set.sendUrl,
            entity,
            "application/json",
            object : AsyncHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?
                ) {
                    responseBody?.run {
                        Log.e("asdf", "statusCode $statusCode responseBody ${URLDecoder.decode(String(responseBody), "UTF-8")}")
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable?
                ) {
                    error?.run {
                        Log.e("asdf", "statusCode $statusCode responseBody ${error.localizedMessage ?: ""}")
                    }
                }
            }
        )
    }
}