package com.example.parkingsystem.models

data class Reservation (
    val carNumber: String? = null,
    val date: String? = null,
    val space: Long? = null,
    val floor: Long? = null,
    val user_id: String? = null
)