package edu.unikom.dontbealone.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListActivityUserAdapter
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.model.DataActivityUser
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_nearby_activity.bBottomNavBack
import org.jetbrains.anko.toast
import java.text.NumberFormat
import java.util.*


class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    var data: DataActivity? = null
    var userRole: DataActivityUser? = null
    private var map: GoogleMap? = null
    private var listActiviyUser = mutableListOf<DataUser>()
    private lateinit var listActUserAdapter: ListActivityUserAdapter

    private val webServices by lazy {
        WebServices.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val id = intent?.extras?.getString("id_activity")
        bBottomNavBack.setOnClickListener { finish() }
        bBottomNavMenu.setOnClickListener {
            val popup = PopupMenu(this@DetailActivity, it)
            popup.menuInflater.inflate(R.menu.menu_detail, popup.menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

                override fun onMenuItemClick(item: MenuItem): Boolean {
                    if (item.itemId == R.id.menu_edit) {
                        val input = InputDialogFragment.newInstance(data, {
                            loadDetail(id)
                        })
                        input.show(
                            supportFragmentManager,
                            "input_dialog_fragment"
                        )
                    } else if (item.itemId == R.id.menu_cancel) {
                        if (userRole != null && userRole!!.level.equals("admin")) {
                            val builder = AlertDialog.Builder(it.context)
                            builder.setMessage("Are you sure you want to delete this activity?")
                                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        deleteActivity(id, dialog)
                                    }
                                }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        dialog?.cancel()
                                    }

                                }).show()
                        }
                    }
                    return true
                }
            })
            popup.menu.findItem(R.id.menu_edit).isVisible = userRole != null && userRole!!.level.equals("admin")
