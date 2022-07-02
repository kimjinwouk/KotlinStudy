package a.jinkim.trade.chatlist

import a.jinkim.trade.DBKey.Companion.CHILD_CHAT
import a.jinkim.trade.DBKey.Companion.DB_USERS
import a.jinkim.trade.R
import a.jinkim.trade.chatdetail.ChatRoomActivity
import a.jinkim.trade.databinding.FragmentChatlistBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(R.layout.fragment_chatlist) {

    private var binding: FragmentChatlistBinding? = null
    private lateinit var chatlistAdapter: ChatListAdapter
    private lateinit var articleDB: DatabaseReference
    private val chatRoomList = mutableListOf<ChatListItem>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatListFragment = FragmentChatlistBinding.bind(view)
        binding = fragmentChatListFragment



        chatlistAdapter = ChatListAdapter(onItemClicked = {
            val intent = Intent(view.context,ChatRoomActivity::class.java)
            intent.putExtra("key",it.key)
            startActivity(intent)
        })

        fragmentChatListFragment.chatListRecyclerView.adapter = chatlistAdapter
        fragmentChatListFragment.chatListRecyclerView.layoutManager =
            LinearLayoutManager(context)



        auth.currentUser ?: return
        chatRoomList.clear()
        val chatDB =
            Firebase.database.reference.child(DB_USERS).child(auth.currentUser.uid).child(CHILD_CHAT)
        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatlistAdapter.submitList(chatRoomList)
                chatlistAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun onResume() {
        super.onResume()
        chatlistAdapter.notifyDataSetChanged()
    }

}