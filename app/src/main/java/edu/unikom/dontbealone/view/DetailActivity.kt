package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.util.Helpers
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_nearby_activity.bBottomNavBack


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
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
    }

}
