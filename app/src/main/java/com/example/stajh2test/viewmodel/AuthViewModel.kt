package com.example.stajh2test.viewmodel

import androidx.lifecycle.ViewModel

import com.example.stajh2test.viewmodel.Model.LoginModel
import com.example.stajh2test.viewmodel.Model.RegisterModel


class AuthViewModel : ViewModel() {
    val registerModel = RegisterModel()
    val loginModel = LoginModel()
}

