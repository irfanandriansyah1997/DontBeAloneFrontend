package edu.unikom.dontbealone.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataChatRoom
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.WebServices
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_room.*
import java.text.SimpleDateFormat


class ListChatRoomAdapter(
    private val items: List<DataChatRoom>,
    private val clickListener: (DataChatRoom) -> Unit
) :
    RecyclerView.Adapter<ListChatRoomAdapter.DataChatRoomListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataChatRoomListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat_room, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DataChatRoomListViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    class DataChatRoomListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val webServices by lazy {
            WebServices.create()
        }

        fun bind(item: DataChatRoom, listener: (DataChatRoom) -> Unit) {
            GlideApp.with(activityTypeImage).load(item.activity.type.icon).into(activityTypeImage)
            tActivityName.text = item.activity.name
            tChatContent.text = if (item.message != null) item.message.toString() else "Tap here to start chatting"
            tChatContent.alpha = if (item.message != null) 1f else 0.5f
            tChatNotif.text = item.notif.toString()
            tChatNotif.visibility = if (item.notif > 0) View.VISIBLE else View.GONE
            val sdf1 = SimpleDateFormat("dd/MM/yyyy")
            val sdf2 = SimpleDateFormat("HH:mm")
            tChatTime.visibility = if (item.message != null) View.VISIBLE else View.GONE
            if (item.message != null)
                tChatTime.text =
                    if (DateUtils.isToday(item.message!!.date)) sdf2.format(item.message!!.date) else sdf1.format(item.message!!.date)
            containerView.setOnClickListener { listener(item) }
        }
    }
}