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
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_maps_activity.*
import kotlinx.android.synthetic.main.item_activity.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.text.NumberFormat
import java.util.*

class MapsActivityFragment : Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(marker: Marker?): Boolean {
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(marker?.position, 12f))
        showItemDetail(marker?.tag.toString())
        return true
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
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
            mMap!!.isMyLocationEnabled = true
            if (lat != 0.0 && lng != 0.0) {
                val pos = LatLng(lat, lng)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12f))
            }

            mMap!!.setOnMarkerClickListener(this)
            mMap!!.setOnMapClickListener {
                hideItemDetail()
            }
        } else
            return
    }

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
            if (mMap != null)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 12f))
            loadActivity()
        }
    }

    var keyword: String? = null
    var type: Int = -1
    private var listActivity = mutableListOf<DataActivity>()
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var mMap: GoogleMap? = null
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null
    private var listener: (loaded: Boolean) -> Unit = {}

    private val webServices by lazy {
        WebServices.create()
    }

    companion object {
        fun newInstance(): MapsActivityFragment {
            return MapsActivityFragment()
        }

        fun newInstance(keyword: String): MapsActivityFragment {
            val fragment = MapsActivityFragment()
            fragment.keyword = keyword
            return fragment
        }

        fun newInstance(loadListener: (loaded: Boolean) -> Unit): MapsActivityFragment {
            val fragment = MapsActivityFragment()
            fragment.listener = loadListener
            return fragment
        }

        fun newInstance(loadListener: (loaded: Boolean) -> Unit, type: Int): MapsActivityFragment {
            val fragment = MapsActivityFragment()
            fragment.listener = loadListener
            fragment.type = type
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map.onCreate(savedInstanceState)
        map.onResume()
        MapsInitializer.initialize(view.context)
        map.getMapAsync(this)
    }

    fun showItemDetail(id: String?) {
        if (id != null && context != null) {
            val item = listActivity.filter { i -> i.id.equals(id) }.first()
            GlideApp.with(context!!).load(item.type.icon).into(activityTypeImage)
            tActivityName.text = item.name
            tActivityAddress.text = item.address.replace(";", ", ").split(",")[0]
            tActivityTime.text = item.time.substring(0, item.time.length - 3)
            val price = item.price.toLong()
            tActivityPrice.text =
                if (price > 0) "Rp " + NumberFormat.getInstance(Locale("ID")).format(price) else "FREE"
            tActivityDistance.text = String.format("%.1f", item.distance) + "km"
            tActivityDistance.visibility = if (item.distance != null) View.VISIBLE else View.GONE
            tActivityUserName.visibility = if (item.user != null) View.VISIBLE else View.GONE
            tActivityUserPhoto.visibility = if (item.user != null) View.VISIBLE else View.GONE
            tActivityUserName.text =
                if (item.user?.name != null && !item.user?.name.equals("")) item.user?.name!!.split(" ")[0] else item.user?.username
            Glide.with(context!!).load(item.user?.photo).error(R.drawable.ic_user).into(tActivityUserPhoto)
            activityItemDivider.visibility = View.GONE
            cardItemActivity.visibility = View.VISIBLE
            activityItemContainer.setOnClickListener {
                startActivity<DetailActivity>("id_activity" to item.id)
            }
        }
    }

    fun hideItemDetail() {
        cardItemActivity.visibility = View.GONE
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
            if (lat != 0.0 && lng != 0.0)
                loadActivity()
        }
    }

    override fun onStop() {
        super.onStop()
        map?.onStop()
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            if (googleApiClient!!.isConnected)
                googleApiClient!!.disconnect()
        }
    }

    override fun onDestroyView() {
        mMap?.clear()
        map?.onDestroy()
        super.onDestroyView()
    }

    fun loadActivity() {
        Log.d("gps_test", "masuk load act")
        cardItemActivity.visibility = View.GONE
        webServices.getActivity(lat, lng, type, 50, keyword).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    if (mMap != null) {
                        mMap!!.clear()
                        listActivity.clear()
                        listActivity.addAll(it.data!!)
                        for (item in listActivity) {
                            mMap!!.addMarker(MarkerOptions().position(LatLng(item.lat, item.lng)).title(item.name))
                                .tag = item.id
                        }
                    }
                } else {
                    toast(it.message).show()
                }
                listener(true)
            },
            onError = {
                listener(true)
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }

}
