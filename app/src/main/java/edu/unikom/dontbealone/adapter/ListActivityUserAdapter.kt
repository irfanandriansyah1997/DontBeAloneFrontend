package edu.unikom.dontbealone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.WebServices
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_activity_user.*


class ListActivityUserAdapter(
    private val items: List<DataUser>,
    private val clickListener: (DataUser) -> Unit,
    private val grantClickListener: (String, Int) -> Unit
) :
    RecyclerView.Adapter<ListActivityUserAdapter.DataUserListViewHolder>() {

    var isAdmin: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataUserListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_activity_user, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DataUserListViewHolder, position: Int) {
        holder.bind(isAdmin, items[position], clickListener, grantClickListener)
    }

    class DataUserListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val webServices by lazy {
            WebServices.create()
        }

        fun bind(isAdmin: Boolean, item: DataUser, listener: (DataUser) -> Unit, grantClickListener: (String, Int) -> Unit) {
            detailUserName.text = if (item.name != null && !item.name.equals("")) item.name else item.username
            Glide.with(containerView).load(item.photo).into(detailUserImage)
            detailUserLevel.visibility =
                if (item.status.equals("Accepted") && item.level.equals("admin")) View.VISIBLE else View.GONE
            detailUserAccept.visibility = if (isAdmin && item.status.equals("Pending")) View.VISIBLE else View.GONE
            detailUserReject.visibility = if (isAdmin && item.status.equals("Pending")) View.VISIBLE else View.GONE
            detailUserAccept.setOnClickListener { grantClickListener(item.username, 1) }
            detailUserReject.setOnClickListener { grantClickListener(item.username, 2) }
            containerView.setOnClickListener { listener(item) }
        }
    }
}