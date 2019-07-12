package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.unikom.dontbealone.R
import kotlinx.android.synthetic.main.fragment_filter_activity.*
import org.jetbrains.anko.support.v4.toast


/**
 * Created by Syauqi Ilham on 7/8/2019.
 */
class FilterActivityDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: (keyword: String) -> Unit): FilterActivityDialogFragment {
            var fragment = FilterActivityDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }

    lateinit var listener: (keyword: String) -> Unit

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
        bInputCancel.setOnClickListener { dismiss() }
        bInputSave.setOnClickListener {
            if (inActName.text.toString().trim().length > 0) {
                listener(inActName.text.toString())
                dismiss()
            } else
                toast("Please input the search keyword")
        }
    }


}