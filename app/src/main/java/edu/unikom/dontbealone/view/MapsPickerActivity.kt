package edu.unikom.dontbealone.view

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import edu.unikom.dontbealone.R
import kotlinx.android.synthetic.main.activity_maps_picker.*
import java.util.*

class MapsPickerActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    companion object {
        val LAT_KEY = "MAPS_PICKER_LATITUDE"
        val LNG_KEY = "MAPS_PICKER_LONGITUDE"
        val ADDRESS_KEY = "ADDRESS_KEY"
        val MAPS_PICKER_REQUEST = 6969
    }

    private val isGpsChecked = false
    private var mMap: GoogleMap? = null
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var type: String? = null
    internal var cameraChangeListener: GoogleMap.OnCameraChangeListener =
        GoogleMap.OnCameraChangeListener { cameraPosition ->
            val pos = cameraPosition.target
            latitude = pos.latitude
            longitude = pos.longitude
        }
    private val alamat: String? = null
    private var place: Place? = null
    lateinit var mapSearchFragment: AutocompleteSupportFragment
    lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_picker)
        val b = intent.extras
        if (b != null) {
            if (b.containsKey("type"))
                type = b.getString("type")
        }
        checkPermission()
        Places.initialize(applicationContext, "AIzaSyBYve-A_4IEAYQpifbA84qrRJHZqqIr61s")
        placesClient = Places.createClient(this)
        map.onCreate(savedInstanceState)
        map.onResume()
        MapsInitializer.initialize(this)
        map.getMapAsync(this)
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        createLocationRequest()
        googleApiClient!!.connect()
        checkGPS()
        bBatalMap.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
        bPilihMap.setOnClickListener {
            if (latitude != 0.0 && longitude != 0.0) {
                val returnIntent = Intent()
                returnIntent.putExtra(LAT_KEY, place?.latLng?.latitude)
                returnIntent.putExtra(LNG_KEY, place?.latLng?.longitude)
                returnIntent.putExtra(ADDRESS_KEY, place?.name + ";" + place?.address)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
        mapSearchFragment = supportFragmentManager.findFragmentById(mapSearch.id) as AutocompleteSupportFragment
        val mapSearchButton =
            (mapSearchFragment.view?.findViewById(R.id.places_autocomplete_search_button) as ImageView)
        val mapClearButton =
            (mapSearchFragment.view?.findViewById(R.id.places_autocomplete_clear_button) as ImageView)
        val mapSearchEditText =
            (mapSearchFragment.view?.findViewById(R.id.places_autocomplete_search_input) as EditText)
        mapSearchFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        mapSearchFragment.setHint("Search a place")
        mapSearchFragment.setCountry("ID")
        mapSearchFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                setCurrentPlace(p0, true)
            }

            override fun onError(p0: Status) {
            }
        })

        mapSearchFragment.a.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.autocomplete_text_size)
        )
        mapSearchFragment.a.typeface = ResourcesCompat.getFont(this, R.font.nunito_regular)

        mapSearchEditText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.autocomplete_text_size)
        )
        mapSearchEditText.typeface = ResourcesCompat.getFont(this, R.font.nunito_regular)
        mapSearchButton.layoutParams.width = resources.getDimension(R.dimen.autocomplete_button_size).toInt()
        mapSearchButton.layoutParams.height = resources.getDimension(R.dimen.autocomplete_button_size).toInt()
        mapClearButton.layoutParams.width = resources.getDimension(R.dimen.autocomplete_button_size).toInt()
        mapClearButton.layoutParams.height = resources.getDimension(R.dimen.autocomplete_button_size).toInt()
        val marginX = resources.getDimension(R.dimen.autocomplete_button_margin_x_size).toInt()
        val marginY = resources.getDimension(R.dimen.autocomplete_button_margin_y_size).toInt()
        (mapSearchButton.layoutParams as LinearLayout.LayoutParams).setMargins(marginX, marginY, -marginX, marginY)
        (mapClearButton.layoutParams as LinearLayout.LayoutParams).setMargins(-marginX, marginY, marginX, marginY)
        mapSearchButton.requestLayout()
    }

    fun setCurrentPlace(p: Place, moveCamera: Boolean) {
        place = p
        tPosisi.text = place!!.latLng?.latitude.toString() + "," + place!!.latLng?.longitude.toString()
        tNamaTempat.text = place!!.name
        tAlamatTempat.text = place!!.address
        tPosisi.visibility = View.VISIBLE
        tAlamatTempat.visibility = View.VISIBLE
        mapSearchFragment.setText(place!!.name)
        val pos = place!!.latLng
        if (moveCamera)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 16f))
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 124
            )
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = AlarmManager.INTERVAL_HOUR
        locationRequest!!.fastestInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.setOnCameraChangeListener(cameraChangeListener)
        if (latitude != null) {
            val pos = LatLng(latitude!!, longitude!!)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 16f))
            //            binding.tPosisi.setText(latitude + "," + longitude);
            //            binding.tAlamat.setText(alamat);
        }
        mMap!!.setOnPoiClickListener {
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(it.latLng, 16f))
            placesClient.fetchPlace(
                FetchPlaceRequest.builder(
                    it.placeId,
                    Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                ).build()
            ).addOnSuccessListener {
                setCurrentPlace(it.place, false)
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (isGpsChecked) {
            checkGPS()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            if (googleApiClient!!.isConnected)
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location) {
        if (latitude == null && longitude == null) {
            latitude = location.latitude
            longitude = location.longitude
            val myLoc = LatLng(latitude!!, longitude!!)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16f))
        }
    }

    internal fun checkGPS() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                        status.startResolutionForResult(this@MapsPickerActivity, 1000)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
