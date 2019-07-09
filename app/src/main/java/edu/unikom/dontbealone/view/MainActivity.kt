package edu.unikom.dontbealone.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import edu.unikom.dontbealone.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    lateinit var menu: MenuDialogFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        bLogout.setOnClickListener {
//            Helpers.setCurrentuser(this, null)
//            startActivity<LoginActivity>()
//            finish()
//        }
        menu = MenuDialogFragment.newInstance({
            when (it) {
                R.id.bMenuProfile -> goToProfile()
                R.id.bMenuHome -> goToHome()
                R.id.bMenuFriend -> goToFriend()
                R.id.bMenuMyAct -> goToMyAct()
                R.id.bMenuMessage -> goToMessage()
            }
        })
        bBottomNavMenu.setOnClickListener {
            menu.show(
                supportFragmentManager,
                "menu_dialog_fragment"
            )
        }
        bBottomNavNotif.setOnClickListener {
            startActivity<NotificationActivity>()
        }
        bNewActivity.setOnClickListener {
            if (menu.selectedMenuId == R.id.bMenuHome) {
                startActivity<NearbyListActivity>()
//                val input = SearchActivityDialogFragment.newInstance({
//
//                })
//                input.show(
//                    supportFragmentManager,
//                    "search_activity_dialog_fragment"
//                )
            } else if (menu.selectedMenuId == R.id.bMenuMyAct) {
                val input = InputDialogFragment.newInstance({

                })
                input.show(
                    supportFragmentManager,
                    "input_dialog_fragment"
                )
            }
        }
        goToHome()
    }

    fun goToProfile() {
        supportFragmentManager.beginTransaction().replace(mainFrame.id, ProfileFragment()).commit()
    }

    fun goToHome() {
        supportFragmentManager.beginTransaction().replace(mainFrame.id, HomeFragment()).commit()
        bNewActivity.text = "Find Activity"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_search),
            null,
            null,
            null
        )
    }

    fun goToFriend() {
        supportFragmentManager.beginTransaction().replace(mainFrame.id, FriendFragment()).commit()
        bNewActivity.text = "Add Friend"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_add_friend),
            null,
            null,
            null
        )
    }

    fun goToMyAct() {
        supportFragmentManager.beginTransaction().replace(mainFrame.id, ListActivityFragment()).commit()
        bNewActivity.text = "Create Activity"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_plus),
            null,
            null,
            null
        )
    }

    fun goToMessage() {
        supportFragmentManager.beginTransaction().replace(mainFrame.id, MessageFragment()).commit()
        bNewActivity.text = "New Message"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_new_message),
            null,
            null,
            null
        )
    }
}
