package edu.unikom.dontbealone.util

import com.google.gson.GsonBuilder
import edu.unikom.dontbealone.BuildConfig
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.model.WebServiceResult
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface WebServices {

    @FormUrlEncoded
    @POST("api.php?p=is_username_exists")
    fun isUsernameExists(@Field("username") username: String): Observable<WebServiceResult<Boolean>>

    @FormUrlEncoded
    @POST("api.php?p=login")
    fun login(@Field("username") username: String, @Field("password") password: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("api.php?p=login_fb")
    fun loginFb(@Field("email") email: String, @Field("fb_id") fbId: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("api.php?p=login_tw")
    fun loginTw(@Field("email") email: String, @Field("tw_id") twId: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("api.php?p=login_gp")
    fun loginGp(@Field("email") email: String, @Field("gp_id") gpId: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("api.php?p=register")
    fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("fb_id") fbId: String,
        @Field("tw_id") twId: String,
        @Field("gp_id") gpId: String,
        @Field("photo") photo: String,
        @Field("password") password: String?
    ): Observable<WebServiceResult<DataUser>>

    companion object {
        lateinit var client: OkHttpClient

        fun create(): WebServices {
            client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .baseUrl(BuildConfig.BASE_URL)
                .baseUrl(BuildConfig.BASE_URL + "dontbealone/")
                .client(client)
                .build()
            return retrofit.create(WebServices::class.java)
        }
    }
}