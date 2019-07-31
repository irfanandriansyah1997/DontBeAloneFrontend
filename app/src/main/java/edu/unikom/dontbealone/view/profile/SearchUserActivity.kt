package edu.unikom.dontbealone.view.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListUserAdapter
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_user.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SearchUserActivity : AppCompatActivity() {

    private var keyword: String = ""
    private var users = mutableListOf<DataUser>()
    private var userAdapter = ListUserAdapter(users, {
        startActivity<ProfileActivity>("user" to it.username)
    })

    private val webServices by lazy {
        WebServices.create()
    }

    companion object {
        fun newInstance(keyword: String): SearchUserActivity {
            val fragment = SearchUserActivity()
            fragment.keyword = keyword
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        if (intent?.extras?.getString("keyword") != null) {
            keyword = intent?.extras?.getString("keyword")!!
            listUser.adapter = userAdapter
            listUser.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            listUser.isNestedScrollingEnabled = false
            listUser.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))

            bApplyFilter.setOnClickListener {
                val input = SearchUserDialogFragment.newInstance(keyword, {
                    keyword = it
                    loadUser()
                })
                input.show(
                    supportFragmentManager,
                    "input_dialog_fragment"
                )
            }
            bBottomNavBack.setOnClickListener { finish() }
        } else finish()
    }

    fun loadUser() {
        listUser.showShimmerAdapter()
        webServices.searchUser(keyword).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    users.clear()
                    users.addAll(it.data!!)
                    userAdapter.notifyDataSetChanged()
                    tUserNoData.visibility = if (users.size > 0) View.GONE else View.VISIBLE
                } else {
                    toast(it.message).show()
                }
                listUser.hideShimmerAdapter()
            },
            onError = {
                listUser.hideShimmerAdapter()
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }

    override fun onResume() {
        super.onResume()
        if (listUser != null)
            loadUser()
    }

}
