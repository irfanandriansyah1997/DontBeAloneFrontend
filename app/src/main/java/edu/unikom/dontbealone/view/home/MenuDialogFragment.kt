package edu.unikom.dontbealone.view.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.util.Helpers
import kotlinx.android.synthetic.main.fragment_menu.*
import org.jetbrains.anko.textColor


/**
 * Created by Syauqi Ilham on 7/8/2019.
 */
class MenuDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: (menuId: Int) -> Unit): MenuDialogFragment {
            var fragment = MenuDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }

    lateinit var listener: (menuId: Int) -> Unit
    var selectedMenuId: Int = R.id.bMenuHome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_menu, container,
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
            behavior.isHideable = true
        }
        val user = Helpers.getCurrentUser(view.context)
        txtMenuName.text = if (!user.name.equals("")) user.name else user.username
        txtMenuEmail.text = user.email
        Glide.with(view).load(user.photo).error(R.drawable.ic_user).into(imgMenuProfile)
        bMenuProfile.setOnClickListener { menuClick(it) }
        bMenuHome.setOnClickListener { menuClick(it) }
        bMenuFriend.setOnClickListener { menuClick(it) }
        bMenuMyAct.setOnClickListener { menuClick(it) }
        bMenuMessage.setOnClickListener { menuClick(it) }
        if (selectedMenuId != R.id.bMenuProfile) {
            val v = view.findViewById<TextView>(selectedMenuId)
            if (v is TextView) {
                v.textColor = resources.getColor(R.color.colorPrimary)
//                v.compoundDrawableTintList =
//                    ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
            }
        }
    }

    fun menuClick(view: View) {
        selectedMenuId = view.id
        listener(view.id)
        dismiss()
    }

}