package edu.unikom.dontbealone.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataChat
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.Helpers
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat.*
import kotlinx.android.synthetic.main.item_chat_me.*
import java.text.SimpleDateFormat


class ListChatAdapter(
    private val context: Context,
    private val options: FirebaseRecyclerOptions<DataChat>,
    private val clickListener: (DataChat) -> Unit
) : FirebaseRecyclerAdapter<DataChat, ListChatAdapter.DataChatRoomListViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataChatRoomListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            if (viewType == 1) R.layout.item_chat_me else R.layout.item_chat, parent, false
        )
    )

    override fun onBindViewHolder(holder: DataChatRoomListViewHolder, p1: Int, item: DataChat) {
        val prevItem = if (p1 > 0) getItem(p1 - 1) else null
        val prevDate = if (prevItem?.date != null) SimpleDateFormat("dd/MM/yyyy").format(prevItem.date) else null
        val curDate = SimpleDateFormat("dd/MM/yyyy").format(getItem(p1).date)
        val showHeader = prevDate == null || !prevDate.equals(curDate)
        holder.bind(prevItem, item, getItemViewType(p1) == 1, showHeader, clickListener)
    }

    override fun getItemViewType(position: Int): Int {
        val currentUser = Helpers.getCurrentUser(context)
        return if (getItem(position).user?.username.equals(currentUser.username)) 1 else 0
    }

    class DataChatRoomListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(
            prevItem: DataChat?,
            item: DataChat,
            isMe: Boolean,
            showDateHeader: Boolean,
            listener: (DataChat) -> Unit
        ) {
            val sameUser = prevItem != null && prevItem.user?.username.equals(item.user?.username)
            if (!isMe) {
                GlideApp.with(imgChatProfile).load(item.user?.photo).error(R.drawable.ic_user).into(imgChatProfile)
                tChatUsername.text = if (item.user?.name != null && !item.user?.name.equals("")) item.user.name else item.user?.username
                tChatContent.text = item.message
                val sdf1 = SimpleDateFormat("dd/MM/yyyy")
                val sdf2 = SimpleDateFormat("HH:mm")
                tChatTime.text = sdf2.format(item.date)
                tChatUsername.visibility = if (sameUser) View.GONE else View.VISIBLE
                imgChatProfile.visibility = if (sameUser) View.INVISIBLE else View.VISIBLE
                tChatDateHeader.text = if (DateUtils.isToday(item.date)) "Today" else sdf1.format(item.date)
                tChatDateHeader.visibility = if (showDateHeader) View.VISIBLE else View.GONE
            } else {
                tChatContentMe.text = item.message
                val sdf1 = SimpleDateFormat("dd/MM/yyyy")
                val sdf2 = SimpleDateFormat("HH:mm")
                tChatTimeMe.text = sdf2.format(item.date)
                tChatDateHeaderMe.text = if (DateUtils.isToday(item.date)) "Today" else sdf1.format(item.date)
                tChatDateHeaderMe.visibility = if (showDateHeader) View.VISIBLE else View.GONE
            }
        }
    }
}