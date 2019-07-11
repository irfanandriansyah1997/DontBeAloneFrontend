package edu.unikom.dontbealone.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListActivityTypeListAdapter
import edu.unikom.dontbealone.model.DataActivityType
import edu.unikom.dontbealone.model.WebServiceResult
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_nearby_activity.bBottomNavBack
import kotlinx.android.synthetic.main.activity_type_picker.*
import org.jetbrains.anko.toast

class TypePickerActivity : AppCompatActivity() {

    companion object {
        val ICON_KEY = "TYPE_PICKER_ICON"
        val TYPE_KEY = "TYPE_PICKER_TYPE"
        val ID_TYPE_KEY = "TYPE_PICKER_ID"
        val TYPE_PICKER_REQUEST = 6968
    }

    var listType = mutableListOf<DataActivityType>()
    lateinit var listTypeAdapter: ListActivityTypeListAdapter

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_picker)
        bBottomNavBack.setOnClickListener { finish() }
        listTypeAdapter = ListActivityTypeListAdapter(listType, {
            val returnIntent = Intent()
            returnIntent.putExtra(ICON_KEY, it.icon)
            returnIntent.putExtra(TYPE_KEY, it.name)
            returnIntent.putExtra(ID_TYPE_KEY, it.id)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        })
        listActType.adapter = listTypeAdapter
        listActType.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listActType.isNestedScrollingEnabled = false
        listActType.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        loadType()
    }

    fun loadType() {
        val call: Observable<WebServiceResult<List<DataActivityType>>>
        call = webServices.getActivityType()
        call.subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    listType.clear()
                    listType.addAll(it.data!!)
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

}
