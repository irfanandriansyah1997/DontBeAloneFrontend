package edu.unikom.dontbealone.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.unikom.dontbealone.R
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = getSharedPreferences("edu.unikom.dontbealone.SHARED_PREFERENCES", Context.MODE_PRIVATE)
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
