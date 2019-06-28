package edu.unikom.dontbealone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import edu.unikom.dontbealone.model.DataIntro
import edu.unikom.dontbealone.view.IntroFragment

class IntroPagerAdapter(fm: FragmentManager, list: ArrayList<DataIntro>) : FragmentPagerAdapter(fm) {

    private var list = ArrayList<DataIntro>()

    init {
        this.list = list
    }

    override fun getItem(i: Int): Fragment {
        return IntroFragment.newInstance(list.get(i))
    }

    override fun getCount(): Int {
        return list.size
    }
}
