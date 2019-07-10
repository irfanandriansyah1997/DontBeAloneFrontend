package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
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
                    Toast.makeText(view.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
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
    }

}
