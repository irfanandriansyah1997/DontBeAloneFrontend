package edu.unikom.dontbealone.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.view.home.MainActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = getSharedPreferences("edu.unikom.dontbealone.SHARED_PREFERENCES", Context.MODE_PRIVATE)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseMessaging.getInstance().subscribeToTopic("database-5789423675").addOnSuccessListener {
            Log.d("chat_test", "success subscribe topic database-5789423675");
        }.addOnFailureListener {
            it.printStackTrace()
        }
        Timer("SplashScreen", false).schedule(1000) {
            var i : Intent
            if (sharedPreferences.getBoolean("intro_seen", false)) {
                if (sharedPreferences.getString("current_user", null) != null)
                    i = Intent(this@SplashScreenActivity, MainActivity::class.java)
                else
                    i = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            } else
                i = Intent(this@SplashScreenActivity, IntroActivity::class.java)
            startActivity(i)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
