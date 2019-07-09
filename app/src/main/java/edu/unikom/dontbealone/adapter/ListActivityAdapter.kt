package edu.unikom.dontbealone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.util.WebServices
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_activity.*


class ListActivityAdapter(private val items: List<DataActivity>, private val clickListener: (DataActivity) -> Unit) :
    RecyclerView.Adapter<ListActivityAdapter.DataActivityListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataActivityListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_activity, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DataActivityListViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    class DataActivityListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val webServices by lazy {
            WebServices.create()
        }

        fun bind(item: DataActivity, listener: (DataActivity) -> Unit) {
            tActivityName.text = item.name
            tActivityAddress.text = item.address
            tActivityTime.text = item.time
            tActivityPrice.text = item.price
            val resID = containerView.context.resources
                .getIdentifier(item.type.icon, "drawable", containerView.context.packageName)
            Glide.with(containerView).load(resID).into(activityTypeImage)
//            Glide.with(containerView).load(item.user.photo).into(tActivityUserImage)
            containerView.setOnClickListener { listener(item) }
        }
    }
}