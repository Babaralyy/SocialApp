package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MvpViewModelFactory(private val mvpRepository: MvpRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return UserRegisterViewModel(mvpRepository) as T

        return when {
            modelClass.isAssignableFrom(UserRegisterViewModel::class.java) -> UserRegisterViewModel(mvpRepository) as T
            modelClass.isAssignableFrom(UserLoginViewModel::class.java) -> UserLoginViewModel(mvpRepository) as T
            modelClass.isAssignableFrom(JourneyViewModel::class.java) -> JourneyViewModel(mvpRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

    }
}