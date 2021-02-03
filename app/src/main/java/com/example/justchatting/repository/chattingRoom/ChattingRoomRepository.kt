package com.example.justchatting.repository.chattingRoom

import androidx.lifecycle.LiveData
import com.example.justchatting.data.DTO.Message
import com.example.justchatting.data.DTO.UserModel
import io.reactivex.Completable

interface ChattingRoomRepository {

    fun getNewGroupId(): LiveData<String>
    fun getChatLogs(): LiveData<ArrayList<Message>>
    fun getMembers() : LiveData<ArrayList<UserModel>>
    fun sendText(
        text: String,
        groupId: String
    )
    fun fetchError() : LiveData<Boolean>
    fun setChatLogAddListener(groupId: String)
    fun createGroupId()
    fun loadGroupMembers(
        groupId: String?
    )

    fun pushFCM(
        text: String,
        groupId: String
    ) : Completable

    fun addMember(
        invitedMember: java.util.HashMap<String, UserModel>,
        groupId: String
    )
    fun exit(groupId: String)
}