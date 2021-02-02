package com.example.justchatting.repository.chattingRoom

import androidx.lifecycle.LiveData
import com.example.justchatting.data.DTO.Message
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.data.chattingRoom.ChattingRoomFirebaseSource
import io.reactivex.Completable
import kotlin.collections.ArrayList

class ChattingRoomRepositoryImpl(private val chattingRoomFirebaseSource: ChattingRoomFirebaseSource) :
    ChattingRoomRepository {

    companion object {
        val TAG = "ChattingRoomRepo"
    }


    override fun getChatLogs(): LiveData<ArrayList<Message>> {
        return chattingRoomFirebaseSource.logs
    }

    override fun getMembers(): LiveData<ArrayList<UserModel>> {
        return chattingRoomFirebaseSource.members
    }

    override fun getNewGroupId(): LiveData<String> {
        return chattingRoomFirebaseSource.newGroupId
    }

    override fun setChatLogAddListener(groupId: String) {
        chattingRoomFirebaseSource.setListener(groupId)
    }

    override fun createGroupId() {
        chattingRoomFirebaseSource.createGroupId()
    }

    override fun loadGroupMembers(
        groupId: String?
    ) {
        chattingRoomFirebaseSource.loadGroupMembers(groupId)
    }

    override fun pushFCM(
        text: String,
        groupId: String
    ): Completable = chattingRoomFirebaseSource.pushFCM(text, groupId)

    override fun addMember(
        invitedMember: java.util.HashMap<String, UserModel>,
        groupId: String
    ) {
        chattingRoomFirebaseSource.addMember(invitedMember, groupId)
    }

    override fun exit(groupId: String) {
        chattingRoomFirebaseSource.exit(groupId)
    }

    override fun sendText(
        text: String,
        groupId: String
    ) {
        chattingRoomFirebaseSource.sendText(text, groupId)
    }


}