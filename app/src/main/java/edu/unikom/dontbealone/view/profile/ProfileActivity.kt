package edu.unikom.dontbealone.view.profile

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import edu.unikom.dontbealone.view.NotificationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class ProfileActivity : AppCompatActivity() {

    private val webServices by lazy {
        WebServices.create()
    }

    var user: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        bBottomNavMenu.setOnClickListener {
            finish()
        }
        bBottomNavNotif.setOnClickListener {
            startActivity<NotificationActivity>()
        }
        user = intent?.extras?.getString("user")
        if (user != null) {
            supportFragmentManager.beginTransaction().replace(R.id.mainFrame, ProfileFragment.newInstance(user!!))
                .commit()
            val isMyUser = Helpers.getCurrentUser(this).username.equals(user)
            bAddFriend.visibility = if (isMyUser) View.GONE else View.VISIBLE
            loadFriendStatus()
            if (!isMyUser) {
                bAddFriend.setOnClickListener {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Adding friend...")
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                    webServices.addFriend(Helpers.getCurrentUser(this).username, user!!).subscribeOn(
                        Schedulers.io()
                    ).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribeBy(
                        onNext = {
                            if (it.success) {
                                loadFriendStatus()
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.mainFrame, ProfileFragment.newInstance(user!!)).commit()
                            } else {
                                toast(it.message).show()
                            }
                            progressDialog.hide()
                        },
                        onError = {
                            it.printStackTrace()
                            toast("An error has occured, please contact the administrator").show()
                            progressDialog.hide()
                        },
                        onComplete = { }
                    )
                }
            }
        } else
            finish()
    }

    fun loadFriendStatus() {
        bAddFriend.isClickable = false
        webServices.getFriendStatus(Helpers.getCurrentUser(this).username, user!!).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success) {
                    bAddFriend.isClickable = it.data == null
                    var btnText = "Add Friend"
                    var btnIcon = R.drawable.ic_add_friend
                    if (it.data != null) {
                        if (it.data!!.status == 1) {
                            btnText = "Already Friend"
                            btnIcon = R.drawable.ic_check
                        } else {
                            btnText = "Waiting Confirmation"
                            btnIcon = R.drawable.ic_clock
                        }
                    }
                    bAddFriend.text = btnText
                    bAddFriend.setCompoundDrawablesWithIntrinsicBounds(
                        resources.getDrawable(btnIcon), null, null, null
                    )
                } else {
                    toast(it.message).show()
                    bAddFriend.isClickable = false
                }
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
                bAddFriend.isClickable = false
            },
            onComplete = { }
        )
    }
}
