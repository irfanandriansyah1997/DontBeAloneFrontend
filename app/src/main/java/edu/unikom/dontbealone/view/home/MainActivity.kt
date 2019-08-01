package edu.unikom.dontbealone.view.home

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.SmsDeliveredReceiver
import edu.unikom.dontbealone.util.SmsSentReceiver
import edu.unikom.dontbealone.view.NotificationActivity
import edu.unikom.dontbealone.view.activity.InputDialogFragment
import edu.unikom.dontbealone.view.activity.ListActivityFragment
import edu.unikom.dontbealone.view.activity.NearbyListActivity
import edu.unikom.dontbealone.view.chat.MessageFragment
import edu.unikom.dontbealone.view.profile.FriendFragment
import edu.unikom.dontbealone.view.profile.ProfileFragment
import edu.unikom.dontbealone.view.profile.SearchUserActivity
import edu.unikom.dontbealone.view.profile.SearchUserDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    lateinit var menu: MenuDialogFragment
    var currentFragment: Fragment? = null
    var errorPanicButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        menu = MenuDialogFragment.newInstance({
            when (it) {
                R.id.bMenuProfile -> goToProfile()
                R.id.bMenuHome -> goToHome()
                R.id.bMenuFriend -> goToFriend()
                R.id.bMenuMyAct -> goToMyAct()
                R.id.bMenuMessage -> goToMessage()
            }
        })
        bBottomNavMenu.setOnClickListener {
            menu.show(
                supportFragmentManager,
                "menu_dialog_fragment"
            )
        }
        bBottomNavNotif.setOnClickListener {
            startActivity<NotificationActivity>()
        }
        bNewActivity.setOnClickListener {
            if (menu.selectedMenuId == R.id.bMenuHome) {
                startActivity<NearbyListActivity>()
//                val input = SearchUserDialogFragment.newInstance({
//
//                })
//                input.show(
//                    supportFragmentManager,
//                    "search_activity_dialog_fragment"
//                )
            } else if (menu.selectedMenuId == R.id.bMenuMyAct) {
                val input = InputDialogFragment.newInstance({
                    (currentFragment as ListActivityFragment).loadActivity()
                })
                input.show(
                    supportFragmentManager,
                    "input_dialog_fragment"
                )
            } else if (menu.selectedMenuId == R.id.bMenuFriend) {
                val input = SearchUserDialogFragment.newInstance("", {
                    startActivity<SearchUserActivity>("keyword" to it)
                })
                input.show(
                    supportFragmentManager,
                    "input_dialog_fragment"
                )
            } else if (menu.selectedMenuId == R.id.bMenuProfile) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE),
                    1
                )
                toast("Press & hold to activate panic button").show()
            }
        }
        bNewActivity.setOnLongClickListener {
            if (menu.selectedMenuId == R.id.bMenuProfile) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE),
                    1
                )
                val us = Helpers.getCurrentUser(this)
                if (us.emergencyNumber != null && us.emergencyNumber!!.trim().length > 0) {
                    val emNums = us.emergencyNumber!!.split(",")
                    errorPanicButton = false
                    for (s in emNums) {
                        sendSMS(
                            s,
                            "S.O.S EMERGENCY!!!\n" + us.name?.toUpperCase() + " NEED YOUR HELP!\nPLEASE KINDLY HELP " + us.name?.toUpperCase()
                        )
                    }
                    if (!errorPanicButton)
                        toast("Panic button success, we've told your emergency phone number that you need some help")
                } else
                    toast("You have to set at least 1 emergency phone number, edit your profile first")
            }
            true
        }
        goToHome()
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val sentPendingIntents = ArrayList<PendingIntent>()
        val deliveredPendingIntents = ArrayList<PendingIntent>()
        val sentPI = PendingIntent.getBroadcast(
            this, 0,
            Intent(this, SmsSentReceiver::class.java), 0
        )
        val deliveredPI = PendingIntent.getBroadcast(
            this, 0,
            Intent(this, SmsDeliveredReceiver::class.java), 0
        )
        try {
            val sms = SmsManager.getDefault()
            val mSMSMessage = sms.divideMessage(message)
            for (i in 0 until mSMSMessage.size) {
                sentPendingIntents.add(i, sentPI)
                deliveredPendingIntents.add(i, deliveredPI)
            }
            sms.sendMultipartTextMessage(
                phoneNumber, null, mSMSMessage,
                sentPendingIntents, deliveredPendingIntents
            )

        } catch (e: Exception) {
            errorPanicButton = true
            e.printStackTrace()
            toast("Panic button failed")
        }

    }

    fun goToProfile() {
        menu.selectedMenuId = R.id.bMenuProfile
        currentFragment = ProfileFragment.newInstance(Helpers.getCurrentUser(this).username)
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "Panic Button"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_emergency),
            null,
            null,
            null
        )
    }

    fun goToHome() {
        menu.selectedMenuId = R.id.bMenuHome
        currentFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "Find Activity"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_search),
            null,
            null,
            null
        )
    }

    fun goToFriend() {
        menu.selectedMenuId = R.id.bMenuFriend
        currentFragment = FriendFragment()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "Add Friend"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_add_friend),
            null,
            null,
            null
        )
    }

    fun goToMyAct() {
        menu.selectedMenuId = R.id.bMenuMyAct
        currentFragment =
            ListActivityFragment.newInstance(null, Helpers.getCurrentUser(this).username)
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "Create Activity"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_plus),
            null,
            null,
            null
        )
    }

    fun goToMessage() {
        menu.selectedMenuId = R.id.bMenuMessage
        currentFragment = MessageFragment()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.GONE
        bNewActivity.text = "New Message"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_new_message),
            null,
            null,
            null
        )
    }

    override fun onBackPressed() {
        if (menu.selectedMenuId != R.id.bMenuHome) {
            goToHome()
        } else
            finish()
    }
}
