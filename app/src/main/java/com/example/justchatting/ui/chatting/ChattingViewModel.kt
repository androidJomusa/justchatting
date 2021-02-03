package com.example.justchatting.ui.chatting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.justchatting.data.DTO.ChattingRoom
import com.example.justchatting.repository.chatting.ChattingRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class ChattingViewModel( private val chattingRepository : ChattingRepository) : ViewModel(), KoinComponent{

    fun getChattingRooms(): LiveData<ArrayList<ChattingRoom>> {
        return chattingRepository.getChattingRooms()
    }
    fun setChattingRoomListChangeListener() {
        chattingRepository.setChattingRoomListChangeListener()
    }

    fun roomFetchError(): LiveData<Boolean> = chattingRepository.roomFetchError()
}