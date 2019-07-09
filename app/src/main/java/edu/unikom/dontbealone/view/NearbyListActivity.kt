package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import kotlinx.android.synthetic.main.activity_nearby_activity.*

class NearbyListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_activity)
        showLoading()
    }

    fun showLoading() {
        mainContainer.visibility = View.GONE
        tLoading.visibility = View.VISIBLE
        imageLoading.visibility = View.VISIBLE
        Glide.with(this).asGif().load(R.raw.nearby_loading).into(imageLoading)
    }

    fun hideLoading() {
        mainContainer.visibility = View.VISIBLE
        tLoading.visibility = View.GONE
        imageLoading.visibility = View.GONE
    }
}
