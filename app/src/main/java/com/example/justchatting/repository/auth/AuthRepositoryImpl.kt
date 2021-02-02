package com.example.justchatting.repository.auth

import android.content.Context
import android.net.Uri
import com.example.justchatting.data.auth.AuthFirebaseSource
import io.reactivex.Completable

class AuthRepositoryImpl(
    private val authFirebaseSource: AuthFirebaseSource,
    val context: Context
) : AuthRepository {

    override fun loginWithEmail(email: String, password: String) = authFirebaseSource.loginWithEmail(email, password)

    override fun signUpWithEmail(email: String, password: String) = authFirebaseSource.signUp(email, password)

    override fun uploadProfile(uri: Uri?) = authFirebaseSource.uploadProfileImage(uri)

    override fun saveUser(
        name: String,
        phoneNumber: String,
        firebaseImageResourcePath: String,
        email: String
    ) = authFirebaseSource.saveUser(name, phoneNumber, firebaseImageResourcePath, email)

    override fun updateToken(): Completable = authFirebaseSource.updateToken()
    override fun saveProfileImageToCache(): Completable = authFirebaseSource.saveProfileImageToCache(context)

}