package br.ufc.chat.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.ufc.chat.Constants
import br.ufc.chat.R
import br.ufc.chat.database.FirebaseDB
import br.ufc.chat.database.UsersDB
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

class SingupActivity : AppCompatActivity() {

    private lateinit var etEmailOrNick: EditText
    private lateinit var etPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnCreate: Button

    private lateinit var firebaseDB: FirebaseDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = getString(R.string.str_singup_title)

        this.etEmailOrNick = findViewById(R.id.et_singup_email_or_nick)
        this.etPass = findViewById(R.id.et_singup_pass)
        this.etConfirmPass = findViewById(R.id.et_singup_confirm_pass)
        this.btnCreate = findViewById(R.id.btn_singup_create)

        this.firebaseDB = FirebaseDB(Constants.DB_USERS_TABLE)

        this.btnCreate.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                createAccount()
            }


        }


    }

    private suspend fun createAccount() = withContext(Dispatchers.IO){

        val db = UsersDB()

        val nickemail = etEmailOrNick.text.toString()
        val pass0 = etPass.text.toString()
        val pass1 = etConfirmPass.text.toString()

        if(pass0 != pass1){
            runOnUiThread {
                Toast.makeText(applicationContext,getString(R.string.str_singup_different_pass_error), Toast.LENGTH_SHORT).show()
            }

            return@withContext
        }
        val deferred = GlobalScope.async {
            db.createAccount(nickemail, pass0)
        }



        try {
            deferred.await()
            setResult(Constants.REQUEST_CODE_SUCESS)
            finish()

        } catch (e: Exception){
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(applicationContext,e.message, Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setResult(Constants.REQUEST_CODE_RETURN)
        finish()
        return true
    }
}