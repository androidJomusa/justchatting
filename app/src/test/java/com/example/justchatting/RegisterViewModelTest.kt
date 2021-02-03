package com.example.justchatting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.justchatting.repository.auth.AuthRepository
import com.example.justchatting.ui.login.RegisterViewModel
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.mockito.Mockito
import org.mockito.Mockito.mock

class RegisterViewModelTest : AutoCloseKoinTest() {

    val mockRepository: AuthRepository by inject()
//    val registerViewModel: RegisterViewModel by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            module{
                factory { mock(AuthRepository::class.java) }
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
    fun 회원가입_성공() {
        //given
        Mockito.`when`(
            mockRepository.signUpWithEmail(
                "tom@gmail.com",
                "123123"
            )
        ).thenReturn(Completable.complete())

        Mockito.`when`(
            mockRepository.uploadProfile(
                null
            )
        ).thenReturn(Single.just(""))

        Mockito.`when`(
            mockRepository.saveUser(
                "tom",
                "010",
                "",
                "tom@gmail.com"
            )
        ).thenReturn(Single.just(true))

        val registerViewModel = RegisterViewModel(mockRepository)

        registerViewModel.email = "tom@gmail.com"
        registerViewModel.name = "tom"
        registerViewModel.password = "123123"
        registerViewModel.phoneNumber = "010"

        //when
        registerViewModel.signUp()

        //then
        assertEquals(registerViewModel.successSignUp.value, true)
    }

    @Test
    fun 회원가입_실패_미입력(){

        val registerViewModel = RegisterViewModel(mockRepository)

        registerViewModel.name = "tom"
        registerViewModel.password = "123123"
        registerViewModel.phoneNumber = "010"

        registerViewModel.signUp()

        assertEquals(registerViewModel.errorToastMessage.value, "please check username, email, password, phone number")
    }

    @Test
    fun 회원가입_GooglSignUp_실패(){
        val registerViewModel = RegisterViewModel(mockRepository)

        registerViewModel.email = "tom@gmail.com"
        registerViewModel.name = "tom"
        registerViewModel.password = "123123"
        registerViewModel.phoneNumber = "010"

        Mockito.`when`(
            mockRepository.signUpWithEmail(
                registerViewModel.email!!,
                registerViewModel.password!!
            )
        ).thenReturn(Completable.error(Throwable("fail message")))

        Mockito.`when`(
            mockRepository.uploadProfile(
                null
            )
        ).thenReturn(Single.just(""))

        Mockito.`when`(
            mockRepository.saveUser(
                registerViewModel.name!!,
                registerViewModel.phoneNumber!!,
                "",
                registerViewModel.email!!
            )
        ).thenReturn(Single.just(true))

        registerViewModel.signUp()

        assertEquals(registerViewModel.errorToastMessage.value,"fail message")
    }
}