package edu.unikom.dontbealone.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.toast

/**
 * Created by Syauqi Ilham on 8/1/2019.
 */

class SmsDeliveredReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent) {
        when(getResultCode()) {
            Activity.RESULT_OK ->
                context.toast("Panic button success, we've told your emergency phone number that you need some help")
            Activity.RESULT_CANCELED ->
                context.toast("Panic button failed")
        }
    }
}