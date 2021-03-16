package br.ufc.chat.views.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.ufc.chat.Constants
import br.ufc.chat.R
import br.ufc.chat.Section
import br.ufc.chat.models.ItemTalkGroup
import br.ufc.chat.models.TalkGroup
import br.ufc.chat.views.MainActivity
import br.ufc.chat.views.TalkGroupActivity

class RvTalkGroupAdapter(val context: Context, val items: HashMap<String, ItemTalkGroup>, val action: Int) : RecyclerView.Adapter<RvTalkGroupAdapter.MyViewHolder>() {
    private val TAG = RvTalkGroupAdapter::class.simpleName
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val icon: TextView = view.findViewById(R.id.txt_talkgroup_tem_icon)
        val title: TextView = view.findViewById(R.id.txt_talkgroup_item_title)
        val subtitle: TextView = view.findViewById(R.id.txt_talkgroup_item_subtitle)
        val llayContainer: LinearLayout = view.findViewById(R.id.llay_talkgroup_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_talkgroup_item, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val values = ArrayList(items.values)
        val keys = ArrayList(items.keys)



        holder.icon.text = values[position].title[0].toString()

        holder.title.text = values[position].title

        holder.subtitle.text = values[position].subTitle





        holder.llayContainer.setOnClickListener {
            val intent = Intent(context, TalkGroupActivity::class.java)

            intent.putExtra("title", values[position].title)
            intent.putExtra("action", action)
            intent.putExtra("id", values[position].id)

            context.startActivity(intent)


        }
    }
}