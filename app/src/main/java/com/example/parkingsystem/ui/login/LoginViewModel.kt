package com.example.parkingsystem.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parkingsystem.base.RepositoryResult
import com.example.parkingsystem.base.Result
import com.example.parkingsystem.base.Result.Error
import com.example.parkingsystem.base.Result.Success
import com.example.parkingsystem.data.ParkingSystemRepository
import com.example.parkingsystem.data.ParkingSystemRepositoryImpl

class LoginViewModel(private val repository: ParkingSystemRepository = ParkingSystemRepositoryImpl()) : ViewModel() {

    private val _viewState: MutableLiveData<LoginViewState> = MutableLiveData(LoginViewState())
    val viewState: LiveData<LoginViewState> = _viewState

    fun doLogin(email: String, password: String) {
        _viewState.value = _viewState.value?.copy(isLoading = true)
        repository.doLogin(email, password, object : RepositoryResult<Unit> {
            override fun result(result: Result<Unit>) {
                when (result) {
                    is Success -> {
                        _viewState.value = _viewState.value?.copy(successLogin = true, isLoading = false)
                    }
                    is Error -> {
                        _viewState.value = _viewState.value?.copy(isLoading = false, error = result.error)
                    }
                }
            }
        })
    }
}

data class LoginViewState(
    val successLogin: Boolean = false,
    val error: String = "",
    val isLoading: Boolean = false
)