package edu.unikom.dontbealone.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataActivityType
import kotlinx.android.synthetic.main.activity_nearby_activity.*
import java.util.*
import kotlin.concurrent.schedule

class NearbyListActivity : AppCompatActivity() {

    var waitingFinished = false
    var loadingFinished = false
    var type: DataActivityType? = null
    var listMode = true
    var keyword: String? = null
    lateinit var listFragment: ListActivityFragment
    lateinit var mapFragment: MapsActivityFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_activity)
        showLoading()
        type = Gson().fromJson(intent?.extras?.getString("type"), DataActivityType::class.java)
        listFragment = ListActivityFragment.newInstance({
            loadingFinished = it
            if (waitingFinished)
                hideLoading()
        }, if (type != null) type!!.id else -1)
        mapFragment = MapsActivityFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, listFragment).commit()
        Timer("LoadingNearby", false).schedule(2000) {
            waitingFinished = true
            if (loadingFinished)
                hideLoading()
        }
        bBottomNavBack.setOnClickListener { finish() }
        bBottomNavViewSwitch.setOnClickListener {
            listMode = !listMode
            switchMode()
        }
        bApplyFilter.setOnClickListener {
            val input = FilterActivityDialogFragment.newInstance(keyword, type, { keyword, type ->
                if (listMode) {
                    this.keyword = keyword
                    this.type = type
                    listFragment.keyword = keyword
                    listFragment.type = if (type != null) type.id else -1
                    listFragment.loadActivity()
                } else {
                    this.keyword = keyword
                    this.type = type
                    mapFragment.keyword = keyword
                    mapFragment.type = if (type != null) type.id else -1
                    mapFragment.loadActivity()
                }
            })
            input.show(
                supportFragmentManager,
                "input_dialog_fragment"
            )
        }
    }

    fun switchMode() {
        Glide.with(this).load(if (listMode) R.drawable.ic_map else R.drawable.ic_list).into(bBottomNavViewSwitch)
        if (listMode) {
            listFragment.keyword = keyword
            listFragment.type = if (type != null) type!!.id else -1
            supportFragmentManager.beginTransaction().replace(mainFrame.id, listFragment).commit()
        } else {
            mapFragment.keyword = keyword
            mapFragment.type = if (type != null) type!!.id else -1
            supportFragmentManager.beginTransaction().replace(mainFrame.id, mapFragment).commit()
        }
    }

    fun showLoading() {
        runOnUiThread {
            mainContainer.visibility = View.GONE
            tLoading.visibility = View.VISIBLE
            imageLoading.visibility = View.VISIBLE
            Glide.with(this).asGif().load(R.raw.nearby_loading).into(imageLoading)
        }
    }

    fun hideLoading() {
        runOnUiThread {
            mainContainer.visibility = View.VISIBLE
            tLoading.visibility = View.GONE
            imageLoading.visibility = View.GONE
        }
    }
}
