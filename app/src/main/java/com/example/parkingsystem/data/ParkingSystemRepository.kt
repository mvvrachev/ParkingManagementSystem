package com.example.parkingsystem.data

import com.example.parkingsystem.base.RepositoryResult
import com.example.parkingsystem.models.ParkingSpace
import com.example.parkingsystem.models.Reservation
import com.example.parkingsystem.models.User

interface ParkingSystemRepository {

    fun doRegister(username: String,
                   email: String,
                   carNumber: String,
                   password: String,
                   confirmPassword: String,
                   callback: RepositoryResult<Unit>)
    fun doLogin(email: String, password: String, callback: RepositoryResult<Unit>)
    fun doLogout(callback: RepositoryResult<Unit>)

    fun loadParkingSpaces(callback: RepositoryResult<List<ParkingSpace>>)
    fun makeReservation(id: Long, floor: Long, date: String, carNumber: String, callback: RepositoryResult<Unit>)
    fun fetchUserCarNumber(callback: RepositoryResult<String>)
    fun fetchUserInfo(callback: RepositoryResult<User>)
    fun loadUserReservations(callback: RepositoryResult<List<Reservation>>)
    fun cancelReservation(reservation: Reservation, callback: RepositoryResult<Unit>)
}