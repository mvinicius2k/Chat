package br.ufc.chat.database

import android.util.Log
import br.ufc.chat.Constants
import br.ufc.chat.models.User
import br.ufc.chat.utils.Hash
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException

class UsersDB {
    private val TAG = "UsersDB"
    private var firebaseDB: FirebaseDB

    constructor(){
        this.firebaseDB = FirebaseDB(Constants.DB_USERS_TABLE)
    }

    @Throws(IOException::class)
    suspend fun login(nickemail: String, pass: String): Boolean{
        var resultAuth = false
        coroutineScope {
            val passhash = Hash.sha256(pass)


            firebaseDB.referece.child(nickemail).get().addOnSuccessListener {
                resultAuth = it.value.toString() == passhash
            }.addOnFailureListener {
                launch {
                    throw IOException("Falha a conectar, tente novamente")
                }

            }.await()



        }
        return resultAuth
    }

    @Throws(IOException::class)
    suspend fun exist(nickemail: String): Boolean{
        var exist = false
        coroutineScope {
            firebaseDB.referece.child(nickemail).get().addOnSuccessListener {

                exist = it.value != null
            }.addOnFailureListener {
                launch {
                    throw IOException("Algo de errado aconteceu")
                }
            }.await()
        }

        Log.d(TAG,"$nickemail " + exist.toString().replace("false","não") + " existe")
        return exist
    }

    suspend fun createAccount(emailnick: String, pass: String){
        coroutineScope {
            val passhash = Hash.sha256(pass)

            firebaseDB.referece.child(emailnick).get().addOnSuccessListener {

                if(it.value != null){

                    launch {
                        throw Exception("O nick/email $emailnick já está usado")
                    }



                } else {
                    val user = User(emailnick, passhash)
                    firebaseDB.referece.child(user.nickemail).setValue(user.pass).addOnCompleteListener {

                    }
                }
            }.await()
        }




    }


}
