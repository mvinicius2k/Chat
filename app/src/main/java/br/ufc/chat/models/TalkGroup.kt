package br.ufc.chat.models

import java.io.Serializable

class TalkGroup(var title: String, var usersList: ArrayList<String>?, var messageList: ArrayList<Message>?, var action: Int?, var lastActivity: MutableMap<String, String>?) : Serializable{

    @Transient
    var lastActivityL: Long = -1
    @Transient
    var id: String? = null

}