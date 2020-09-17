package com.example.justchatting.ui.chatting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.justchatting.UserModel
import com.example.justchatting.repository.chatting.SelectGroupRepository
import com.example.justchatting.repository.chatting.SelectGroupRepositoryImpl
import org.koin.core.KoinComponent
import org.koin.core.inject

class SelectGroupViewModel : ViewModel() ,KoinComponent{
    private val selectGroupRepository : SelectGroupRepository by inject()

    fun load(){
        selectGroupRepository.loadFriends()
    }

    fun getFriends(): LiveData<ArrayList<UserModel>> {
        return (selectGroupRepository as SelectGroupRepositoryImpl).friends
    }

    fun loadGroupId(groupMembers : HashMap<String,Boolean>){
        selectGroupRepository.loadGroupId(groupMembers)
    }
    fun getGroupId() : LiveData<String>{
        return (selectGroupRepository as SelectGroupRepositoryImpl).groupId
    }
}