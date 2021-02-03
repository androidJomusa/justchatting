package com.example.justchatting.data.friend

import androidx.lifecycle.MutableLiveData
import com.example.justchatting.Event
import com.example.justchatting.data.DTO.Friend
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.ui.friend.FriendFragment.AddResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendAdditionFirebaseSource {
    private var _isValidToAdd : MutableLiveData<Event<AddResult>> = MutableLiveData()
    val isValidToAdd : MutableLiveData<Event<AddResult>>
        get() = _isValidToAdd
    val uid = FirebaseAuth.getInstance().uid

    fun addFriendWithEmail(email : String, friends: HashMap<String, UserModel>) {
        if(email.isEmpty())
            return

        val friendEmailRef = FirebaseDatabase.getInstance().getReference("/email/$email")
        friendEmailRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                checkAndAdd(snapshot, friends)
            }
        })
    }

    fun addFriendWithPhoneNumber(phoneNum : String, friends: HashMap<String, UserModel>){
        if(phoneNum.isEmpty())
            return

        val friendPhoneRef = FirebaseDatabase.getInstance().getReference("/phone/$phoneNum")
        friendPhoneRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                checkAndAdd(snapshot, friends)
            }
        })
    }

    private fun checkAndAdd(snapshot :DataSnapshot, friends : HashMap<String, UserModel>){
        val friendId = snapshot.getValue(String()::class.java)
        if(friendId == null){
            _isValidToAdd.postValue(Event(AddResult.FAILED))
            return
        }

        if(friends.containsKey(friendId)) {
            _isValidToAdd.postValue(Event(AddResult.EXIST))
            return
        }

        val fromUserFriendRef = FirebaseDatabase.getInstance().getReference("/friends/$uid/$friendId")
        fromUserFriendRef.setValue(
            Friend(
                true,
                ""
            )
        )
        val toUserFriendRef = FirebaseDatabase.getInstance().getReference("/friends/$friendId/$uid")
        toUserFriendRef.setValue(
            Friend(
                true,
                ""
            )
        )

        _isValidToAdd.postValue(Event(AddResult.SUCCESS))
    }
}