package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.util.Helpers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    lateinit var menu: MenuDialogFragment
    var currentFragment: Fragment? = null

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
                    (currentFragment as ListActivityFragment).loadActivity()
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
        menu.selectedMenuId = R.id.bMenuProfile
        currentFragment = ProfileFragment()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.GONE
        bNewActivity.text = "Edit Profile"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_new_message),
            null,
            null,
            null
        )
    }

    fun goToHome() {
        menu.selectedMenuId = R.id.bMenuHome
        currentFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "Find Activity"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_search),
            null,
            null,
            null
        )
    }

    fun goToFriend() {
        menu.selectedMenuId = R.id.bMenuFriend
        currentFragment = FriendFragment()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "Add Friend"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_add_friend),
            null,
            null,
            null
        )
    }

    fun goToMyAct() {
        menu.selectedMenuId = R.id.bMenuMyAct
        currentFragment = ListActivityFragment.newInstance(Helpers.getCurrentUser(this).username)
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "Create Activity"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_plus),
            null,
            null,
            null
        )
    }

    fun goToMessage() {
        menu.selectedMenuId = R.id.bMenuMessage
        currentFragment = MessageFragment()
        supportFragmentManager.beginTransaction().replace(mainFrame.id, currentFragment!!).commit()
        bNewActivity.visibility = View.VISIBLE
        bNewActivity.text = "New Message"
        bNewActivity.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_new_message),
            null,
            null,
            null
        )
    }
}
