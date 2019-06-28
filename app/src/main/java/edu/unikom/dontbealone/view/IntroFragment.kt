package edu.unikom.dontbealone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataIntro
import kotlinx.android.synthetic.main.fragment_intro.*

class IntroFragment : Fragment() {

    private lateinit var item: DataIntro

    fun setItem(item: DataIntro) {
        this.item = item
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgResId = context!!.resources.getIdentifier(item.image, "drawable", context!!.packageName)
        imgTutorialImage.setImageResource(imgResId)
        txtTutorialTitle.text = item.titleText
        txtTutorialDesc.text = item.descText
    }

    companion object {

        fun newInstance(item: DataIntro): IntroFragment {
            val fragment = IntroFragment()
            fragment.setItem(item)
            return fragment
        }
    }
}
