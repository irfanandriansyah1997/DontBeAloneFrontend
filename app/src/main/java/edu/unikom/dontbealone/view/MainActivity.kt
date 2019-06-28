package edu.unikom.dontbealone.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataUser
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("edu.unikom.dontbealone.SHARED_PREFERENCES", Context.MODE_PRIVATE)
        val user = Gson().fromJson(sharedPreferences.getString("current_user", null), DataUser::class.java)
        txtHomeName.text = if (!user.name.equals("")) user.name else user.username
        txtHomeEmail.text = user.email
        Glide.with(this).load(user.photo).placeholder(R.drawable.ic_bg_img_default).into(imgHomeProfile)
        bLogout.setOnClickListener {
            sharedPreferences.edit().remove("current_user").apply()
            startActivity<LoginActivity>()
            finish()
        }
    }
}
