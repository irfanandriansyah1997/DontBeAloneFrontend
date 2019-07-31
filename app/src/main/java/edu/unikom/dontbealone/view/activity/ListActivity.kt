package edu.unikom.dontbealone.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.unikom.dontbealone.R
import kotlinx.android.synthetic.main.activity_nearby_activity.*

class ListActivity : AppCompatActivity() {

    lateinit var listFragment: ListActivityFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_activity)
        var user = intent?.extras?.getString("user")
        listFragment =
            if (user != null) ListActivityFragment.newInstance(null, user) else ListActivityFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, listFragment).commit()
        bBottomNavBack.setOnClickListener { finish() }
        bApplyFilter.setOnClickListener {
            val input = FilterActivityDialogFragment.newInstance(listFragment.keyword, null, { keyword, type ->
                listFragment.keyword = keyword
                listFragment.loadActivity()
            })
            input.show(
                supportFragmentManager,
                "input_dialog_fragment"
            )
        }
    }

}
