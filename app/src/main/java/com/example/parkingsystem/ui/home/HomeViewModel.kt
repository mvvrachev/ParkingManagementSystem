package com.example.parkingsystem.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parkingsystem.base.RepositoryResult
import com.example.parkingsystem.base.Result
import com.example.parkingsystem.base.Result.Error
import com.example.parkingsystem.base.Result.Success
import com.example.parkingsystem.data.ParkingSystemRepository
import com.example.parkingsystem.data.ParkingSystemRepositoryImpl
import com.example.parkingsystem.models.FirebaseParkingSpace
import com.example.parkingsystem.models.ParkingSpace
import com.example.parkingsystem.models.Reservation

class HomeViewModel(private val repository: ParkingSystemRepository = ParkingSystemRepositoryImpl()) : ViewModel() {

    private val _parkingSpaces: MutableLiveData<HomeViewState> = MutableLiveData(HomeViewState())
    val parkingSpaces: LiveData<HomeViewState> = _parkingSpaces

    private val _reservation: MutableLiveData<MakeReservationViewState> = MutableLiveData(MakeReservationViewState())
    val reservation: LiveData<MakeReservationViewState> = _reservation

    private val _carNumber: MutableLiveData<GetUserCarNumber> = MutableLiveData(GetUserCarNumber())
    val carNumber: LiveData<GetUserCarNumber> = _carNumber

    init {
        loadParkingSpaces()
        fetchUserCarNumber()
    }

    fun loadParkingSpaces() {
        _parkingSpaces.value = _parkingSpaces.value?.copy(isLoading = true, error = "")
        repository.loadParkingSpaces(object : RepositoryResult<List<ParkingSpace>> {
            override fun result(result: Result<List<ParkingSpace>>) {
                when(result) {
                    is Success -> {
                        _parkingSpaces.value = _parkingSpaces.value?.copy(isLoading = false, data = result.data, error = "")
                    }
                    is Error -> {
                        _parkingSpaces.value = _parkingSpaces.value?.copy(isLoading = false, error = result.error)
                    }
                }
            }
        })
    }

    fun makeReservation(id: Long, floor: Long, date: String, carNumber: String) {
        _reservation.value = _reservation.value?.copy(isLoading = true, error = "")
        repository.makeReservation(id, floor, date, carNumber, object : RepositoryResult<Unit> {
            override fun result(result: Result<Unit>) {
                when(result) {
                    is Success -> {
                        _reservation.value = _reservation.value?.copy(isLoading = false, successMakeReservation = true, error = "")
                    }
                    is Error -> {
                        _reservation.value = _reservation.value?.copy(isLoading = false, error = result.error)
                    }
                }
            }
        })
    }

    fun fetchUserCarNumber() {
        _carNumber.value = _carNumber.value?.copy(isLoading = true, error = "")
        repository.fetchUserCarNumber(object : RepositoryResult<String> {
            override fun result(result: Result<String>) {
                when(result) {
                    is Success -> {
                        _carNumber.value = _carNumber.value?.copy(isLoading = false, carNumber = result.data, error = "")
                    }
                    is Error -> {
                        _carNumber.value = _carNumber.value?.copy(isLoading = false, error = result.error)
                    }
                }
            }
        })
    }

}

data class HomeViewState(
    val data: List<ParkingSpace> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)

data class MakeReservationViewState(
    val successMakeReservation: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)

data class GetUserCarNumber(
    val carNumber: String = "",
    val isLoading: Boolean = false,
    val error: String = ""
)

