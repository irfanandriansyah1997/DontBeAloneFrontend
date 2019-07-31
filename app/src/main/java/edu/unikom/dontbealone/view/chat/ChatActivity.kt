package edu.unikom.dontbealone.view.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import edu.unikom.dontbealone.R
import edu.unikom.dontbealone.adapter.ListChatAdapter
import edu.unikom.dontbealone.model.DataChat
import edu.unikom.dontbealone.model.DataChatRoom
import edu.unikom.dontbealone.util.GlideApp
import edu.unikom.dontbealone.util.Helpers
import edu.unikom.dontbealone.util.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.toast

class ChatActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var chatAdapter: ListChatAdapter

    private val webServices by lazy {
        WebServices.createChat()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val chatRoom = Gson().fromJson(intent?.extras?.getString("chat_room"), DataChatRoom::class.java)
        bBottomNavBack.setOnClickListener { finish() }
        titleChat.text = chatRoom?.activity?.name
//        titleChat.setOnClickListener {
//            startActivity(
//                intentFor<DetailActivity>("id_activity" to chatRoom.activity.id).addFlags(
//                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                )
//            )
//        }
        GlideApp.with(this).load(chatRoom?.activity?.type?.icon).into(iconChat)
//        iconChat.setOnClickListener {
//            startActivity(
//                intentFor<DetailActivity>("id_activity" to chatRoom.activity.id).addFlags(
//                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                )
//            )
//        }
//        bApplyFilter.setOnClickListener {
//            val input = FilterActivityDialogFragment.newInstance({
//                listFragment.keyword = it
//                listFragment.loadActivity()
//            })
//            input.show(
//                supportFragmentManager,
//                "input_dialog_fragment"
//            )
//        }
        databaseReference = FirebaseDatabase.getInstance().reference
        val parser = SnapshotParser<DataChat> { snapshot ->
            snapshot.getValue(DataChat::class.java)!!
        }
        val options =
            FirebaseRecyclerOptions.Builder<DataChat>()
                .setQuery(databaseReference.child("database/" + chatRoom.id), parser).build()
        chatAdapter = ListChatAdapter(this, options, {

        })
        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                listChat.scrollToPosition(chatAdapter.itemCount - 1)
            }
        })
        listChat.adapter = chatAdapter
        listChat.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        bChatSend.setOnClickListener {
            if (inChatMessage.text.toString().trim().length > 0) {
                val user = Helpers.getCurrentUser(it.context)
//                val chat =
//                    DataChat(message = inChatMessage.text.toString(), user = user)
//                databaseReference.child("database-1").push().setValue(chat)
                webServices.sendChat(chatRoom.activity.id, user.username, inChatMessage.text.toString().trim())
                    .subscribeOn(
                        Schedulers.io()
                    ).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribeBy(
                        onNext = {
                            if (!it.success) {
                                toast(it.message)
                            }
                        },
                        onError = {
                            it.printStackTrace()
                            toast("An error has occured, please contact the administrator").show()
                        },
                        onComplete = { }
                    )
                inChatMessage.text = null
            }
        }
    }

    override fun onStart() {
        super.onStart()
        chatAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatAdapter.stopListening()
    }
}
