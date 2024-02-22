package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MvpViewModelFactory(private val mvpRepository: MvpRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserRegisterViewModel::class.java) -> UserRegisterViewModel(
                mvpRepository
            )

            modelClass.isAssignableFrom(UserLoginViewModel::class.java) -> UserLoginViewModel(
                mvpRepository
            )

            modelClass.isAssignableFrom(JourneyViewModel::class.java) -> JourneyViewModel(
                mvpRepository
            )

            modelClass.isAssignableFrom(PlaylistViewModel::class.java) -> PlaylistViewModel(
                mvpRepository
            )
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}