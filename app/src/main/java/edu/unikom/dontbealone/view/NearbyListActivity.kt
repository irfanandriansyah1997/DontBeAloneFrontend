package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import kotlinx.android.synthetic.main.activity_nearby_activity.*
import java.util.*
import kotlin.concurrent.schedule

class NearbyListActivity : AppCompatActivity() {

    var waitingFinished = false
    var loadingFinished = false
    var type: Int? = null
    lateinit var listFragment: ListActivityFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_activity)
        showLoading()
        type = intent?.extras?.getInt("type")
        listFragment = ListActivityFragment.newInstance({
            loadingFinished = it
            if (waitingFinished)
                hideLoading()
        }, if (type != null) type!! else -1)
        supportFragmentManager.beginTransaction().replace(mainFrame.id, listFragment).commit()
        Timer("LoadingNearby", false).schedule(2000) {
            waitingFinished = true
            if (loadingFinished)
                hideLoading()
        }
        bBottomNavBack.setOnClickListener { finish() }
        bApplyFilter.setOnClickListener {
            val input = FilterActivityDialogFragment.newInstance({
                listFragment.keyword = it
                listFragment.loadActivity()
            })
            input.show(
                supportFragmentManager,
                "input_dialog_fragment"
            )
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
