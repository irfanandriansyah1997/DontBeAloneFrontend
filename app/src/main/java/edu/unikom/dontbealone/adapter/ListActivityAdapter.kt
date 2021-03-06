package edu.unikom.dontbealone.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.model.DataActivity
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_activity.*
import java.text.NumberFormat
import java.util.*


class ListActivityAdapter(
    private val activity: Activity,
    private val items: List<DataActivity>,
    private val isMyActivity: Boolean,
    private val clickListener: (DataActivity) -> Unit
) :
    RecyclerView.Adapter<ListActivityAdapter.DataActivityListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataActivityListViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_activity, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DataActivityListViewHolder, position: Int) {
        holder.bind(activity, items[position], isMyActivity, clickListener)
    }

    class DataActivityListViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val webServices by lazy {
            WebServices.create()
        }

        fun bind(activity: Activity, item: DataActivity, isMyActivity: Boolean, listener: (DataActivity) -> Unit) {
            tActivityName.text = item.name
            tActivityAddress.text = item.address.replace(";", ", ").split(",")[0]
            tActivityTime.text = item.time.substring(0, item.time.length - 3)
            val price = item.price.toLong()
            tActivityPrice.text =
                if (price > 0) "Rp " + NumberFormat.getInstance(Locale("ID")).format(price) else "FREE"
            tActivityDistance.text = String.format("%.1f", item.distance) + "km"
            tActivityDistance.visibility = if (item.distance != null) View.VISIBLE else View.GONE
            GlideApp.with(containerView).load(item.type.icon).into(activityTypeImage)
            tActivityUserName.visibility = if (item.user != null) View.VISIBLE else View.GONE
            tActivityUserPhoto.visibility = if (item.user != null) View.VISIBLE else View.GONE
            tActivityUserName.text =
                if (item.user?.name != null && !item.user?.name.equals("")) item.user?.name!!.split(" ")[0] else item.user?.username
            Glide.with(containerView).load(item.user?.photo).error(R.drawable.ic_user).into(tActivityUserPhoto)
            if (isMyActivity) {
                tActivityNotif.text = item.userPendingCount.toString()
                tActivityNotif.visibility =
                    if (Helpers.getCurrentUser(activity).username.equals(item.user?.username) && item.userPendingCount > 0) View.VISIBLE else View.GONE
                tActivityStatus.visibility =
                    if (item.myUser != null && item.myUser?.status != null && item.myUser?.level.equals("user")) View.VISIBLE else View.GONE
                if (item.myUser != null) {
                    val statusIcon = when (item.myUser!!.status) {
                        "Accepted" -> R.drawable.ic_check_small
                        "Pending" -> R.drawable.ic_clock_small
                        "Rejected" -> R.drawable.ic_close_small
                        else -> R.drawable.ic_clock_small
                    }
                    tActivityStatus.setCompoundDrawablesWithIntrinsicBounds(
                        tActivityStatus.resources.getDrawable(statusIcon),
                        null,
                        null,
                        null
                    )
                    tActivityStatus.text = item.myUser!!.status
                }
            }
//            Glide.with(containerView).load(item.user.photo).into(tActivityUserImage)
            containerView.setOnClickListener { listener(item) }
        }
    }
}