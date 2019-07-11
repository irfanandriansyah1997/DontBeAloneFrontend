package edu.unikom.dontbealone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataActivityType
import edu.unikom.dontbealone.util.WebServices
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_activity_type.*


class ListActivityTypeListAdapter(
    private val items: List<DataActivityType>,
    private val clickListener: (DataActivityType) -> Unit
) :
    RecyclerView.Adapter<ListActivityTypeListAdapter.DataActivityTypeListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataActivityTypeListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_activity_type_list, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DataActivityTypeListViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    class DataActivityTypeListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val webServices by lazy {
            WebServices.create()
        }

        fun bind(item: DataActivityType, listener: (DataActivityType) -> Unit) {
            tActivityTypeName.text = item.name
            Glide.with(containerView).load(item.icon).into(activityTypeImage)
            containerView.setOnClickListener { listener(item) }
        }
    }
}