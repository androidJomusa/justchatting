package com.example.justchatting.data.chatting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justchatting.data.DTO.ChattingRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChattingFirebaseSource {
    private var chattingRoomMap = HashMap<String, ChattingRoom>()
    val uid = FirebaseAuth.getInstance().uid
    private var _chattingRooms : MutableLiveData<ArrayList<ChattingRoom>> = MutableLiveData()
    val chattingRooms : LiveData<ArrayList<ChattingRoom>>
        get() =  _chattingRooms
    private val _roomFetchError: MutableLiveData<Boolean> = MutableLiveData()
    val roomFetchError : LiveData<Boolean>
        get() = _roomFetchError

    fun setChattingRoomListChangeListener() {

        val userGroupRef = FirebaseDatabase.getInstance().getReference("/user_groups/$uid")
        userGroupRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                _roomFetchError.value = true
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                updateChattingRoom(snapshot)
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                updateChattingRoom(snapshot)
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                chattingRoomMap.remove(snapshot.key)
                _chattingRooms.postValue(getChattingArrayList())
            }
        })
    }

    private fun updateChattingRoom(snapshot1: DataSnapshot){
        val groupId = snapshot1.key
        val chatRoomRef = FirebaseDatabase.getInstance().getReference("/chatrooms/$groupId")
        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val chattingRoom = snapshot.getValue(ChattingRoom::class.java) ?: return

                val userGroupNameRef = FirebaseDatabase.getInstance().getReference("/user_groups/$uid/$groupId")
                userGroupNameRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val groupName = snapshot.getValue(String::class.java)?: return
                        chattingRoom.groupName = groupName
                        chattingRoomMap[groupId!!] = chattingRoom
                        _chattingRooms.postValue(getChattingArrayList())
                    }
                })
            }
        })
    }

    private fun getChattingArrayList(): ArrayList<ChattingRoom> {
        val arrayList = ArrayList(chattingRoomMap.values)
        arrayList.sortByDescending { it.timeStamp }
        return arrayList
    }

}