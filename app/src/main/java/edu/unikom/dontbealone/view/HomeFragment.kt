package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListActivityAdapter
import edu.unikom.dontbealone.adapter.ListActivityTypeAdapter
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.model.DataActivityType
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class HomeFragment : Fragment() {

    var listMyActivity = mutableListOf<DataActivity>()
    lateinit var listMyActAdapter: ListActivityAdapter

    var listActivityType = mutableListOf<DataActivityType>()
    lateinit var listTypeAdapter: ListActivityTypeAdapter

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listMyActAdapter = ListActivityAdapter(listMyActivity, {
            startActivity<DetailActivity>("id_activity" to it.id)
        })
        listMyAct.adapter = listMyActAdapter
        listMyAct.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        listMyAct.isNestedScrollingEnabled = false

        listTypeAdapter = ListActivityTypeAdapter(listActivityType, {
        })
        listTrendAct.adapter = listTypeAdapter
        listTrendAct.layoutManager = GridLayoutManager(view.context, 3)
        listTrendAct.isNestedScrollingEnabled = false
//        loadMyActivity()
    }

    fun loadMyActivity() {
        webServices.getActivityByUser(Helpers.getCurrentUser(context).username, 2).subscribeOn(
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
            },
            onError = {
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }

    fun loadTrendAct() {
        webServices.getActivityType().subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    listActivityType.clear()
                    listActivityType.addAll(it.data!!)
                    listTypeAdapter.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()
        loadMyActivity()
        loadTrendAct()
    }

}
