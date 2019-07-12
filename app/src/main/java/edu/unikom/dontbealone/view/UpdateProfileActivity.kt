package edu.unikom.dontbealone.view

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_nearby_activity.bBottomNavBack
import kotlinx.android.synthetic.main.activity_update_profile.*
import org.jetbrains.anko.toast


class UpdateProfileActivity : AppCompatActivity() {

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        val user = Gson().fromJson<DataUser>(intent?.extras?.getString("user"), DataUser::class.java)
        setUser(user)
        bBottomNavBack.setOnClickListener { finish() }
        bUpdateProfile.setOnClickListener {
            if (inName.text.toString().trim().length > 0 && inAddress.text.toString().length > 0) {
                updateProfile()
            } else
                toast("Please complete the profile form").show()
        }
    }

    fun setUser(data: DataUser?) {
        inName.setText(data?.name)
        inAddress.setText(data?.address)
        inPhone.setText(data?.phoneNumber)
        inBio.setText(data?.bio)
        inEmail.setText(data?.email)
    }

    fun updateProfile() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Updating your profile...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        webServices.updateUser(
            Helpers.getCurrentUser(this).username,
            inEmail.text.toString(),
            inName.text.toString(),
            inPhone.text.toString(),
            inAddress.text.toString(),
            inBio.text.toString()
        ).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success) {
                    Helpers.setCurrentuser(this, it.data)
                    finish()
                }
                toast(it.message)
                progressDialog.cancel()
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }
}
