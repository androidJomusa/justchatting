package com.example.justchatting.repository.chatting

import androidx.lifecycle.LiveData
import com.example.justchatting.data.DTO.ChattingRoom

interface ChattingRepository {
    fun setChattingRoomListChangeListener()
    fun getChattingRooms() : LiveData<ArrayList<ChattingRoom>>
    fun roomFetchError(): LiveData<Boolean>
}