//            popup.menu.findItem(R.id.menu_request).isVisible = userRole != null && userRole!!.level.equals("admin")
            popup.menu.findItem(R.id.menu_cancel).title =
                if (userRole != null && userRole!!.level.equals("admin")) "Delete Activity" else "Cancel Activity"
            popup.menu.findItem(R.id.menu_message).isVisible = userRole != null && userRole!!.status.equals("Accepted")
            popup.show()
        }
        bJoinActiviy.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setMessage("Are you sure you want to join this activity?")
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        joinActivity(id, dialog)
                    }
                }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.cancel()
                    }

                }).show()
        }
        listActUserAdapter = ListActivityUserAdapter(listActiviyUser, {
        }, { s: String, i: Int ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to " + (if (i == 1) "accept" else "reject") + " " + s + " to join this activity?")
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        grantActivity(id, s, i, dialog)
                    }
                }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.cancel()
                    }

                }).show()
        })
        listActUser.adapter = listActUserAdapter
        listActUser.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listActUser.isNestedScrollingEnabled = false
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapActivity) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        loadDetail(id)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.uiSettings?.isMapToolbarEnabled = false
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                6969
            )
        else
            map?.isMyLocationEnabled = true
    }

    fun moveMap() {
        if (data != null) {
            val latlng = LatLng(data!!.lat, data!!.lng)
            map?.addMarker(MarkerOptions().position(latlng))
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 6969) {
            var granted = true
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    granted = false
                    break
                }
            }
            if (granted)
                map?.setMyLocationEnabled(true)
        }
    }

    fun grantActivity(id: String?, username: String, status: Int, dialog: DialogInterface?) {
        if (id != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage(if (status == 1) "Accepting" else "Rejecting" + " user...")
            progressDialog.show()
            progressDialog.setCancelable(false)
            webServices.grantActivity(id, username, status).subscribeOn(
                Schedulers.io()
            ).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeBy(
                onNext = {
                    if (it.success) {
                        loadDetail(id)
                        dialog?.cancel()
                    }
                    toast(it.message)
                    progressDialog.cancel()
                },
                onError = {
                    it.printStackTrace()
                    toast("An error has occured, please contact the administrator").show()
                },
                onComplete = { }
            )
        }
    }

    fun joinActivity(id: String?, dialog: DialogInterface?) {
        if (id != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Joining activity...")
            progressDialog.show()
            progressDialog.setCancelable(false)
            webServices.joinActivity(id, Helpers.getCurrentUser(this).username).subscribeOn(
                Schedulers.io()
            ).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeBy(
                onNext = {
                    if (it.success) {
                        loadDetail(id)
                        dialog?.cancel()
                    }
                    toast(it.message)
                    progressDialog.cancel()
                },
                onError = {
                    it.printStackTrace()
                    toast("An error has occured, please contact the administrator").show()
                },
                onComplete = { }
            )
        }
    }

    fun deleteActivity(id: String?, dialog: DialogInterface?) {
        if (id != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Deleting activity...")
            progressDialog.show()
            progressDialog.setCancelable(false)
            webServices.cancelActivity(id).subscribeOn(
                Schedulers.io()
            ).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeBy(
                onNext = {
                    if (it.success) {
                        finish()
                        dialog?.cancel()
                    }
                    toast(it.message)
                    progressDialog.cancel()
                },
                onError = {
                    it.printStackTrace()
                    toast("An error has occured, please contact the administrator").show()
                },
                onComplete = { }
            )
        }
    }

    fun loadUserRole(id: String?) {
        if (id != null) {
            webServices.getUserRoleActivity(id, Helpers.getCurrentUser(this).username).subscribeOn(
                Schedulers.io()
            ).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeBy(
                onNext = {
                    showUserRole(it.data)
                },
                onError = {
                    it.printStackTrace()
                    toast("An error has occured, please contact the administrator").show()
                },
                onComplete = { }
            )
        }
    }

    fun loadDetail(id: String?) {
        if (id != null) {
            loadUserRole(id)
            webServices.getDetailActivity(id).subscribeOn(
                Schedulers.io()
            ).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeBy(
                onNext = {
                    if (it.success && it.data != null) {
                        showDetail(it.data!![0])
                    } else {
                        toast(it.message).show()
                    }
                },
                onError = {
                    it.printStackTrace()
                    toast("An error has occured, please contact the administrator").show()
                },
                onComplete = { }
            )
        }
    }

    fun showDetail(data: DataActivity) {
        this.data = data
        moveMap()
        tActivityName.text = data.name
        tActivityAddress.text = data.address.replace(";", ", ")
        tActivityDesc.text = data.desc
        tActivityType.text = data.type.name
        tActivityTime.text = data.time
        val price = data.price.toLong()
        tActivityPrice.text =
            if (price > 0) "Rp " + NumberFormat.getInstance(Locale("ID")).format(price) else "FREE"
        GlideApp.with(this).load(data.type.icon).into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                val dsize = resources.getDimension(R.dimen.activity_type_icon_small_size).toInt()
                val d = BitmapDrawable(
                    resources,
                    Bitmap.createScaledBitmap((resource as BitmapDrawable).bitmap, dsize, dsize, true)
                )
                tActivityType.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
            }
        })
        val isAdmin = Helpers.getCurrentUser(this).username.equals(data.member[0].username)
        listActiviyUser.clear()
        if (isAdmin)
            listActiviyUser.addAll(data.member.filter { s -> !s.status.equals("Rejected") })
        else
            listActiviyUser.addAll(data.member.filter { s -> s.status.equals("Accepted") })
        listActUserAdapter.isAdmin = isAdmin
        listActUserAdapter.notifyDataSetChanged()
    }

    fun showUserRole(data: DataActivityUser?) {
        userRole = data
        bJoinActiviy.visibility = if (data == null || data.status.equals("Pending")) View.VISIBLE else View.GONE
        bJoinActiviy.isClickable = data == null || !data.status.equals("Pending")
        bJoinActiviy.text =
            if (data != null && data.status.equals("Pending")) "Waiting Confirmation" else "Join Activity"
        bJoinActiviy.setCompoundDrawablesWithIntrinsicBounds(
            resources.getDrawable(
                if (data != null && data.status.equals(
                        "Pending"
                    )
                ) R.drawable.ic_clock else R.drawable.ic_check
            ), null, null, null
        )
        bBottomNavMenu.visibility = if (data != null) View.VISIBLE else View.GONE
    }

}
