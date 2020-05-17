package com.example.messageparser

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import com.example.messageparser.SMSEventHandler.handle

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "BroadcastReceiver Received")
        if ("android.provider.Telephony.SMS_RECEIVED" == intent.action || "android.provider.Telephony.MMS_RECEIVED" == intent.action) {
            val bundle = intent.extras?.get("pdus") as Array<Any>
            val smsMessage = bundle.map { SmsMessage.createFromPdu(it as ByteArray) }

            val messageObjects = smsMessage.groupBy { it.originatingAddress }
            messageObjects.forEach {
                it.key?.run {
                    handle(this, it.value.sortedBy { it.timestampMillis }.joinToString("") { it.messageBody })
                }
            }
        }
    }

    companion object {
        private const val TAG = "SMSReceiver"
    }
}