package com.example.parkingsystem.ui.userInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.base.AdapterClickListener
import com.example.parkingsystem.models.ParkingSpace
import com.example.parkingsystem.models.Reservation
import com.example.parkingsystem.utils.DatesHelper.getTodayDate

class UserReservationsAdapter(private val clickCallback: AdapterClickListener) : RecyclerView.Adapter<UserReservationsAdapter.UserReservationHolder>() {

    private val userReservations: MutableList<Reservation> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReservationHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.row_reservations, parent, false)
        return UserReservationHolder(layout)
    }

    override fun onBindViewHolder(holder: UserReservationHolder, position: Int) {

        val context = holder.parkingSpaceNumber.context

        holder.parkingSpaceNumber.text = context.getString(R.string.parkingSpace, userReservations[position].floor, userReservations[position].space)

        when (userReservations[position].date) {
            getTodayDate() -> {
                holder.day.text = context.getString(R.string.today)
            }
            else -> {
                holder.day.text = context.getString(R.string.tomorrow)
            }
        }

        holder.cancelButton.setOnClickListener {
            clickCallback.onClick(holder.adapterPosition, R.id.cancelReservation)
        }
    }

    override fun getItemCount(): Int {
        return userReservations.size
    }

    fun setData(data: List<Reservation>) {
        userReservations.clear()
        userReservations.addAll(data)
        notifyDataSetChanged()
    }

    fun getElementByPosition(position: Int): Reservation {
        return userReservations[position]
    }

    class UserReservationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parkingSpaceNumber: TextView = itemView.findViewById(R.id.parkingSpace)
        val day: TextView = itemView.findViewById(R.id.day)
        val cancelButton: TextView = itemView.findViewById(R.id.cancelReservation)
    }
}