package edu.unikom.dontbealone.util

import android.content.Context
import com.google.gson.Gson
import edu.unikom.dontbealone.model.DataUser

/**
 * Created by Syauqi Ilham on 6/28/2019.
 */
class Helpers {

    companion object {
        fun setCurrentuser(ctx: Context, user: DataUser?) {
            val sharedPreferences =
                ctx.getSharedPreferences("edu.unikom.dontbealone.SHARED_PREFERENCES", Context.MODE_PRIVATE)
            if (user != null)
                sharedPreferences.edit().putString("current_user", Gson().toJson(user)).apply()
            else
                sharedPreferences.edit().remove("current_user").apply()
        }

        fun getCurrentUser(ctx: Context): DataUser {
            val sharedPreferences =
                ctx.getSharedPreferences("edu.unikom.dontbealone.SHARED_PREFERENCES", Context.MODE_PRIVATE)
            val user = Gson().fromJson(sharedPreferences.getString("current_user", null), DataUser::class.java)
            return user
        }
    }
}