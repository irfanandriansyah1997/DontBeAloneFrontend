package edu.unikom.dontbealone.util

import com.google.gson.GsonBuilder
import edu.unikom.dontbealone.BuildConfig
import edu.unikom.dontbealone.model.*
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface WebServices {

    @GET("auth/is_username_exists")
    fun isUsernameExists(@Query("username") username: String): Observable<WebServiceResult<Boolean>>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(@Field("username") username: String, @Field("password") password: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("auth/login_fb")
    fun loginFb(@Field("email") email: String, @Field("fb_id") fbId: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("auth/login_tw")
    fun loginTw(@Field("email") email: String, @Field("tw_id") twId: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("auth/login_gp")
    fun loginGp(@Field("email") email: String, @Field("gp_id") gpId: String): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("auth/register")
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

    @FormUrlEncoded
    @POST("user/update")
    fun updateUser(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("phone_number") photo: String,
        @Field("address") password: String,
        @Field("bio") bio: String
    ): Observable<WebServiceResult<DataUser>>

    @FormUrlEncoded
    @POST("user/update_passsword")
    fun updateUser(@Field("username") username: String, @Field("password") password: String): Observable<WebServiceResult<DataUser>>

    @GET("user/get_user_by_username/{username}")
    fun getUser(@Path("username") username: String): Observable<WebServiceResult<DataUser>>

    @GET("user/search")
    fun searchUser(@Query("keyword") keyword: String): Observable<WebServiceResult<List<DataUser>>>

    @GET("activity/get_activity")
    fun getActivity(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("type") type: Int,
        @Query("distance") radius: Int,
        @Query("keyword") keyword: String?
    ): Observable<WebServiceResult<List<DataActivity>>>

    @GET("activity/get_activity_by_user")
    fun getActivityByUser(@Query("username") username: String, @Query("limit") limit: Int): Observable<WebServiceResult<List<DataActivity>>>

    @GET("activity/get_activity_by_id/{id}")
    fun getDetailActivity(@Path("id") id: String): Observable<WebServiceResult<List<DataActivity>>>

    @GET("activity/get_activity_type")
    fun getActivityType(): Observable<WebServiceResult<List<DataActivityType>>>

    @GET("activity/get_activity_type_trending")
    fun getActivityTypeTrending(): Observable<WebServiceResult<List<DataActivityType>>>

    @GET("activity/get_user_role/{id}/{username}")
    fun getUserRoleActivity(@Path("id") id: String, @Path("username") username: String): Observable<WebServiceResult<DataActivityUser>>

    @FormUrlEncoded
    @POST("activity/insert")
    fun insertActivity(
        @Field("activity_name") name: String,
        @Field("activity_type") type: Int,
        @Field("price") price: String,
        @Field("description") desc: String,
        @Field("lat") lat: Double,
        @Field("lng") lng: Double,
        @Field("address") address: String,
        @Field("username") username: String
    ): Observable<WebServiceResultPost>

    @FormUrlEncoded
    @POST("activity/update/{id}")
    fun updateActivity(
        @Path("id") id: String,
        @Field("activity_name") name: String,
        @Field("activity_type") type: Int,
        @Field("price") price: String,
        @Field("description") desc: String,
        @Field("lat") lat: Double,
        @Field("lng") lng: Double,
        @Field("address") address: String,
        @Field("username") username: String
    ): Observable<WebServiceResultPost>

    @POST("activity/banned/{id}")
    fun deleteActivity(@Path("id") id: String): Observable<WebServiceResult<DataActivity>>

    @FormUrlEncoded
    @POST("activity/leave_activity")
    fun leaveActivity(@Field("id_activity") id: String, @Field("username") username: String): Observable<WebServiceResult<DataActivity>>

    @FormUrlEncoded
    @POST("activity/join_activity")
    fun joinActivity(@Field("id_activity") id: String, @Field("username") username: String): Observable<WebServiceResult<DataActivity>>

    @FormUrlEncoded
    @POST("activity/grant_activity")
    fun grantActivity(@Field("id_activity") id: String, @Field("username") username: String, @Field("status") status: Int): Observable<WebServiceResult<DataActivity>>

    @FormUrlEncoded
    @POST("chat")
    fun sendChat(@Field("activity_id") id: String, @Field("username") username: String, @Field("message") message: String): Observable<WebServiceResult<DataChat>>

    @GET("room")
    fun getChatRoom(@Query("username") username: String): Observable<WebServiceResult<List<DataChatRoom>>>

    @GET("friend/get_friend")
    fun getFriend(@Query("username") username: String): Observable<WebServiceResult<List<DataFriend>>>

    @FormUrlEncoded
    @POST("friend/add_friend")
    fun addFriend(@Field("sender") sender: String, @Field("receiver") receiver: String): Observable<WebServiceResult<DataFriend>>

    @FormUrlEncoded
    @POST("friend/confirm_friend")
    fun confirmFriend(@Field("sender") sender: String, @Field("receiver") receiver: String): Observable<WebServiceResult<DataFriend>>

    @FormUrlEncoded
    @POST("friend/reject_friend")
    fun rejectFriend(@Field("sender") sender: String, @Field("receiver") receiver: String): Observable<WebServiceResult<DataFriend>>

    @FormUrlEncoded
    @POST("friend/get_friend_status")
    fun getFriendStatus(@Field("sender") sender: String, @Field("receiver") receiver: String): Observable<WebServiceResult<DataFriend>>

    companion object {
        lateinit var client: OkHttpClient
        lateinit var clientChat: OkHttpClient

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
                .baseUrl(BuildConfig.BASE_URL)
//                .baseUrl(BuildConfig.BASE_URL + "dontbealone/") /* local */
                .client(client)
                .build()
            return retrofit.create(WebServices::class.java)
        }

        fun createChat(): WebServices {
            clientChat = OkHttpClient.Builder()
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
                .baseUrl(BuildConfig.BASE_URL_CHAT)
//                .baseUrl(BuildConfig.BASE_URL + "dontbealone/") /* local */
                .client(clientChat)
                .build()
            return retrofit.create(WebServices::class.java)
        }
    }
}