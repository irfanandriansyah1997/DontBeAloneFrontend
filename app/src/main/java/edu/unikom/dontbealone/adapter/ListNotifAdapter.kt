package edu.unikom.dontbealone.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataNotif
import edu.unikom.dontbealone.util.WebServices
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_notif.*


class ListNotifAdapter(private val items: List<DataNotif>, private val clickListener: (DataNotif) -> Unit) :
    RecyclerView.Adapter<ListNotifAdapter.DataNotifListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataNotifListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_notif, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DataNotifListViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    class DataNotifListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val webServices by lazy {
            WebServices.create()
        }

        fun bind(item: DataNotif, listener: (DataNotif) -> Unit) {
            tNotifContent.text = Html.fromHtml(item.content)
            tNotifTime.text = item.time
            Glide.with(containerView).load(item.photo).into(notifImage)
            containerView.setOnClickListener { listener(item) }
        }
    }
}