package com.example.stajh2test.viewmodel

import androidx.lifecycle.ViewModel

import com.example.stajh2test.viewmodel.Model.ForgotPasswordModel
import com.example.stajh2test.viewmodel.Model.LoginModel
import com.example.stajh2test.viewmodel.Model.NewPasswordModel
import com.example.stajh2test.viewmodel.Model.VerificationModel
import com.example.stajh2test.viewmodel.model.RegisterModel

class AuthViewModel : ViewModel() {
    val registerModel = RegisterModel()
    val loginModel = LoginModel()
    val forgotPasswordModel = ForgotPasswordModel()
    val verificationModel = VerificationModel()
    val newPasswordModel = NewPasswordModel()
}
