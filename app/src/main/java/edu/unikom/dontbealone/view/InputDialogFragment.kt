package edu.unikom.dontbealone.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_input.*
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Syauqi Ilham on 7/8/2019.
 */
class InputDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: () -> Unit): InputDialogFragment {
            var fragment = InputDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }

    private val webServices by lazy {
        WebServices.create()
    }

    lateinit var listener: () -> Unit

    var type: Int = 0
    var lat: Double = 0.0
    var lng: Double = 0.0
    var address: String = ""
    var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(
            R.layout.fragment_input, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog?
            var bottomSheet = dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from<View>(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.isHideable = false

            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
            })
        }
        bInputType.setOnClickListener { startActivityForResult<TypePickerActivity>(TypePickerActivity.TYPE_PICKER_REQUEST) }
        bInputMap.setOnClickListener { startActivityForResult<MapsPickerActivity>(MapsPickerActivity.MAPS_PICKER_REQUEST) }
        bInputTime.setOnClickListener { getDate() }
        inActPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inActPrice.hint = if (s.toString().trim().length > 0) "" else "How much will it cost?"
            }

        })
        bInputCancel.setOnClickListener {
            listener()
            dismiss()
        }
        bInputSave.setOnClickListener {
            if (inActName.text.toString().trim().length > 0 &&
                inActDesc.text.toString().trim().length > 0 &&
                inActPrice.text.toString().trim().length > 0 &&
                type != 0 && lat != 0.0 && lng != 0.0 && address.trim().length > 0
            ) {
                showLoading()
                webServices.insertActivity(
                    inActName.text.toString().trim(),
                    type,
                    inActPrice.text.toString().trim(),
                    inActDesc.text.toString().trim(),
                    lat,
                    lng,
                    address,
                    Helpers.getCurrentUser(it.context).username
                ).subscribeOn(
                    Schedulers.io()
                ).observeOn(
                    AndroidSchedulers.mainThread()
                ).subscribeBy(
                    onNext = {
                        if (it.success) {
                            listener()
                            dismiss()
                        }
                        toast(it.message).show()
                        hideLoading()
                    },
                    onError = {
                        it.printStackTrace()
                        toast("An error has occured, please contact the administrator").show()
                        hideLoading()
                    },
                    onComplete = { }
                )
            } else
                toast("Some information required are missing, please complete the form")
        }
    }

    private fun showLoading() {
        vLoadingBg.visibility = View.VISIBLE
        vLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vLoadingBg.visibility = View.GONE
        vLoading.visibility = View.GONE
    }

    fun getDate() {
        val c = Calendar.getInstance()
        c.time = Date()
        if (!time.equals("")) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                c.time = sdf.parse(time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        val day = c.get(Calendar.DATE)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)
        val dialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val cc = Calendar.getInstance()
                cc.set(year, month, dayOfMonth)
                getTime(SimpleDateFormat("yyyy-MM-dd").format(cc.time))
            }, year, month, day
        )
        dialog.setCancelable(false)
        dialog.show()
    }

    fun getTime(tgl: String) {
        val c = Calendar.getInstance()
        c.time = Date()
        if (!time.equals("")) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                c.time = sdf.parse(time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timePickerDialog: TimePickerDialog
        timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                time = tgl + " " + String.format("%02d:%02d", selectedHour, selectedMinute)
                bInputTime.text = time
            }, hour, minute, true
        )
        timePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MapsPickerActivity.MAPS_PICKER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            lat = data.getDoubleExtra(MapsPickerActivity.LAT_KEY, 0.0)
            lng = data.getDoubleExtra(MapsPickerActivity.LNG_KEY, 0.0)
            address = data.getStringExtra(MapsPickerActivity.ADDRESS_KEY)
            bInputMap.text = address.replace(";", ", ")
        } else if (requestCode == TypePickerActivity.TYPE_PICKER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            type = data.getIntExtra(TypePickerActivity.ID_TYPE_KEY, 0)
            val icon = data.getStringExtra(TypePickerActivity.ICON_KEY)
            val name = data.getStringExtra(TypePickerActivity.TYPE_KEY)
            bInputType.text = name
            GlideApp.with(this).load(icon).into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    val dsize = resources.getDimension(R.dimen.activity_type_icon_small_size).toInt()
                    val d = BitmapDrawable(
                        resources,
                        Bitmap.createScaledBitmap((resource as BitmapDrawable).bitmap, dsize, dsize, true)
                    )
                    bInputType.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
                }
            })
        }
    }

}