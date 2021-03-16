package br.ufc.chat.views.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.ufc.chat.Constants
import br.ufc.chat.views.fragments.FragmentEnum
import br.ufc.chat.views.fragments.TalkFragment

class TabAdapter(var tabs: Context, var supportFragmentManager: FragmentManager,var  tabCount: Int) :
    FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    lateinit var talk: TalkFragment
    lateinit var group: TalkFragment

    override fun getItem(position: Int): Fragment {
        return when(position){
            FragmentEnum.Talk.ordinal ->  TalkFragment.newInstance(Constants.ACTION_TALK)
            FragmentEnum.Group.ordinal -> TalkFragment.newInstance(Constants.ACTION_GROUP)
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return this.tabCount
    }

}