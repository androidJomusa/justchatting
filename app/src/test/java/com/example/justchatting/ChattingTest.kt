package com.example.justchatting

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.justchatting.data.DTO.ChattingRoom
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.repository.chatting.ChattingRepository
import com.example.justchatting.repository.friend.FriendRepository
import com.example.justchatting.ui.chatting.ChattingViewModel
import com.example.justchatting.ui.friend.FriendViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ChattingTest : AutoCloseKoinTest(){

    val chattingRepository: ChattingRepository by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            module{
                factory { Mockito.mock(ChattingRepository::class.java) }
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
    fun 대화방_불러오기_성공() {
        var arrayList = ArrayList<ChattingRoom>()
        arrayList.add(ChattingRoom("",0,"","테스트대화방"))
        Mockito.`when`(chattingRepository.getChattingRooms()).thenReturn(MutableLiveData(arrayList))

        val chattingViewModel = ChattingViewModel(chattingRepository)
        chattingViewModel.getChattingRooms().observeForever{}

        Assert.assertEquals(chattingViewModel.getChattingRooms().value!![0].groupName, "테스트대화방")
    }

    @Test
    fun 대화방_불러오기_실패() {
        Mockito.`when`(chattingRepository.getChattingRooms()).thenReturn(MutableLiveData(ArrayList()))

        val chattingViewModel = ChattingViewModel(chattingRepository)
        chattingViewModel.getChattingRooms().observeForever{}

        Assert.assertTrue(chattingViewModel.getChattingRooms().value!!.isEmpty())
    }
}