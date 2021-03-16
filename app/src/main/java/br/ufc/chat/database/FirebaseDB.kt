package br.ufc.chat.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDB {
    private val TAG = "FirebaseDB"

    var referece: DatabaseReference
    private var database: FirebaseDatabase

    constructor(path: String){
        database = FirebaseDatabase.getInstance()
        referece = database.getReference(path)


    }


}