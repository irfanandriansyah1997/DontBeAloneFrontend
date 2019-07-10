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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_activity)
        showLoading()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, ListActivityFragment.newInstance({
            loadingFinished = it
            if (waitingFinished)
                hideLoading()
        })).commit()
        Timer("LoadingNearby", false).schedule(1500) {
            waitingFinished = true
            if (loadingFinished)
                hideLoading()
        }
        bBottomNavBack.setOnClickListener { finish() }
    }

    fun showLoading() {
        runOnUiThread {
            tLoading.visibility = View.VISIBLE
            imageLoading.visibility = View.VISIBLE
            Glide.with(this).asGif().load(R.raw.nearby_loading).into(imageLoading)
        }
    }

    fun hideLoading() {
        runOnUiThread {
            tLoading.visibility = View.GONE
            imageLoading.visibility = View.GONE
        }
    }
}
