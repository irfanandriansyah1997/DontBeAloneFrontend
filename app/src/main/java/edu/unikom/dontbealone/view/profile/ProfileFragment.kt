package edu.unikom.dontbealone.view.profile

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListActivityAdapter
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import edu.unikom.dontbealone.view.LoginActivity
import edu.unikom.dontbealone.view.activity.DetailActivity
import edu.unikom.dontbealone.view.activity.ListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class ProfileFragment : Fragment() {

    var listMyActivity = mutableListOf<DataActivity>()
    lateinit var listMyActAdapter: ListActivityAdapter

    companion object {
        fun newInstance(user: String): ProfileFragment {
            var fragment = ProfileFragment()
            fragment.user = user
            return fragment
        }
    }

    private lateinit var user: String

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
        btnProfileSetting.visibility =
            if (user.equals(Helpers.getCurrentUser(view.context).username)) View.VISIBLE else View.GONE
        listMyActAdapter = ListActivityAdapter(activity!!, listMyActivity, false, {
            startActivity<DetailActivity>("id_activity" to it.id)
        })
        listProfileAct.isNestedScrollingEnabled = false
        listProfileAct.adapter = listMyActAdapter
        listProfileAct.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        btnProfileActAll.setOnClickListener {
            startActivity<ListActivity>("user" to user)
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    fun loadProfile() {
        webServices.getUser(user).subscribeOn(
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
        listProfileAct.showShimmerAdapter()
        webServices.getActivityByUser(user, 2).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    listMyActivity.clear()
                    listMyActivity.addAll(it.data!!)
                    listMyActAdapter.notifyDataSetChanged()
                } else {
                    toast(it.message).show()
                }
                listProfileAct.hideShimmerAdapter()
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
                listProfileAct.hideShimmerAdapter()
            },
            onComplete = { }
        )
    }

    fun showProfile(data: DataUser) {
        if (user.equals(Helpers.getCurrentUser(context!!).username))
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
        Glide.with(context!!).load(data.photo).error(R.drawable.ic_user).into(imgProfileProfile)
        if (user.equals(Helpers.getCurrentUser(context!!).username) &&
            (data.name == null || data.name.equals("") ||
                    data.address == null || data.address.equals(""))
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
