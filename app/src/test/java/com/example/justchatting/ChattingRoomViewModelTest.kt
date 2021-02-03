package com.example.justchatting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justchatting.data.DTO.Message
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.di.viewModelModule
import com.example.justchatting.repository.chattingRoom.ChattingRoomRepository
import com.example.justchatting.ui.chattingRoom.ChattingRoomViewModel
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.mockito.Mockito
import org.mockito.Mockito.times
import java.lang.Exception

class ChattingRoomViewModelTest : AutoCloseKoinTest() {

    private val mockRepository: ChattingRoomRepository by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            module {
                factory { Mockito.mock(ChattingRoomRepository::class.java) }
            }
        )
    }

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val ruleForLivaData = InstantTaskExecutorRule()

    @Test
    fun 대화_기록_불러오기_성공(){
        val chattingRoomViewModel = ChattingRoomViewModel(mockRepository)

        chattingRoomViewModel.groupId = "room_NO_1"

        val messages = ArrayList<Message>(listOf(Message()))
        val mockChatLogs = MutableLiveData<ArrayList<Message>>(messages)
        Mockito.`when`(
            mockRepository.getChatLogs()
        ).thenReturn(mockChatLogs)

        chattingRoomViewModel.setChatLogAddListener(chattingRoomViewModel.groupId)

        Mockito.verify(mockRepository, times(1)).setChatLogAddListener(chattingRoomViewModel.groupId)
        assertEquals(chattingRoomViewModel.getChatLogs().value!![0].text, "")
    }

    @Test
    fun 대화_기록_불러오기_실패(){
        val chattingRoomViewModel = ChattingRoomViewModel(mockRepository)

        Mockito.`when`(
            mockRepository.getChatLogs()
        ).thenReturn(MutableLiveData<ArrayList<Message>>())

        Mockito.`when`(
            mockRepository.fetchError()
        ).thenReturn(MutableLiveData<Boolean>(true))

        assertEquals(chattingRoomViewModel.chatLogFetchError().value, true)
    }

    @Test
    fun 대화방_초대_성공(){
        val chattingRoomViewModel = ChattingRoomViewModel(mockRepository)

        val user = HashMap<String, UserModel>()
        user["uid"] = UserModel(username = "tom")

        chattingRoomViewModel.groupId = "groupId"

        val members = ArrayList<UserModel>()
        members.add(user["uid"] ?: UserModel())
        Mockito.`when`(mockRepository.getMembers()).thenReturn(MutableLiveData(members))

        chattingRoomViewModel.addMember(user)

        assertEquals(chattingRoomViewModel.getMembers().value!![0].username, "tom")
    }
}