package edu.unikom.dontbealone.view.profile

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.view.activity.DetailActivity
import kotlinx.android.synthetic.main.activity_friend_picker.*

class FriendPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_picker)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, FriendFragment.newInstance("Invite Friend",false, {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to invite " + (if (it.name != null && !it.name.equals("")) it.name else it.username) + " to this activity?")
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val i = Intent(this@FriendPickerActivity, DetailActivity::class.java)
                        i.putExtra("user", Gson().toJson(it))
                        setResult(Activity.RESULT_OK, i)
                        finish()
                    }
                }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.cancel()
                    }

                }).show()
        })).commit()
        bBottomNavBack.setOnClickListener { finish() }
    }

}
