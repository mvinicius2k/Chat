package br.ufc.chat.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import br.ufc.chat.Constants
import br.ufc.chat.R
import br.ufc.chat.database.FirebaseDB
import br.ufc.chat.views.adapters.TabAdapter
import br.ufc.chat.views.fragments.FragmentEnum
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var btn: ViewPager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = getString(R.string.str_main_title)

        this.tabLayout = findViewById(R.id.tab_main_tablayout)
        this.viewPager = findViewById(R.id.vp_main)



        this.viewPager.adapter = TabAdapter(this, supportFragmentManager, tabLayout.tabCount)




        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab != null){
                    viewPager.currentItem = tab.position
                } else {
                    Log.d(TAG,"Tab nulo")
                }
            }
        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG,"requestCode = $requestCode; resultCode = $resultCode")
        if(requestCode == Constants.ACTION_GROUP){
            if(resultCode == Constants.REQUEST_CODE_SUCESS){
                Toast.makeText(this,"Grupo criado com sucesso", Toast.LENGTH_LONG).show()
            }
        }
    }


}