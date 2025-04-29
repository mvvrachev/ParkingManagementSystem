package com.example.parkingsystem.data

import com.example.parkingsystem.base.RepositoryResult
import com.example.parkingsystem.data.datasources.FirebaseRemoteDataSource
import com.example.parkingsystem.models.ParkingSpace
import com.example.parkingsystem.models.Reservation
import com.example.parkingsystem.models.User

class ParkingSystemRepositoryImpl(
    private val firebaseRemoteDataSource: FirebaseRemoteDataSource = FirebaseRemoteDataSource()
) : ParkingSystemRepository {

    override fun doRegister(
        username: String,
        email: String,
        carNumber: String,
        password: String,
        confirmPassword: String,
        callback: RepositoryResult<Unit>
    ) {
        firebaseRemoteDataSource.doRegister(username, email, carNumber, password, confirmPassword, callback)
    }

    override fun doLogin(email: String, password: String, callback: RepositoryResult<Unit>) {
        firebaseRemoteDataSource.doLogin(email, password, callback)
    }

    override fun doLogout(callback: RepositoryResult<Unit>) {
        firebaseRemoteDataSource.doLogout(callback)
    }

    override fun loadParkingSpaces(callback: RepositoryResult<List<ParkingSpace>>) {
        firebaseRemoteDataSource.loadParkingSpaces(callback)
    }

    override fun makeReservation(id: Long, floor: Long, date: String, carNumber: String, callback: RepositoryResult<Unit>) {
        firebaseRemoteDataSource.makeReservation(id, floor, date,carNumber, callback)
    }

    override fun fetchUserCarNumber(callback: RepositoryResult<String>) {
        firebaseRemoteDataSource.fetchUserCarNumber(callback)
    }

    override fun fetchUserInfo(callback: RepositoryResult<User>) {
        firebaseRemoteDataSource.fetchUserInfo(callback)
    }

    override fun loadUserReservations(callback: RepositoryResult<List<Reservation>>) {
        firebaseRemoteDataSource.loadUserReservations(callback)
    }

    override fun cancelReservation(reservation: Reservation, callback: RepositoryResult<Unit>) {
        firebaseRemoteDataSource.cancelReservation(reservation, callback)
    }
}