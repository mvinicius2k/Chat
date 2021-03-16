package br.ufc.chat.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import br.ufc.chat.Constants
import br.ufc.chat.R
import br.ufc.chat.Section
import br.ufc.chat.database.UsersDB
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException

class LoginActivity : AppCompatActivity() {
    lateinit var txtSingup : TextView
    lateinit var btnLogin : Button
    lateinit var etLogin : EditText
    lateinit var etPass : EditText
    lateinit var txtLoginStatus : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        txtSingup = findViewById(R.id.txt_to_singup)
        btnLogin = findViewById(R.id.btn_login)
        etLogin = findViewById(R.id.et_login)
        etPass = findViewById(R.id.et_pass)
        txtLoginStatus = findViewById(R.id.txt_login_status)

        txtSingup.setOnClickListener {
            val intent = Intent(this, SingupActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val loginStr = etLogin.text.toString()
            val passStr = etPass.text.toString()

            GlobalScope.launch(Dispatchers.IO) {
                login(loginStr, passStr)
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ActivityEnum.Singup.ordinal){
            if(resultCode == Constants.REQUEST_CODE_SUCESS){
                Toast.makeText(this,getString(R.string.str_login_account_created), Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun login(user : String, pass : String) = withContext(Dispatchers.IO){
        val db = UsersDB()

        runOnUiThread {
            btnLogin.isEnabled = false
        }

        val deferred = GlobalScope.async {
            db.login(user, pass)
        }



        try {

            var sucess = deferred.await()

            if(sucess){

                val intent = Intent(applicationContext, MainActivity::class.java)
                Section.nickemail = user
                startActivity(intent)
                finish()
                return@withContext

            } else{
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_login_auth_incorrect),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }


        } catch (e: Exception){
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(applicationContext, getString(R.string.str_login_auth_error), Toast.LENGTH_SHORT).show()
            }

        } finally {

            runOnUiThread {
                btnLogin.isEnabled = true
            }
        }

//        Toast.makeText(applicationContext, resources.getText(R.string.txt_login_status_error0), Toast.LENGTH_SHORT).show()
    }
}