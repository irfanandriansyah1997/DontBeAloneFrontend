package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast

class RegisterActivity : AppCompatActivity() {

    private var photo: String = ""
    private var name: String = ""
    private var uname: String = ""
    private var email: String? = null
    private var fbId: String = ""
    private var twId: String = ""
    private var gpId: String = ""

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        uname =
            if (intent.extras != null && intent.extras.containsKey("uname")) intent.extras?.getString("uname")!! else ""
        name =
            if (intent.extras != null && intent.extras.containsKey("name")) intent.extras?.getString("name")!! else ""
        email = intent.extras?.getString("email")
        fbId =
            if (intent.extras != null && intent.extras.containsKey("fb_id")) intent.extras?.getString("fb_id")!! else ""
        twId =
            if (intent.extras != null && intent.extras.containsKey("tw_id")) intent.extras?.getString("tw_id")!! else ""
        gpId =
            if (intent.extras != null && intent.extras.containsKey("gp_id")) intent.extras?.getString("gp_id")!! else ""
        photo =
            if (intent.extras != null && intent.extras.containsKey("photo")) intent.extras?.getString("photo")!! else ""
        inUsername.setText(uname)
        inEmail.setText(email)
        inEmail.visibility = if (email != null) View.GONE else View.VISIBLE
        inPassword.visibility = if (email != null) View.GONE else View.VISIBLE
        tLogin.setOnClickListener {
            finish()
        }
        bSignUp.setOnClickListener {
            if (inUsername.text.toString().trim().length > 0 && inEmail.text.toString().trim().length > 0) {
                if (email == null && inPassword.text.toString().trim().length == 0) {
                    toast("Please complete the sign up form").show()
                } else {
                    showLoading()
                    webServices.register(
                        inUsername.text.toString().trim(),
                        inEmail.text.toString(),
                        name,
                        fbId,
                        twId,
                        gpId,
                        photo,
                        inPassword.text.toString()
                    ).subscribeOn(
                        Schedulers.io()
                    ).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribeBy(
                        onNext = {
                            if (it.success) {
                                Helpers.setCurrentuser(this, it.data)
                                startActivity(intentFor<MainActivity>().newTask().clearTask())
                                finish()
                                toast("User Register Success").show()
                            } else {
                                toast(it.message).show()
                            }
                            hideLoading()
                        },
                        onError = {
                            it.printStackTrace()
                            toast("An error has occured, please contact the administrator").show()
                            hideLoading()
                        },
                        onComplete = { }
                    )
                }
            } else
                toast("Please complete the sign up form").show()
        }
    }

    private fun showLoading() {
        vLoadingBg.visibility = View.VISIBLE
        vLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vLoadingBg.visibility = View.GONE
        vLoading.visibility = View.GONE
    }

}
