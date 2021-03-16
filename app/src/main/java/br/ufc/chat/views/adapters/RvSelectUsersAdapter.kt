package br.ufc.chat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.ufc.chat.Constants
import br.ufc.chat.R
import br.ufc.chat.Section
import br.ufc.chat.models.User
import br.ufc.chat.views.NewTalkGroupActivity
import br.ufc.chat.views.TalkGroupActivity

class RvSelectUsersAdapter  (val users: ArrayList<String>, val action: Int,  val context: Context): RecyclerView.Adapter<RvSelectUsersAdapter.MyViewHolder>() {

    private val TAG = RvSelectUsersAdapter::class.simpleName



    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val nick: TextView = view.findViewById(R.id.txt_marked_item_nick)
        val ibRemove: ImageButton = view.findViewById(R.id.ib_marked_item_remove)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSelectUsersAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_select_users, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  users.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if(action == Constants.ACTION_TALK && users[position] == Section.nickemail)
            return
        if(action == Constants.ACTION_GROUP && users[position] == Section.nickemail){
            holder.ibRemove.visibility = View.GONE
        }

        val user = users[position]

        holder.nick.text = user


        holder.ibRemove.setOnClickListener {
            if(context is NewTalkGroupActivity){

                    context.selectedList.removeAt(position)
                    context.rvNewTalkGroupAdapter.notifyItemRemoved(position)




            }
        }


    }
}