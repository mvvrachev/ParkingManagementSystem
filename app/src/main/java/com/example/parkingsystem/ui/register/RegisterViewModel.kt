package com.example.parkingsystem.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parkingsystem.base.RepositoryResult
import com.example.parkingsystem.base.Result
import com.example.parkingsystem.base.Result.Error
import com.example.parkingsystem.base.Result.Success
import com.example.parkingsystem.data.ParkingSystemRepository
import com.example.parkingsystem.data.ParkingSystemRepositoryImpl


class RegisterViewModel(private val repository: ParkingSystemRepository = ParkingSystemRepositoryImpl()) : ViewModel() {

    private val _viewState: MutableLiveData<RegisterViewState> = MutableLiveData(RegisterViewState())
    val viewState: LiveData<RegisterViewState> = _viewState

    fun doRegister(username: String, email: String, carNumber: String, password: String, confirmPassword: String) {
        _viewState.value = _viewState.value?.copy(isLoading = true)
        repository.doRegister(username, email, carNumber, password, confirmPassword, object : RepositoryResult<Unit> {
            override fun result(result: Result<Unit>) {
                when (result) {
                    is Success -> {
                        _viewState.value = _viewState.value?.copy(successRegister = true, isLoading = false)
                    }
                    is Error -> {
                        _viewState.value = _viewState.value?.copy(isLoading = false, error = result.error)
                    }
                }
            }
        })
    }
}

data class RegisterViewState(
    val successRegister: Boolean = false,
    val error: String = "",
    val isLoading: Boolean = false
)