package edu.unikom.dontbealone.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.model.WebServiceResult
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import edu.unikom.dontbealone.view.home.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var twitterAuthClient: TwitterAuthClient
//    private lateinit var firebaseAuth: FirebaseAuth

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(
                TwitterAuthConfig(
                    "vXjxQqF7MIU9JAoiEYEaT7B5f",
                    "jMNMdFPZSNeCFbPF3oe0YC7jOVX71YlmK9NQi5FqFIpTVbQ2zv"
                )
            )
            .debug(true)
            .build()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("946753097645-dnnh0k7k08iqh9e0elpp0urd20hal0hv.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSigninClient = GoogleSignIn.getClient(this, gso)
        callbackManager = CallbackManager.Factory.create()
        Twitter.initialize(this)
        setContentView(edu.unikom.dontbealone.R.layout.activity_login)
//        firebaseAuth = FirebaseAuth.getInstance()
        twitterAuthClient = TwitterAuthClient()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { it, response ->
                        loginFb(it)
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,birthday")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                }

                override fun onError(exception: FacebookException) {
                    exception.printStackTrace()
//                    toast(exception.message).show()
                }
            })
        bLoginFb.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
        bLoginTwitter.setOnClickListener {
            twitterAuthClient.authorize(this@LoginActivity, object : Callback<TwitterSession>() {

                override fun success(result: Result<TwitterSession>?) {
                    if (result != null)
                        loginTw(result.data)
                }

                override fun failure(e: TwitterException) {
                    e.printStackTrace()
                }
            })

        }
        bLoginGplus.setOnClickListener {
            val signInIntent = googleSigninClient.signInIntent
            startActivityForResult(signInIntent, 6969)
        }
        bLogin.setOnClickListener {
            if (inUsername.text.toString().trim().length > 0 && inPassword.text.toString().trim().length > 0) {
                showLoading()
                webServices.login(inUsername.text.toString().trim(), inPassword.text.toString().trim())
                    .subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribeBy(
                        onNext = {
                            handleLogin(it)
                        },
                        onError = {
                            it.printStackTrace()
                            toast("An error has occured, please contact the administrator").show()
                            hideLoading()
                        },
                        onComplete = { }
                    )
            } else
                toast("Please complete the login form").show()
        }
        tRegister.setOnClickListener {
            startActivity<RegisterActivity>()
        }
    }

    private fun loginFb(it: JSONObject) {
        showLoading()
        val id = it.getString("id")
        val email = it.getString("email")
        val name = it.getString("name")
        val photo = "http://graph.facebook.com/$id/picture?type=large"
        webServices.isUsernameExists(it.getString("email")).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.data!!) {
                    webServices.loginFb(email, id).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribeBy(
                        onNext = {
                            handleLogin(it)
                        },
                        onError = {
                            it.printStackTrace()
                            toast("An error has occured, please contact the administrator").show()
                            hideLoading()
                        },
                        onComplete = { }
                    )
                } else {
                    startActivity<RegisterActivity>("email" to email, "fb_id" to id, "name" to name, "photo" to photo)
                    hideLoading()
                }
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
                hideLoading()
            },
            onComplete = { }
        )
    }

    private fun loginGp(account: GoogleSignInAccount?) {
        if (account != null) {
            showLoading()
//            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
//            firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//
//                    } else {
//                        Log.w("login_test", "signInWithCredential:failure", task.exception)
//                        toast("An error has occured, please contact the administrator").show()
//                        hideLoading()
//                    }
//                }
            val id = account.id!!
            val email = account.email!!
            val name = account.displayName!!
            val photo = account.photoUrl.toString()
            webServices.isUsernameExists(email).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeBy(
                onNext = {
                    if (it.data!!) {
                        webServices.loginGp(email, id).subscribeOn(Schedulers.io()).observeOn(
                            AndroidSchedulers.mainThread()
                        ).subscribeBy(
                            onNext = {
                                handleLogin(it)
                            },
                            onError = {
                                it.printStackTrace()
                                toast("An error has occured, please contact the administrator").show()
                                hideLoading()
                            },
                            onComplete = { }
                        )
                    } else {
                        startActivity<RegisterActivity>(
                            "email" to email,
                            "gp_id" to id,
                            "name" to name,
                            "photo" to photo
                        )
                        hideLoading()
                    }
                },
                onError = {
                    it.printStackTrace()
                    toast("An error has occured, please contact the administrator").show()
                    hideLoading()
                },
                onComplete = { }
            )
        }
    }

    private fun loginTw(session: TwitterSession) {
        showLoading()
        val authToken = session.authToken
        val token = authToken.token
        val secret = authToken.secret
        val id = session.userId
        val uname = session.userName
        TwitterCore.getInstance().apiClient.accountService.verifyCredentials(false, false, true)
            .enqueue(object : Callback<User>() {
                override fun success(result: Result<User>?) {
                    if (result != null) {
                        val name = result.data.name
                        val email = result.data.email
                        val photo = result.data.profileImageUrl.replace("_normal", "")
                        webServices.isUsernameExists(email).subscribeOn(Schedulers.io()).observeOn(
                            AndroidSchedulers.mainThread()
                        ).subscribeBy(
                            onNext = {
                                if (it.data!!) {
                                    webServices.loginTw(email, id.toString()).subscribeOn(Schedulers.io()).observeOn(
                                        AndroidSchedulers.mainThread()
                                    ).subscribeBy(
                                        onNext = {
                                            handleLogin(it)
                                        },
                                        onError = {
                                            it.printStackTrace()
                                            toast("An error has occured, please contact the administrator").show()
                                            hideLoading()
                                        },
                                        onComplete = { }
                                    )
                                } else {
                                    startActivity<RegisterActivity>(
                                        "email" to email,
                                        "tw_id" to id.toString(),
                                        "name" to name,
                                        "uname" to uname,
                                        "photo" to photo
                                    )
                                    hideLoading()
                                }
                            },
                            onError = {
                                it.printStackTrace()
                                toast("An error has occured, please contact the administrator").show()
                                hideLoading()
                            },
                            onComplete = { }
                        )
                    }
                }

                override fun failure(exception: TwitterException?) {
                    exception?.printStackTrace()
                    hideLoading()
                }
            })
    }

    private fun handleLogin(it: WebServiceResult<DataUser>) {
        if (it.success) {
            val user = it.data
//            firebaseAuth.signInWithEmailAndPassword(user!!.email, inPassword.text.toString())
//                .addOnSuccessListener {
            Helpers.setCurrentuser(this, user)
            startActivity(intentFor<MainActivity>().newTask().clearTask())
            finish()
//                }
//                .addOnFailureListener {
//                    it.printStackTrace()
//                    toast("An error has occured, please contact the administrator").show()
//                    hideLoading()
//                }
        } else {
            toast(it.message).show()
        }
        hideLoading()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6969 && resultCode != 0) {
            var account: GoogleSignInAccount? = null
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                account = task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                e.printStackTrace()
            } finally {
                loginGp(account)
            }
        } else if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return
        } else
            twitterAuthClient.onActivityResult(requestCode, resultCode, data)
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
