package a.jinkim.trade.chatdetail

import a.jinkim.trade.DBKey.Companion.DB_CHAT
import a.jinkim.trade.databinding.ActivityChatdetailBinding
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatdetailBinding
    private var chatDB: DatabaseReference? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val chatKey = intent.getLongExtra("key", -1)

        chatList.clear()
        chatDB = Firebase.database.reference.child(DB_CHAT).child("$chatKey")
        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return
                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
        binding.sendButton.setOnClickListener {

            val chatItem = ChatItem(
                senderId = auth.currentUser.uid,
                message = binding.messageEditText.text.toString()
            )

            chatDB?.push()?.setValue(chatItem)
        }


    }

}