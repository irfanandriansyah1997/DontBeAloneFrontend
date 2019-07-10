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


class ListActivityTypeAdapter(
    private val items: List<DataActivityType>,
    private val clickListener: (DataActivityType) -> Unit
) :
    RecyclerView.Adapter<ListActivityTypeAdapter.DataActivityTypeListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataActivityTypeListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_activity_type, parent, false
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
            val resID = containerView.context.resources
                .getIdentifier(
                    if (item.icon != null) item.icon else "ic_futsal",
                    "drawable",
                    containerView.context.packageName
                )
            Glide.with(containerView).load(resID).into(activityTypeImage)
            containerView.setOnClickListener { listener(item) }
        }
    }
}