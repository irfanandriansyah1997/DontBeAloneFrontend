package edu.unikom.dontbealone.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class ProfileFragment : Fragment() {

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnProfileSetting.setOnClickListener {
            val popup = PopupMenu(view.context, it)
            popup.menuInflater.inflate(R.menu.menu_profile, popup.menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    if (item.itemId == R.id.menu_edit) {
                        goToUpdateProfile(view.context)
                    } else if (item.itemId == R.id.menu_logout) {
                        Helpers.setCurrentuser(view.context, null)
                        startActivity<LoginActivity>()
                        activity?.finish()
                    }
                    return true
                }
            })
            popup.show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    fun loadProfile() {
        webServices.getUser(Helpers.getCurrentUser(context).username).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    showProfile(it.data!!)
                } else {
                    toast(it.message).show()
                }
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }

    fun showProfile(data: DataUser) {
        Helpers.setCurrentuser(context!!, data)
        txtProfileName.text = if (data.name != null && !data.name.equals("")) data.name else data.username
        txtProfileBio.text = data.bio
        txtProfileBioLine.visibility = if (data.bio != null && !data.bio.equals("")) View.VISIBLE else View.GONE
        txtProfileBioTitle.visibility = if (data.bio != null && !data.bio.equals("")) View.VISIBLE else View.GONE
        txtProfileBio.visibility = if (data.bio != null && !data.bio.equals("")) View.VISIBLE else View.GONE
        txtProfileEmail.text = data.email
        txtProfilePhone.text = data.phoneNumber
        txtProfilePhone.visibility =
            if (data.phoneNumber != null && !data.phoneNumber.equals("")) View.VISIBLE else View.GONE
        txtProfileFacebook.text = data.name
        txtProfileTwitter.text = data.name
        txtProfileAddress.text = data.address
        txtProfileAddressLine.visibility =
            if (data.address != null && !data.address.equals("")) View.VISIBLE else View.GONE
        txtProfileAddressTitle.visibility =
            if (data.address != null && !data.address.equals("")) View.VISIBLE else View.GONE
        txtProfileAddress.visibility = if (data.address != null && !data.address.equals("")) View.VISIBLE else View.GONE
        Glide.with(context!!).load(data.photo).into(imgProfileProfile)
        if (data.name == null || data.name.equals("") ||
            data.address == null || data.address.equals("")
        ) {
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage("It looks like your profile missing some information, please update your profile to complete it!")
                .setPositiveButton("Update Profile", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        goToUpdateProfile(context!!)
                    }
                }).setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.cancel()
                    }

                }).show()
        }
    }

    fun goToUpdateProfile(context: Context) {
        startActivity<UpdateProfileActivity>("user" to Gson().toJson(Helpers.getCurrentUser(context)))
    }

}
