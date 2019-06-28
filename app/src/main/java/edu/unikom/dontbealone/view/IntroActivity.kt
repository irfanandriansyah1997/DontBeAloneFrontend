package edu.unikom.dontbealone.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.IntroPagerAdapter
import edu.unikom.dontbealone.model.DataIntro
import kotlinx.android.synthetic.main.activity_intro.*
import org.jetbrains.anko.startActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class IntroActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        sharedPreferences = getSharedPreferences("edu.unikom.dontbealone.SHARED_PREFERENCES", Context.MODE_PRIVATE)
        val jsonReader = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.list_tutorial)))
        val list = Gson().fromJson<ArrayList<DataIntro>>(jsonReader, object : TypeToken<ArrayList<DataIntro>>() {}.type)
        val tutorialPagerAdapter = IntroPagerAdapter(supportFragmentManager, list)
        vpgTutorialViewPager.adapter = tutorialPagerAdapter
        bGetStarted.setOnClickListener(View.OnClickListener {
            sharedPreferences.edit().putBoolean("intro_seen", true).apply()
            startActivity<LoginActivity>()
            finish()
        })
    }
}
