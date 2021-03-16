package br.ufc.chat.views.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import br.ufc.chat.R
import br.ufc.chat.Section
import br.ufc.chat.models.ItemMessagesTalkGroup


class RvMessageTalkGroupAdapter (val items: ArrayList<ItemMessagesTalkGroup>) : RecyclerView.Adapter<RvMessageTalkGroupAdapter.MyViewHolder>() {
    private val TAG = RvMessageTalkGroupAdapter::class.simpleName

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.txt_talk_item_title)
        val datetimeStr: TextView = view.findViewById(R.id.txt_talk_item_datetime)
        val message: TextView = view.findViewById(R.id.txt_talkgroup_item_message)
        val llayContainer: LinearLayout = view.findViewById(R.id.llay_talkgroup_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_talk_item, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.datetimeStr.text = items[position].datetime
        holder.message.text = items[position].message

        if(items[position].title == Section.nickemail){
            holder.title.text = "Você"
            holder.title.setBackgroundColor(Color.LTGRAY)
            holder.datetimeStr.setBackgroundColor(Color.LTGRAY)
            holder.message.setBackgroundColor(Color.LTGRAY)
            val newMarginStart = holder.llayContainer.marginEnd
            val marginButton = holder.llayContainer.marginBottom

            val params =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(newMarginStart, 0, 0, marginButton)
            holder.llayContainer.layoutParams = params

        }
    }
}