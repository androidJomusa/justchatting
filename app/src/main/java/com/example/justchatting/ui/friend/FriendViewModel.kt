package com.example.justchatting.ui.friend

import android.app.Application
import androidx.lifecycle.*
import com.example.justchatting.Event
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.repository.chatting.SelectGroupRepository
import com.example.justchatting.repository.friend.FriendRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.example.justchatting.ui.friend.FriendFragment.AddResult

class FriendViewModel(private val friendRepository : FriendRepository, application: Application) : AndroidViewModel(application), KoinComponent{

    private val selectGroupRepository : SelectGroupRepository by inject()
    var friends : HashMap<String, UserModel> = HashMap()
        private set

    fun setFriendListChangeListener(){
        friendRepository.setFriendListChangeListener(friends)
    }
    fun getUsers() : LiveData<ArrayList<UserModel>> {
        return friendRepository.getUsers()
    }
    fun isValidToAdd() : MutableLiveData<Event<AddResult>>{
        return friendRepository.isValidToAdd()
    }
    fun syncWithContacts() {
        friendRepository.makeFriendRelationships(getApplication())
    }
    fun addFriendWithPhoneNumber(input: String) {
        friendRepository.addFriendWithPhoneNumber(input, friends)
    }
    fun addFriendWithId(input: String) {
        friendRepository.addFriendWithEmail(input, friends)
    }
    fun getGroupId() : MutableLiveData<Event<String>>{
        return  selectGroupRepository.getGroupId()
    }
    fun loadGroupId(groupMembers: HashMap<String, UserModel?>) {
        selectGroupRepository.loadGroupId(groupMembers)
    }
}