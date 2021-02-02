package com.example.justchatting.ui.chattingRoom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justchatting.JustApp
import com.example.justchatting.data.DTO.Message
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.repository.chattingRoom.ChattingRoomRepository
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent

class ChattingRoomViewModel(private val chattingRoomRepository : ChattingRoomRepository) : ViewModel(), KoinComponent{
    var groupMembers : MutableLiveData<HashMap<String, UserModel>> = MutableLiveData()
    var groupId : String = ""

    fun getChatLogs() : LiveData<ArrayList<Message>>{
        return chattingRoomRepository.getChatLogs()
    }
    fun getNewGroupId(): LiveData<String>{
        return chattingRoomRepository.getNewGroupId()
    }
    fun setChatLogAddListener(groupId: String) {
        chattingRoomRepository.setChatLogAddListener(groupId)
    }

    fun createGroupId() {
        chattingRoomRepository.createGroupId()
    }

    fun sendText(text: String, groupId: String) {
        chattingRoomRepository.sendText(text, groupId)
        chattingRoomRepository.pushFCM(text, groupId).observeOn(Schedulers.io())
            .subscribeOn(Schedulers.computation())
            .subscribe({
            },{

            })
    }

    fun loadGroupMembers(groupId: String?) {
        chattingRoomRepository.loadGroupMembers(groupId)
    }

    fun getMembers(): LiveData<ArrayList<UserModel>> {
        return chattingRoomRepository.getMembers()
    }

    override fun onCleared() {
        JustApp.roomId = ""
        super.onCleared()
    }

    fun addMember(members: HashMap<String, UserModel>?) {
        if (members != null) {
            chattingRoomRepository.addMember(members, groupId)
        }
    }

    fun exit() {
        chattingRoomRepository.exit(groupId)
    }

}