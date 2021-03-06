package edu.unikom.dontbealone.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataUser
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user.*


class ListUserAdapter(
    private val items: List<DataUser>,
    private val clickListener: (DataUser) -> Unit
) :
    RecyclerView.Adapter<ListUserAdapter.DataUserListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataUserListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_user, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DataUserListViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    class DataUserListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val webServices by lazy {
            WebServices.create()
        }

        fun bind(
            item: DataUser,
            listener: (DataUser) -> Unit
        ) {
            detailUserName.text = if (item.name != null && !item.name.equals("")) item.name else item.username
            detailUserName.setTypeface(
                detailUserName.typeface,
                if (item.username.equals(Helpers.getCurrentUser(detailUserName.context).username)) Typeface.BOLD else Typeface.NORMAL
            )
            Glide.with(containerView).load(item.photo).error(R.drawable.ic_user).into(detailUserImage)
//            detailUserLevel.visibility =
//                if (item.status.equals("Accepted") && item.level.equals("admin")) View.VISIBLE else View.GONE
//            detailUserAccept.visibility = if (isAdmin && item.status.equals("Pending")) View.VISIBLE else View.GONE
//            detailUserReject.visibility = if (isAdmin && item.status.equals("Pending")) View.VISIBLE else View.GONE
//            detailUserAccept.setOnClickListener { grantClickListener(item.username, 1) }
//            detailUserReject.setOnClickListener { grantClickListener(item.username, 2) }
            containerView.setOnClickListener { listener(item) }
        }
    }
}