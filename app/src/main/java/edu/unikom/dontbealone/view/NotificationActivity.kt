package edu.unikom.dontbealone.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListNotifAdapter
import edu.unikom.dontbealone.model.DataNotif
import edu.unikom.dontbealone.util.Helpers
import kotlinx.android.synthetic.main.activity_nearby_activity.bBottomNavBack
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    var listNotification = mutableListOf<DataNotif>()
    lateinit var listNotifAdapter: ListNotifAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        bBottomNavBack.setOnClickListener { finish() }
        listNotifAdapter = ListNotifAdapter(listNotification, {

        })
        listNotif.adapter = listNotifAdapter
        listNotif.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listNotif.isNestedScrollingEnabled = false
        listNotif.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        listNotification.add(
            DataNotif(
                "<b>Syauqi Ilham</b> requested to join <b>Futsal Friendly Match</b>",
                Helpers.getCurrentUser(this).photo,
                "12h"
            )
        )
        listNotification.add(
            DataNotif(
                "<b>Syauqi Ilham</b> canceled a request to join <b>Futsal Friendly Match</b>",
                Helpers.getCurrentUser(this).photo,
                "12h"
            )
        )
        listNotification.add(
            DataNotif(
                "<b>Futsal Friendly Match</b> will be started in 2h",
                Helpers.getCurrentUser(this).photo,
                "12h"
            )
        )
    }

}
