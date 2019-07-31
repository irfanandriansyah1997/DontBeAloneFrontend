package edu.unikom.dontbealone.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import edu.unikom.dontbealone.model.DataActivityType
import edu.unikom.dontbealone.util.GlideApp
import kotlinx.android.synthetic.main.fragment_filter_activity.*
import org.jetbrains.anko.support.v4.startActivityForResult


/**
 * Created by Syauqi Ilham on 7/8/2019.
 */
class FilterActivityDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            keyword: String?,
            type: DataActivityType?,
            listener: (keyword: String, type: DataActivityType?) -> Unit
        ): FilterActivityDialogFragment {
            var fragment = FilterActivityDialogFragment()
            fragment.keyword = keyword
            fragment.type = type
            fragment.listener = listener
            return fragment
        }
    }

    var keyword: String? = null
    var type: DataActivityType? = null
    lateinit var listener: (keyword: String, type: DataActivityType?) -> Unit

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
            R.layout.fragment_filter_activity, container,
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
        inActName.setText(keyword)
        bInputType.text = if (type != null) type!!.name else "What kind of activity do you want?"
        GlideApp.with(this).load(type?.icon).into(object : CustomTarget<Drawable>() {
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
        bInputType.setOnClickListener {
            startActivityForResult<TypePickerActivity>(TypePickerActivity.TYPE_PICKER_REQUEST)
        }
        bInputTypeClear.setOnClickListener {
            type = null
            bInputType.text = "What kind of activity do you want?"
            bInputType.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(R.drawable.ic_type),
                null,
                null,
                null
            )
            bInputTypeClear.visibility = View.GONE
        }
        bInputTypeClear.visibility = if (type != null) View.VISIBLE else View.GONE
        bInputCancel.setOnClickListener { dismiss() }
        bInputSave.setOnClickListener {
            listener(inActName.text.toString(), type)
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TypePickerActivity.TYPE_PICKER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getIntExtra(TypePickerActivity.ID_TYPE_KEY, 0)
            val icon = data.getStringExtra(TypePickerActivity.ICON_KEY)
            val name = data.getStringExtra(TypePickerActivity.TYPE_KEY)
            type = DataActivityType(id, name, icon)
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