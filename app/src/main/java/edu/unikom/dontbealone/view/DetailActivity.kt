package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_nearby_activity.bBottomNavBack
import org.jetbrains.anko.toast


class DetailActivity : AppCompatActivity() {

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val id = intent?.extras?.getString("id_activity")
        bBottomNavBack.setOnClickListener { finish() }
        bBottomNavMenu.setOnClickListener {
            val popup = PopupMenu(this@DetailActivity, it)
            popup.menuInflater.inflate(R.menu.menu_detail, popup.menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    Toast.makeText(this@DetailActivity, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    return true
                }
            })
            popup.show()
        }
        Glide.with(this).load(Helpers.getCurrentUser(this).photo).into(detailUserImage)
        loadDetail(id)
    }

    fun loadDetail(id: String?) {
        if (id != null) {
            webServices.getDetailActivity(id).subscribeOn(
                Schedulers.io()
            ).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeBy(
                onNext = {
                    if (it.success && it.data != null) {
                        showDetail(it.data!![0])
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

    fun showDetail(data: DataActivity) {
        tActivityName.text = data.name
        tActivityAddress.text = data.address.replace(";", ", ")
        tActivityDesc.text = data.desc
        tActivityType.text = data.type.name
        tActivityTime.text = data.time
        tActivityPrice.text = "Rp " + data.price
    }

}
