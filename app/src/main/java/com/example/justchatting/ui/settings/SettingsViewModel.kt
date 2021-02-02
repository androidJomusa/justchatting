package com.example.justchatting.ui.settings

import android.R
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justchatting.repository.settings.SettingsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class SettingsViewModel(private val settingsRepository: SettingsRepository, application: Application) : AndroidViewModel(application){
    val disposable = CompositeDisposable()
    var profileImage: MutableLiveData<Bitmap?> = MutableLiveData()
    val notificationConfig: MutableLiveData<Boolean> = MutableLiveData()
    val errorToastMessage = MutableLiveData<String>()

    init {
        notificationConfig.value = settingsRepository.getNotificationConfig()
    }
    fun setNotificationConfig(boolean: Boolean) {
        settingsRepository.setNotificationConfig(boolean)
    }

    fun loadMyProfileImage() {
        profileImage.value = settingsRepository.loadImage(getApplication())
    }

    fun saveProfileImageToCache() {
        settingsRepository.saveProfileImageToCache(getApplication<Application>().applicationContext, profileImage.value!!)
    }

    fun uploadProfileImage(data: Uri?) {
        disposable.add(settingsRepository.uploadProfileImage(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                settingsRepository.editProfileImageUrl(it)
            }, {
                errorToastMessage.value = "failed to upload profile image"
            })
        )
    }
}