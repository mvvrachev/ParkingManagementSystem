package com.example.parkingsystem.models

data class ParkingSpace (
    val id: Long,
    val floor: Long,
    var isBookedToday: Boolean,
    var isBookedTomorrow: Boolean
)