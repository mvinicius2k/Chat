package br.ufc.chat.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.ufc.chat.R
import br.ufc.chat.database.GroupDB
import br.ufc.chat.models.ItemMessagesTalkGroup
import br.ufc.chat.views.adapters.RvMessageTalkGroupAdapter
import br.ufc.chat.views.adapters.RvTalkGroupAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class TalkGroupActivity : AppCompatActivity() {

    private var action = -1
    private var currentId: String? = null

    lateinit var messagesList: ArrayList<ItemMessagesTalkGroup>
    private  lateinit var etMessage: EditText
    private  lateinit var ibSend: ImageButton

    private lateinit var rvTalk: RecyclerView
    private lateinit var rvTalkAdapter: RecyclerView.Adapter<*>
    private lateinit var rvTalkManager: RecyclerView.LayoutManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talkgroup)
        if(intent != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.title = intent.getStringExtra("title")
            this.action = intent.getIntExtra("action", 0)
            this.currentId = intent.getStringExtra("id")
        }

        this.etMessage = findViewById(R.id.et_talk_message)
        this.ibSend = findViewById(R.id.ib_talk_send)
        this.rvTalk = findViewById(R.id.rv_talk)
        this.rvTalkManager = LinearLayoutManager(this)
        this.messagesList = ArrayList()

        rvTalkAdapter = RvMessageTalkGroupAdapter(messagesList)
        rvTalk.apply {
            setHasFixedSize(false)
            layoutManager = rvTalkManager
            adapter = rvTalkAdapter
        }

        GlobalScope.launch(Dispatchers.IO) {
            messagesListener()
        }



        ibSend.setOnClickListener {
            GlobalScope.launch {
                sendMessage()
            }
        }



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    suspend fun messagesListener(){
        val groupDB = GroupDB()

        val deferred = GlobalScope.async {
            groupDB.getMessagesAndUpdate(currentId!!, messagesList, rvTalkAdapter)

        }

        try {
            deferred.await()


        } catch (e: IOException){
            Toast.makeText(applicationContext,e.message,Toast.LENGTH_LONG).show()
        }
    }

    suspend fun sendMessage(){
        val groupDB = GroupDB()

        val deferred = GlobalScope.async {
            groupDB.sendMessage(currentId!!, etMessage.text.toString())
        }

        try {
            deferred.await()
            runOnUiThread {
                etMessage.text.clear()
            }
        } catch (e: IOException){
            runOnUiThread {
                Toast.makeText(applicationContext,e.message,Toast.LENGTH_LONG).show()
            }

        }


    }
}