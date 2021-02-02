package com.example.justchatting

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.repository.auth.AuthRepository
import com.example.justchatting.repository.chatting.SelectGroupRepository
import com.example.justchatting.repository.friend.FriendRepository
import com.example.justchatting.ui.friend.FriendFragment
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

class FriendTest : AutoCloseKoinTest(){

    val friendRepository: FriendRepository by inject()

    @Mock
    lateinit var application: Application


    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            module{
                factory { Mockito.mock(FriendRepository::class.java) }
            }
        )
    }

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val ruleForLivaData = InstantTaskExecutorRule()

    @Test
    fun 친구목록_불러오기_성공() {

        var arrayList = ArrayList<UserModel>()
        arrayList.add(UserModel("","tom","","","",""))
        Mockito.`when`(friendRepository.getUsers()).thenReturn(MutableLiveData(arrayList))

        val friendViewModel = FriendViewModel( friendRepository, application)
        friendViewModel.getUsers().observeForever{}

        Assert.assertEquals(friendViewModel.getUsers().value!![0].username, "tom")
    }

    @Test
    fun 친구목록_불러오기_실패() {
        Mockito.`when`(friendRepository.getUsers()).thenReturn(MutableLiveData(ArrayList()))

        val friendViewModel = FriendViewModel(friendRepository, application)
        friendViewModel.getUsers().observeForever{}

        Assert.assertTrue(friendViewModel.getUsers().value!!.isEmpty())
    }

    @Test
    fun 친구추가_성공() {
        Mockito.`when`(friendRepository.isValidToAdd())
            .thenReturn(MutableLiveData(Event(FriendFragment.AddResult.SUCCESS)))

        val friendViewModel = FriendViewModel(friendRepository, application)
        friendViewModel.isValidToAdd().observeForever{}

        Assert.assertEquals(friendViewModel.isValidToAdd().value!!.peekContent(), FriendFragment.AddResult.SUCCESS)
    }

    @Test
    fun 친구추가_중복() {
        Mockito.`when`(friendRepository.isValidToAdd())
            .thenReturn(MutableLiveData(Event(FriendFragment.AddResult.EXIST)))

        val friendViewModel = FriendViewModel(friendRepository, application)
        friendViewModel.isValidToAdd().observeForever{}

        Assert.assertEquals(friendViewModel.isValidToAdd().value!!.peekContent(), FriendFragment.AddResult.EXIST)
    }

    @Test
    fun 친구추가_실패() {
        Mockito.`when`(friendRepository.isValidToAdd())
            .thenReturn(MutableLiveData(Event(FriendFragment.AddResult.FAILED)))

        val friendViewModel = FriendViewModel(friendRepository, application)
        friendViewModel.isValidToAdd().observeForever{}

        Assert.assertEquals(friendViewModel.isValidToAdd().value!!.peekContent(), FriendFragment.AddResult.FAILED)
    }
}