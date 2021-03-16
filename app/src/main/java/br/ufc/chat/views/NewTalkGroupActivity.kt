package br.ufc.chat.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.ufc.chat.Constants
import br.ufc.chat.R
import br.ufc.chat.Section
import br.ufc.chat.database.GroupDB
import br.ufc.chat.database.UsersDB
import br.ufc.chat.models.Message
import br.ufc.chat.models.TalkGroup
import br.ufc.chat.views.adapters.RvSelectUsersAdapter
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.*
import java.io.IOException

class NewTalkGroupActivity : AppCompatActivity() {


    private  lateinit var etNickmail: EditText
    private  lateinit var ibGo: ImageButton
    private lateinit var rvNewTalkGroup: RecyclerView
    lateinit var rvNewTalkGroupAdapter: RecyclerView.Adapter<*>
    private lateinit var rvNewTalkGroupManager: RecyclerView.LayoutManager
    private lateinit var ibAdd: ImageButton
    lateinit var etNameGroup: EditText
    private var action: Int = 0

    lateinit var selectedList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_talk_group)

        if(intent != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.title = intent.getStringExtra("title")
            this.action = intent.getIntExtra("action", 0)
        }

        this.etNickmail = findViewById(R.id.et_new_talkgroup_nickemail)
        this.ibGo = findViewById(R.id.ib_new_talkgroup_go)
        this.rvNewTalkGroup = findViewById(R.id.rv_new_talkgroup)
        this.ibAdd = findViewById(R.id.ib_new_talkgroup_add)
        this.etNameGroup = findViewById(R.id.et_new_talkgroup_name)
        this.selectedList = ArrayList()
        selectedList.add(Section.nickemail!!)

        this.rvNewTalkGroupManager = LinearLayoutManager(this)

        rvNewTalkGroupAdapter = RvSelectUsersAdapter(selectedList, action, this)
        rvNewTalkGroup.apply {
            setHasFixedSize(false)
            layoutManager = rvNewTalkGroupManager
            adapter = rvNewTalkGroupAdapter
        }

        if(action == Constants.ACTION_GROUP){
            ibAdd.visibility = View.VISIBLE
            etNameGroup.visibility = View.VISIBLE

        } else if(action == Constants.ACTION_TALK){
            rvNewTalkGroup.visibility = View.GONE
        }


        ibGo.setOnClickListener {

            if(action == Constants.ACTION_TALK){



                GlobalScope.launch(Dispatchers.IO){
                    createTalk()
                }


            } else if(action == Constants.ACTION_GROUP){
                GlobalScope.launch(Dispatchers.IO){
                    createGroup()
                }
            }


        }
        ibAdd.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                addToGroup()
            }
        }

    }

    private suspend fun userExist(nickemail: String): Boolean {

        val db = UsersDB()
        var exist = false

        val deferred = GlobalScope.async {
            exist = db.exist(nickemail)
        }

        try {
            deferred.await()

        } catch (e: IOException){
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(applicationContext,e.message, Toast.LENGTH_SHORT).show()
            }
        }





        return exist
    }

    private suspend fun createTalk() = withContext(Dispatchers.IO){





        val user = etNickmail.text.toString()


        if(!userExist(user)){
            runOnUiThread {
                Toast.makeText(applicationContext, "$user não existe", Toast.LENGTH_LONG).show()
            }

            return@withContext
        }

        val groupDB = GroupDB()

        if(user == Section.nickemail){
            runOnUiThread {
                Toast.makeText(applicationContext, getString(R.string.str_new_talkgroup_selfadd_error), Toast.LENGTH_LONG).show()

            }

            return@withContext
        }

        if(user in selectedList){
            runOnUiThread {
                selectedList
                1+1
                Toast.makeText(applicationContext, getString(R.string.str_new_talkgroup_readd_error), Toast.LENGTH_LONG)    .show()
            }

            return@withContext
        }



        selectedList.add(user)


        if(action == Constants.ACTION_TALK){
            val talkGroup = TalkGroup(user, selectedList, ArrayList<Message>(), action, ServerValue.TIMESTAMP)

            val deferred = GlobalScope.async {
                groupDB.createGroup(talkGroup)
            }

            try {
                deferred.await()
                setResult(Constants.REQUEST_CODE_SUCESS)
                finish()
            } catch (e: IOException){
                runOnUiThread {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            } catch (ex: NullPointerException){
                runOnUiThread {
                    Toast.makeText(applicationContext, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            runOnUiThread {
                etNickmail.text.clear()
                rvNewTalkGroupAdapter.notifyItemInserted(selectedList.size-1)
            }

        }



    }

    private suspend fun addToGroup(){
        createTalk()
    }

    private suspend fun createGroup(){

        val groupDB = GroupDB()

        val title = etNameGroup.text.toString()

        if(title.isNullOrBlank()){
            runOnUiThread {
                Toast.makeText(applicationContext, "Digite um nome para o grupo", Toast.LENGTH_SHORT).show()

            }
            return
            //throw  NullPointerException("Algum campo está vazio")
        }

        val talkGroup = TalkGroup(title, selectedList, ArrayList<Message>(), action, ServerValue.TIMESTAMP)

        val deferred = GlobalScope.async {
            groupDB.createGroup(talkGroup)
        }

        try {
            deferred.await()
            setResult(Constants.REQUEST_CODE_SUCESS)
            finish()
        } catch (e: IOException){
            runOnUiThread {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setResult(Constants.REQUEST_CODE_RETURN)
        finish()
        return true
    }

}