package com.example.parkingsystem.data.datasources

import android.content.ContentValues.TAG
import android.util.Log
import android.util.Patterns
import com.example.parkingsystem.base.RepositoryResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.parkingsystem.base.Result
import com.example.parkingsystem.models.*
import com.example.parkingsystem.utils.DatesHelper.getTodayDate
import com.example.parkingsystem.utils.DatesHelper.getTomorrowDate
import com.example.parkingsystem.utils.EmailSender
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import java.lang.StringBuilder

class FirebaseRemoteDataSource {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun doRegister(username: String, email: String, carNumber: String, password: String, confirmPassword: String, repositoryResult: RepositoryResult<Unit>) {
        if(username.isEmpty() || email.isEmpty() || carNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            repositoryResult.result(Result.Error("Fields must not be empty!"))
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            repositoryResult.result(Result.Error("Invalid email address!"))
        }
        else if(email.split("@")[1] != "tu-sofia.bg") {
            repositoryResult.result(Result.Error("Not a TU-Sofia email address!"))
        }
        else if(password.length < 8) {
            repositoryResult.result(Result.Error("Password must be at least 8 characters!"))
        }
        else if(password != confirmPassword) {
            repositoryResult.result(Result.Error("Passwords do not match!"))
        }
        else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        addAdditionalUserInfo(username, carNumber, requireNotNull(auth.currentUser))
                        repositoryResult.result(Result.Success(Unit))
                    } else {
                        repositoryResult.result(Result.Error(task.exception.toString().split(" ", limit = 2)[1]))
                    }
                }
        }
    }

    private fun addAdditionalUserInfo(username: String, carNumber: String, user: FirebaseUser) {
        val values = hashMapOf("username" to username, "carNumber" to carNumber)
        db.collection("user-profiles").document(user.uid).set(values)
    }

    fun doLogin(email: String, password: String, repositoryResult: RepositoryResult<Unit>) {
        if(email.isEmpty() || password.isEmpty()) {
            repositoryResult.result(Result.Error("Please fill in the fields!"))
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        repositoryResult.result(Result.Success(Unit))
                    } else {
                        repositoryResult.result(Result.Error(task.exception.toString().split(" ", limit = 2)[1]))
                    }
                }
        }
    }

    fun doLogout(repositoryResult: RepositoryResult<Unit>) {
        try {
            auth.signOut()
            repositoryResult.result(Result.Success(Unit))
        } catch (ex: Exception) {
            repositoryResult.result(Result.Error("Could not log you out. Please try again!"))
        }
    }

    fun loadParkingSpaces(repositoryResult: RepositoryResult<List<ParkingSpace>>) {
        val firebaseParkingSpaces = mutableListOf<FirebaseParkingSpace>()
        val reservations = mutableListOf<Reservation>()
        val parkingSpacesWithReservations = mutableListOf<ParkingSpace>()

        db.collection("parking-spaces").get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    firebaseParkingSpaces.add(document.toObject())
                }

                db.collection("reservations")
                    .whereIn("date", listOf(getTodayDate(), getTomorrowDate())).get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            reservations.add(document.toObject())
                        }

                        for (ps in firebaseParkingSpaces) {
                            parkingSpacesWithReservations.add(
                                ParkingSpace(
                                    requireNotNull(ps.id), requireNotNull(ps.floor),
                                    isBookedToday = false,
                                    isBookedTomorrow = false
                                )
                            )
                        }

                        for (ps in parkingSpacesWithReservations) {
                            for (reservation in reservations) {
                                if(ps.id == reservation.space) {
                                    when (reservation.date) {
                                        getTodayDate() -> {
                                            ps.isBookedToday = true
                                        }
                                        getTomorrowDate() -> {
                                            ps.isBookedTomorrow = true
                                        }
                                    }
                                }
                            }
                        }
                        repositoryResult.result(Result.Success(parkingSpacesWithReservations))
                    }
                    .addOnFailureListener { exception ->
                        repositoryResult.result(Result.Error("Could not fetch parking spaces. Swipe down to refresh!"))
                        Log.e("FirestoreError", "Error fetching parking spaces", exception)
                    }
            }
            .addOnFailureListener { exception ->
                repositoryResult.result(Result.Error("Could not fetch parking spaces. Swipe down to refresh!"))
                Log.e("FirestoreError", "Error fetching parking spaces", exception)
            }
    }

    fun makeReservation(id: Long, floor: Long, date: String, carNumber: String, repositoryResult: RepositoryResult<Unit>) {
        val reservations = db.collection("reservations")
        val reservation = Reservation(carNumber, date, id, floor, requireNotNull(auth.currentUser).uid)
        reservations.add(reservation)
            .addOnSuccessListener {
                repositoryResult.result(Result.Success(Unit))
                if(date == getTodayDate()) {
                    sendEmail()
                }
            }
            .addOnFailureListener { e ->
                repositoryResult.result(Result.Error("Error making reservation. Please try again!"))
                Log.e("FirestoreError", "Error fetching parking spaces", e)

            }

    }

    fun fetchUserCarNumber(repositoryResult: RepositoryResult<String>) {
        val currentUser = auth.currentUser
        db.collection("user-profiles").document(requireNotNull(currentUser).uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val u = documentSnapshot.toObject<UserInfo>()
                val carNumber = requireNotNull(u).carNumber
                if(carNumber == null) {
                    repositoryResult.result(Result.Error("Unexpected error occurred! Try again!"))
                }
                else {
                    repositoryResult.result(Result.Success(carNumber))
                }
            }
            .addOnFailureListener {
                repositoryResult.result(Result.Error("Unexpected error occurred! Try again!"))
            }
    }

    fun fetchUserInfo(repositoryResult: RepositoryResult<User>) {
        val currentUser = auth.currentUser
        val db = Firebase.firestore.collection("user-profiles").document(requireNotNull(currentUser).uid.toString())
        db.get()
            .addOnSuccessListener { documentSnapshot ->
                val u = documentSnapshot.toObject<UserInfo>()
                val user = User(currentUser.email, requireNotNull(u).carNumber, u.username)
                repositoryResult.result(Result.Success(user))
        }
            .addOnFailureListener {
                repositoryResult.result(Result.Error("Could not load user data!"))
            }
    }

    fun loadUserReservations(repositoryResult: RepositoryResult<List<Reservation>>) {
        val userReservations: MutableList<Reservation> = mutableListOf()
        val currentUserUid = requireNotNull(auth.currentUser).uid
        db.collection("reservations").whereEqualTo("user_id", currentUserUid)
            .whereIn("date", listOf(getTodayDate(), getTomorrowDate()))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userReservations.add(document.toObject())
                }
                repositoryResult.result(Result.Success(userReservations))
            }
            .addOnFailureListener {
                repositoryResult.result(Result.Error("Could not load your reservations."))
            }
    }

    fun cancelReservation(reservation: Reservation, repositoryResult: RepositoryResult<Unit>) {
        db.collection("reservations").whereEqualTo("date", reservation.date)
            .whereEqualTo("carNumber", reservation.carNumber)
            .whereEqualTo("floor", reservation.floor)
            .whereEqualTo("space", reservation.space)
            .whereEqualTo("user_id", reservation.user_id)
            .get()
            .addOnSuccessListener { documents ->
                for(d in documents) {
                    db.collection("reservations").document(d.id).delete()
                        .addOnSuccessListener {

                            if(reservation.date == getTodayDate()) {
                                sendEmail()
                            }

                            repositoryResult.result(Result.Success(Unit))
                        }
                        .addOnFailureListener {
                            repositoryResult.result(Result.Error("Could not cancel the reservation. Please try again!"))
                        }
                }
            }
            .addOnFailureListener {
                repositoryResult.result(Result.Error("Could not cancel the reservation. Please try again!"))
            }
    }

    private fun sendEmail() {
        db.collection("reservations").whereEqualTo("date", getTodayDate())
            .get()
            .addOnSuccessListener { documents ->
                var reservations = StringBuilder("Reservations for today: \n\n")
                for (document in documents) {
                    reservations.append("Space: ").append(document.data["space"]).append("\n")
                            .append("Floor: ").append(document.data["floor"]).append("\n")
                            .append("Car Number: ").append(document.data["carNumber"]).append("\n")
                            .append("\n");
                }
                val emailSender = EmailSender(reservations)
                emailSender.execute()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting reservations: ", exception)
            }
    }
}