package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListActivityAdapter
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.model.DataActivityType
import edu.unikom.dontbealone.util.Helpers
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity

class HomeFragment : Fragment() {

    var listMyActivity = mutableListOf<DataActivity>()
    lateinit var listMyActAdapter: ListActivityAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listMyActAdapter = ListActivityAdapter(listMyActivity, {
            startActivity<DetailActivity>()
        })
        listMyAct.adapter = listMyActAdapter
        listMyAct.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        listMyAct.isNestedScrollingEnabled = false
        listMyActivity.add(
            DataActivity(
                "Futsal Friendly Match",
                "",
                DataActivityType("Futsal", "ic_futsal"),
                0.0,
                0.0,
                "Moritz Futsal Cimahi",
                "13:35",
                "Rp 25.000",
                Helpers.getCurrentUser(view.context)
            )
        )
        listMyActivity.add(
            DataActivity(
                "Naik Gunung Gede",
                "",
                DataActivityType("Hiking", "ic_gunung"),
                0.0,
                0.0,
                "Gunung Gede Pangrango",
                "21-07-2019 14:35",
                "Rp 25.000",
                Helpers.getCurrentUser(view.context)
            )
        )
        listMyActivity.add(
            DataActivity(
                "Nobar Avenger Endgame",
                "",
                DataActivityType("Movies", "ic_nonton"),
                0.0,
                0.0,
                "Empire XXI, Bandung Indah Plaza",
                "17:20",
                "Rp 45.000",
                Helpers.getCurrentUser(view.context)
            )
        )
        listMyActAdapter.notifyDataSetChanged()
    }

}
