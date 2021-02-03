package com.example.justchatting

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.repository.friend.FriendRepository
import com.example.justchatting.repository.settings.SettingsRepository
import com.example.justchatting.ui.friend.FriendViewModel
import com.example.justchatting.ui.settings.SettingsViewModel
import io.reactivex.Single
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import java.lang.Exception
import kotlin.text.Typography.times

class SettingsViewModelTest : AutoCloseKoinTest(){
    val settingsRepository: SettingsRepository by inject()

    @Mock
    lateinit var application: Application

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            module{
                factory { Mockito.mock(SettingsRepository::class.java) }
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
    fun 프로필_이미지_변경_성공() {

        val settingsViewModel = SettingsViewModel(settingsRepository, application)
        val uri: Uri = mock(Uri::class.java)
        Mockito.`when`(settingsRepository.uploadProfileImage(uri)).thenReturn(Single.just(""))

        settingsViewModel.uploadProfileImage(uri)
        Mockito.verify(settingsRepository, times(1)).editProfileImageUrl("")
    }

    @Test
    fun 프로필_이미지_변경_실패() {
        val exception = Exception()
        val settingsViewModel = SettingsViewModel(settingsRepository, application)
        val uri: Uri = mock(Uri::class.java)
        Mockito.`when`(settingsRepository.uploadProfileImage(uri)).thenReturn(Single.error(exception))

        settingsViewModel.uploadProfileImage(uri)
        Assert.assertEquals(settingsViewModel.errorToastMessage.value!! , "failed to upload profile image")
    }

    @Test
    fun 프로필_캐시_이미지_불러오기(){
        val bitmap: Bitmap = mock(Bitmap::class.java)
        Mockito.`when`(settingsRepository.loadImage(application)).thenReturn(bitmap)
        val settingsViewModel = SettingsViewModel(settingsRepository, application)
        settingsViewModel.profileImage.observeForever{}
        settingsViewModel.loadMyProfileImage()

        Assert.assertEquals(settingsViewModel.profileImage.value, bitmap)
    }

    @Test
    fun 알림설정_불러오기(){
        Mockito.`when`(settingsRepository.getNotificationConfig()).thenReturn(true)
        val settingsViewModel = SettingsViewModel(settingsRepository, application)
        settingsViewModel.notificationConfig.observeForever{}
        Assert.assertEquals(settingsViewModel.notificationConfig.value, true)
    }
}