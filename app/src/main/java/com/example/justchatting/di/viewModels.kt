package com.example.justchatting.di

import android.os.Bundle
import com.example.justchatting.ui.chatting.ChattingViewModel
import com.example.justchatting.ui.chatting.SelectGroupViewModel
import com.example.justchatting.ui.chattingRoom.ChattingRoomViewModel
import com.example.justchatting.ui.friend.FriendViewModel
import com.example.justchatting.ui.login.LoginViewModel
import com.example.justchatting.ui.login.RegisterViewModel
import com.example.justchatting.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ChattingViewModel() }
    viewModel { SelectGroupViewModel()}
    viewModel { ChattingRoomViewModel(get()) }
    single { FriendViewModel(androidApplication())}
    viewModel { SettingsViewModel(get(), androidApplication()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}