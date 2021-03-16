package br.ufc.chat.models

class ItemMessagesTalkGroup(var title: String, var datetime: String, var message: String) {

    @Transient
    var datetimeL = 0L

}