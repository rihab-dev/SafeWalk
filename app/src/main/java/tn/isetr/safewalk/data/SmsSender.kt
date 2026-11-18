package tn.isetr.safewalk.data

import android.content.Context
import android.os.Build
import android.telephony.SmsManager

object SmsSender {
    fun send(context: Context, numero: String, message: String) {
        val sms: SmsManager =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                context.getSystemService(SmsManager::class.java)
            else
                @Suppress("DEPRECATION") SmsManager.getDefault()

        val parts = sms.divideMessage(message)
        sms.sendMultipartTextMessage(numero, null, parts, null, null)
    }
}