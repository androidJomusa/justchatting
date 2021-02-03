package com.example.justchatting.di

import com.example.justchatting.data.auth.AuthFirebaseSource
import com.example.justchatting.repository.auth.AuthRepository
import com.example.justchatting.repository.auth.AuthRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val loginModule = module {
    single { AuthFirebaseSource() }
    factory<AuthRepository> { AuthRepositoryImpl(get(), androidContext()) }
}