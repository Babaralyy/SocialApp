package com.codecoy.mvpflycollab.dataconverter

import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserSerializer {
    private val gson = Gson()

    fun serialize(user: UserLoginData): String {
        return gson.toJson(user)
    }

    fun deserialize(serializedUser: String): UserLoginData {
        val type = object : TypeToken<UserLoginData>() {}.type
        return gson.fromJson(serializedUser, type)
    }
}