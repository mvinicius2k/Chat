package br.ufc.chat.views.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.ufc.chat.Constants
import br.ufc.chat.R
import br.ufc.chat.database.GroupDB
import br.ufc.chat.models.ItemTalkGroup
import br.ufc.chat.views.ActivityEnum
import br.ufc.chat.views.NewTalkGroupActivity
import br.ufc.chat.views.TalkGroupActivity
import br.ufc.chat.views.adapters.RvTalkGroupAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TalkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkFragment : Fragment() {
    val TAG = TalkFragment::class.simpleName

    lateinit var mContext: Context

    private var action = 0

    lateinit var itemTalkGroupHashMap: HashMap<String, ItemTalkGroup>

    lateinit var rvTalkGroup: RecyclerView
    lateinit var rvTalkGroupManager: RecyclerView.LayoutManager
    lateinit var rvTalkGroupAdapter: RecyclerView.Adapter<*>

    lateinit var btnAdd: FloatingActionButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        action = arguments!!.getInt(ARG_PARAM1)

        Log.d(TAG,"Instanciado fragment com ação nº $action")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        this.mContext = container?.context!!

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_talkgroup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.btnAdd = view.findViewById(R.id.btn_talk_add)
        this.rvTalkGroup = view.findViewById(R.id.rv_talk)
        this.itemTalkGroupHashMap = HashMap()



        this.rvTalkGroupManager = LinearLayoutManager(mContext)

        this.rvTalkGroupAdapter = RvTalkGroupAdapter(mContext, itemTalkGroupHashMap, action)
        rvTalkGroup.apply {
            setHasFixedSize(false)
            layoutManager = rvTalkGroupManager
            adapter = rvTalkGroupAdapter
        }

        GlobalScope.launch (Dispatchers.IO) {
            fillRv()
        }

        btnAdd.setOnClickListener {
            createItem(action)

        }
    }

    fun createItem(action: Int){
        val intent = Intent(activity?.applicationContext, NewTalkGroupActivity::class.java)
        when(action){

            Constants.ACTION_TALK -> {
                intent.putExtra("title", "Conversar...")

            }

            Constants.ACTION_GROUP -> {
                intent.putExtra("title", "Criando um grupo")
            }

            else -> {
                Log.d(TAG,"WTF action = $action")
                return
            }

        }

        intent.putExtra("action", action)

        startActivityForResult(intent, action)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    private suspend fun fillRv(){
        val groupDB = GroupDB()

        val deferred = GlobalScope.async {
            groupDB.getTalkGroupsAndUpdate(action, itemTalkGroupHashMap,rvTalkGroupAdapter)
        }

        try {
            deferred.await()
        } catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TalkFragment.
         */
        // TODO: Rename and change types and number of parameters

        @JvmStatic
        fun newInstance(action: Int) =
            TalkFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, action)
                }


            }

    }
}