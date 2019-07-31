package edu.unikom.dontbealone.view.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListChatRoomAdapter
import edu.unikom.dontbealone.model.DataChatRoom
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_message.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class MessageFragment : Fragment() {

    private var chatRooms = mutableListOf<DataChatRoom>()
    private var chatRoomsAdapter = ListChatRoomAdapter(chatRooms, {
        startActivity<ChatActivity>("chat_room" to Gson().toJson(it))
    })

    private val webServices by lazy {
        WebServices.createChat()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listChatRoom.adapter = chatRoomsAdapter
        listChatRoom.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        listChatRoom.isNestedScrollingEnabled = false
        listChatRoom.addItemDecoration(DividerItemDecoration(view.context, RecyclerView.VERTICAL))
    }

    fun loadChatRoom() {
        listChatRoom.showShimmerAdapter()
        webServices.getChatRoom(Helpers.getCurrentUser(context).username).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onNext = {
                if (it.success && it.data != null) {
                    chatRooms.clear()
                    chatRooms.addAll(it.data!!)
                    chatRoomsAdapter.notifyDataSetChanged()
                    tChatNoData.visibility = if (chatRooms.size > 0) View.GONE else View.VISIBLE
                } else {
                    toast(it.message).show()
                }
                listChatRoom.hideShimmerAdapter()
            },
            onError = {
                listChatRoom.hideShimmerAdapter()
                it.printStackTrace()
                toast("An error has occured, please contact the administrator").show()
            },
            onComplete = { }
        )
    }

    override fun onResume() {
        super.onResume()
        if (context != null)
            loadChatRoom()
    }

}
