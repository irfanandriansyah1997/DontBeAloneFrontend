package edu.unikom.dontbealone.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import org.jetbrains.anko.toast

/**
 * Created by Syauqi Ilham on 8/1/2019.
 */
class SmsSentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent) {
        when (resultCode) {
            Activity.RESULT_OK ->
                context.toast("Panic button success, we've told your emergency phone number that you need some help")
            SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                context.toast("Panic button failed, generic failure")
            SmsManager.RESULT_ERROR_NO_SERVICE ->
                context.toast("Panic button failed, no service")
            SmsManager.RESULT_ERROR_NULL_PDU ->
                context.toast("Panic button failed, null pdu")
            SmsManager.RESULT_ERROR_RADIO_OFF ->
                context.toast("Panic button failed, radio off")
        }
    }
}