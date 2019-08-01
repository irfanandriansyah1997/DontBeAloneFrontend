package edu.unikom.dontbealone.view.profile

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListFriendAdapter
import edu.unikom.dontbealone.model.DataFriend
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_friend.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class FriendFragment : Fragment() {

    companion object {
        fun newInstance(title: String, includePending: Boolean, listener: (user: DataUser) -> Unit): FriendFragment {
            val fragment = FriendFragment()
            fragment.title = title
            fragment.includePending = includePending
            fragment.clickListener = listener
            return fragment
        }
    }

    lateinit var clickListener: (user: DataUser) -> Unit
    private var title = "Friends"
    private var includePending = true
    private var friends = mutableListOf<DataFriend>()
    private var friendAdapter = ListFriendAdapter(friends, {
        if (::clickListener.isInitialized)
            clickListener(it)
        else
            startActivity<ProfileActivity>("user" to it.username)
    }, { dataFriend, status ->
        if (status == 1)
            confirmFriend(dataFriend)
        else
            rejectFriend(dataFriend)
    })

    fun confirmFriend(dataFriend: DataFriend) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Accepting friend...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        webServices.confirmFriend(dataFriend.sender.username, dataFriend.receiver.username).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success) {
                    loadFriend()
                }
                toast(it.message)
                progressDialog.cancel()
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
                progressDialog.cancel()
            },
            onComplete = { }
        )
    }

    fun rejectFriend(dataFriend: DataFriend) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Rejecting friend...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        webServices.rejectFriend(dataFriend.sender.username, dataFriend.receiver.username).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success) {
                    loadFriend()
                }
                toast(it.message)
                progressDialog.cancel()
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
                progressDialog.cancel()
            },
            onComplete = { }
        )
    }

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleListAct.text = title
        listFriend.adapter = friendAdapter
        listFriend.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        listFriend.isNestedScrollingEnabled = false
        listFriend.addItemDecoration(DividerItemDecoration(view.context, RecyclerView.VERTICAL))
    }

    fun loadFriend() {
        listFriend.showShimmerAdapter()
        webServices.getFriend(Helpers.getCurrentUser(context).username).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    friends.clear()
                    friends.addAll(if (includePending) it.data!! else it.data!!.filter { s -> s.status == 1 })
                    friendAdapter.notifyDataSetChanged()
                    tFriendNoData.visibility = if (friends.size > 0) View.GONE else View.VISIBLE
                } else {
                    toast(it.message).show()
                }
                listFriend.hideShimmerAdapter()
            },
            onError = {
                listFriend.hideShimmerAdapter()
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }

    override fun onResume() {
        super.onResume()
        if (context != null)
            loadFriend()
    }

}
