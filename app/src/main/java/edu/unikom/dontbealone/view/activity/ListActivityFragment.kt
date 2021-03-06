package edu.unikom.dontbealone.view.activity

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListActivityAdapter
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.model.WebServiceResult
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_list_activity.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class ListActivityFragment : Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    override fun onConnected(p0: Bundle?) {
        try {
            if (context != null) {
                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(p0: Location?) {
        Log.d("gps_test", "masuk locationChanged " + p0)
        if (p0 != null) {
            lat = p0.latitude
            lng = p0.longitude
            val myLoc = LatLng(lat, lng)
            if (username == null)
                loadActivity()
        }
    }

    var keyword: String? = null
    var type: Int = -1
    private var username: String? = null
    private var listActivity = mutableListOf<DataActivity>()
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null
    private var listener: (loaded: Boolean) -> Unit = {}
    private lateinit var listActAdapter: ListActivityAdapter

    private val webServices by lazy {
        WebServices.create()
    }

    companion object {
        fun newInstance(): ListActivityFragment {
            return ListActivityFragment()
        }

        fun newInstance(keyword: String?, username: String): ListActivityFragment {
            val fragment = ListActivityFragment()
            fragment.keyword = keyword
            fragment.username = username
            return fragment
        }

        fun newInstance(loadListener: (loaded: Boolean) -> Unit): ListActivityFragment {
            val fragment = ListActivityFragment()
            fragment.listener = loadListener
            return fragment
        }

        fun newInstance(loadListener: (loaded: Boolean) -> Unit, type: Int): ListActivityFragment {
            val fragment = ListActivityFragment()
            fragment.listener = loadListener
            fragment.type = type
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var title = if (username != null) {
            if (Helpers.getCurrentUser(view.context).username.equals(username))
                "My Activity"
            else
                username + "'s Activity"
        } else "Nearby Activity"
        titleListAct.text = title
        listActAdapter = ListActivityAdapter(
            activity!!,
            listActivity,
            Helpers.getCurrentUser(view.context).username.equals(username),
            {
                startActivity<DetailActivity>("id_activity" to it.id)
            })
        listAct.adapter = listActAdapter
        listAct.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        listAct.isNestedScrollingEnabled = false
    }

    fun checkGPSPermission(context: Context, load: Boolean) {
        Log.d("gps_test", "masuk check permission")
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (load) {
                googleApiClient = GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
                createLocationRequest()
                googleApiClient!!.connect()
                checkGPS(context)
            }
        } else {
            if (load) {
                toast("Sorry, but we need your gps access to help you find nearby activities :(")
                activity?.finish()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 124
                )
            }
        }
    }

    fun checkGPS(context: Context) {
        Log.d("gps_test", "masuk check gps")
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)
            builder.setAlwaysShow(true)
            val result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status = result.status

                val state = result
                    .locationSettingsStates
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        status.startResolutionForResult(activity, 1000)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun createLocationRequest() {
        Log.d("gps_test", "masuk create request")
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = AlarmManager.INTERVAL_HOUR
        locationRequest!!.fastestInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 124 && context != null) {
            checkGPSPermission(context!!, true)
        }
    }

    override fun onResume() {
        super.onResume()
        if (context != null) {
            if (username == null) {
                Log.d("gps_test", "masuk")
                checkGPSPermission(context!!, false)
                googleApiClient = GoogleApiClient.Builder(context!!)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
                googleApiClient!!.connect()
                checkGPS(context!!)
                createLocationRequest()
            }
            if (listAct != null && username != null || (lat != 0.0 && lng != 0.0))
                loadActivity()
        }
    }

    override fun onStop() {
        super.onStop()
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            if (googleApiClient!!.isConnected)
                googleApiClient!!.disconnect()
        }
    }

    fun loadActivity() {
        Log.d("gps_test", "masuk load act")
        listAct.showShimmerAdapter()
        val call: Observable<WebServiceResult<List<DataActivity>>>
        if (username != null)
            call = webServices.getActivityByUser(username!!, -1)
        else
            call = webServices.getActivity(lat, lng, type, 50, keyword)
        call.subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    listActivity.clear()
                    listActivity.addAll(it.data!!)
                    listActAdapter.notifyDataSetChanged()
                    tActNoData.visibility = if (listActivity.size > 0) View.GONE else View.VISIBLE
                } else {
                    toast(it.message).show()
                }
                listener(true)
                listAct.hideShimmerAdapter()
            },
            onError = {
                listener(true)
                listAct.hideShimmerAdapter()
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }

}
