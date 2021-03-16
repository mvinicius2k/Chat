package br.ufc.chat.database

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import br.ufc.chat.Constants
import br.ufc.chat.Section
import br.ufc.chat.models.ItemMessagesTalkGroup
import br.ufc.chat.models.ItemTalkGroup
import br.ufc.chat.models.Message
import br.ufc.chat.models.TalkGroup
import br.ufc.chat.utils.Strings
import br.ufc.chat.views.fragments.TalkFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.io.IOException

class GroupDB {
    private val TAG = GroupDB::class.simpleName
    private var firebaseDB: FirebaseDB




    constructor(){
        this.firebaseDB = FirebaseDB(Constants.DB_GROUPS_TABLE)
    }

    @Throws(IOException::class)
    suspend fun createGroup(talkGroup: TalkGroup){

        coroutineScope {

            val key = firebaseDB.referece.push().key

            firebaseDB.referece.child("TalkGroup-$key").setValue(talkGroup).addOnSuccessListener {
                return@addOnSuccessListener
            }.addOnFailureListener {
                throw IOException("Algo de errado ocorrou ao criar isto")
            }
        }

    }



    suspend fun getTalkGroupsAndUpdate(action: Int, talkGroupHashMap: HashMap<String, ItemTalkGroup>, adapter: RecyclerView.Adapter<*>){


        coroutineScope {

            val listener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "getTalkGroups cancelado")
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d(TAG,"Dados alterados")
                    talkGroupHashMap.clear()

                    snapshot.children.forEach {

                        val child = it.value as HashMap<*,*>

                        val actionChild = child[TalkGroup::action.name] as Long?

                        if(action.toLong() == actionChild){
                            val usersList = child[TalkGroup::usersList.name] as List<String>

                            if(Section.nickemail in usersList){
                                var title = child[TalkGroup::title.name] as String?

                                if(title == Section.nickemail && action == Constants.ACTION_TALK){
                                    title = usersList[0]
                                }

                                val subtitle = Strings.longToDateTime((child[TalkGroup::lastActivity.name] as Long?)!!)
                                val id = it.key

                                val talkGroup = ItemTalkGroup(
                                        title!!,
                                        subtitle!!,
                                        id!!
                                )

                                talkGroupHashMap[talkGroup.id] = talkGroup
                            }


                        }

                    }

                    adapter.notifyDataSetChanged()
                }

            }
            firebaseDB.referece.addValueEventListener(listener)

        }





    }


    @Throws(IOException::class)
    suspend fun getMessagesAndUpdate(groupid: String, listRef: ArrayList<ItemMessagesTalkGroup>, adapter: RecyclerView.Adapter<*>){
        coroutineScope {
            val listener = object  : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "getMessagesAndUpdate cancelado")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG,"Dados alterados")


                    val list = ArrayList<ItemMessagesTalkGroup>()

                    snapshot.child(groupid)
                            .child(TalkGroup::messageList.name)
                            .children.forEach {
                                val hashMap = it.value as HashMap<*,*>

                                val text = hashMap[Message::text.name] as String
                                val datetime = Strings.longToDateTime(hashMap[Message::datetime.name] as Long)
                                val username = hashMap[Message::username.name] as String

                                val itemMessage = ItemMessagesTalkGroup(username, datetime, text)

                                itemMessage.datetimeL = hashMap[Message::datetime.name] as Long
                                list.add(itemMessage)


                                Log.d(TAG, "mensagens $it")


                                //val child = it.value as HashMap<*,*>
                                //val messageList = child[TalkGroup::messageList.name] as List<*>
                            }
                    list.sortWith(compareBy {it.datetimeL} )
                    listRef.clear()
                    listRef.addAll(list)
                    adapter.notifyDataSetChanged()

                }

            }
            firebaseDB.referece.addValueEventListener(listener)
        }


    }

    suspend fun sendMessage(groupid: String, text: String){

        coroutineScope {

            val time = ServerValue.TIMESTAMP

            firebaseDB.referece
                    .child(groupid)
                    .child(TalkGroup::lastActivity.name).setValue(time)
                    .addOnSuccessListener {
                        val key = "Message-" + firebaseDB.referece.push().key

                        val hashMap = HashMap<String, Message>()

                        val message = Message(Section.nickemail!!, text, time)

                        hashMap[key!!] = message

                        firebaseDB.referece
                                .child(groupid)
                                .child(TalkGroup::messageList.name)
                                .child(key!!)
                                .setValue(message).addOnSuccessListener {

                                }.addOnFailureListener {
                                    throw IOException("Falha ao enviar mensagem")
                                }
                    }.addOnFailureListener {
                        throw IOException("Falha ao tentar enviar mensagem")
                    }.await()



        }

    }






}