package edu.unikom.dontbealone.view.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_nearby_activity.bBottomNavBack
import kotlinx.android.synthetic.main.activity_update_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
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
            if (inName.text.toString().trim().length > 0 && inAddress.text.toString().length > 0 && inEmergencyPhone1.text.toString().length > 0) {
                updateProfile()
            } else
                toast("Please complete the profile form").show()
        }
        imgProfile.setOnClickListener {
            ImagePicker.with(this)
                .compress(2048)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            imgProfile.setImageURI(fileUri)
            val profileImage = ImagePicker.getFile(data)
            if (profileImage != null) {
                Log.d("photo_test", "profileImage!=null")
                val imgPart = MultipartBody.Part.createFormData(
                    "photo",
                    profileImage.name,
                    RequestBody.create(MediaType.parse("multipart"), profileImage)
                )
                val usernamePart = MultipartBody.Part.createFormData("username", Helpers.getCurrentUser(this).username)
                imgProfileLoading.visibility = View.VISIBLE
                webServices.updateUserPhoto(imgPart, usernamePart).subscribeOn(
                    Schedulers.io()
                ).observeOn(
                    AndroidSchedulers.mainThread()
                ).subscribeBy(
                    onNext = {
                        Log.d("photo_test", "onNext " + Gson().toJson(it))
                        if (it.success) {
                            Glide.with(this).load(profileImage).error(R.drawable.ic_user).into(imgProfile)
                        }
                        toast(it.message).show()
                        imgProfileLoading.visibility = View.GONE
                    },
                    onError = {
                        it.printStackTrace()
                        toast("An error has occured, please contact the administrator").show()
                        imgProfileLoading.visibility = View.GONE
                    },
                    onComplete = { }
                )
            }
        }
    }

    fun setUser(data: DataUser?) {
        Glide.with(this).load(data?.photo).error(R.drawable.ic_user).into(imgProfile)
        inName.setText(data?.name)
        inAddress.setText(data?.address)
        inPhone.setText(data?.phoneNumber)
        inBio.setText(data?.bio)
        inEmail.setText(data?.email)
        var emergencyNumbers = data?.emergencyNumber?.split(",")
        if (emergencyNumbers != null) {
            if (emergencyNumbers.size >= 1) {
                inEmergencyPhone1.setText(emergencyNumbers[0])
            }
            if (emergencyNumbers.size >= 2) {
                inEmergencyPhone2.setText(emergencyNumbers[1])
                inEmergencyPhone2.visibility = View.VISIBLE
            }
            if (emergencyNumbers.size >= 3) {
                inEmergencyPhone3.setText(emergencyNumbers[2])
                inEmergencyPhone3.visibility = View.VISIBLE
            }
        }
    }

    fun updateProfile() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Updating your profile...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        var emergencyNumber = inEmergencyPhone1.text.toString().trim()
        if (inEmergencyPhone2.text.toString().trim().length > 0)
            emergencyNumber += "," + inEmergencyPhone2.text.toString().trim()
        if (inEmergencyPhone3.text.toString().trim().length > 0)
            emergencyNumber += "," + inEmergencyPhone3.text.toString().trim()
        webServices.updateUser(
            Helpers.getCurrentUser(this).username,
            inEmail.text.toString(),
            inName.text.toString(),
            inPhone.text.toString(),
            inAddress.text.toString(),
            inBio.text.toString(),
            emergencyNumber
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
