package com.example.justchatting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.justchatting.repository.auth.AuthRepository
import com.example.justchatting.ui.login.LoginViewModel
import io.reactivex.Completable
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.mockito.Mockito

class LoginTest : AutoCloseKoinTest() {

    val mockRepository: AuthRepository by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            module {
                factory { Mockito.mock(AuthRepository::class.java) }
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
    fun 로그인_성공() {
        val loginViewModel = LoginViewModel(mockRepository)

        loginViewModel.email = "tom@gmail.com"
        loginViewModel.password = "123123"

        Mockito.`when`(
            mockRepository.loginWithEmail(loginViewModel.email!!, loginViewModel.password!!)
        ).thenReturn(Completable.complete())

        Mockito.`when`(
            mockRepository.updateToken()
        ).thenReturn(Completable.complete())

        Mockito.`when`(
            mockRepository.saveProfileImageToCache()
        ).thenReturn(Completable.complete())

        loginViewModel.login()

        assertEquals(loginViewModel.successLogin.value, true)
    }

    @Test
    fun 로그인_실패() {
        val loginViewModel = LoginViewModel(mockRepository)

        loginViewModel.email = "tom@gmail.com"
        loginViewModel.password = "123123"

        Mockito.`when`(
            mockRepository.loginWithEmail(loginViewModel.email!!, loginViewModel.password!!)
        ).thenReturn(Completable.error(Throwable("authentication failed")))

        Mockito.`when`(
            mockRepository.updateToken()
        ).thenReturn(Completable.complete())

        Mockito.`when`(
            mockRepository.saveProfileImageToCache()
        ).thenReturn(Completable.complete())

        loginViewModel.login()

        assertEquals(loginViewModel.errorToastMessage.value, "authentication failed")
    }

    @Test
    fun 로그인_실패_미입력(){
        val loginViewModel = LoginViewModel(mockRepository)

        loginViewModel.email = ""
        loginViewModel.password = "123123"

        loginViewModel.login()

        assertEquals(loginViewModel.errorToastMessage.value, "please enter your email and password")
    }
}