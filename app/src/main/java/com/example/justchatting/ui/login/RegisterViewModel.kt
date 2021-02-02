package com.example.justchatting.ui.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justchatting.repository.auth.AuthRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    var email: String? = null
    var password: String? = null
    var name: String? = null
    var phoneNumber: String? = null
    var selectedPhotoUri: Uri? = null

    private val _successSignUp = MutableLiveData<Boolean>()
    val successSignUp: LiveData<Boolean>
        get() = _successSignUp

    private val _errorToastMessage = MutableLiveData<String>()
    val errorToastMessage : LiveData<String>
        get()  = _errorToastMessage

    fun signUp() {
        if (name.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty() || phoneNumber.isNullOrEmpty()) {
            _errorToastMessage.value = "please check username, email, password, phone number"
            return
        }

        val uploadImage: Single<String> = repository.uploadProfile(selectedPhotoUri)
        val signUpWithEmail: Completable = repository.signUpWithEmail(email!!, password!!)

        disposables.add(signUpWithEmail
            .andThen(uploadImage
                .flatMap { imagePath ->
                    repository.saveUser(name!!, phoneNumber!!, imagePath, email!!)
                })
            .doOnSuccess {
                _successSignUp.value = true
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _successSignUp.value = true
            }, {
                _errorToastMessage.value = it.message
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}