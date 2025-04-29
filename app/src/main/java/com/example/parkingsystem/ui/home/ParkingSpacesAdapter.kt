package com.example.parkingsystem.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.base.AdapterClickListener
import com.example.parkingsystem.models.ParkingSpace
import com.example.parkingsystem.utils.DatesHelper.getTodayDate
import com.example.parkingsystem.utils.DatesHelper.getTomorrowDate
import com.google.android.material.chip.Chip

class ParkingSpacesAdapter(private val clickCallback: AdapterClickListener) : RecyclerView.Adapter<ParkingSpacesAdapter.ParkingHolder>() {

    private val parkingSpaces : MutableList<ParkingSpace> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.row_parking_space, parent, false)
        return ParkingHolder(layout)
    }

    override fun onBindViewHolder(holder: ParkingHolder, position: Int) {
        val context = holder.parkingSpaceNumber.context

        holder.parkingSpaceNumber.text = context.getString(R.string.parkingSpace, parkingSpaces[position].floor, parkingSpaces[position].id)

        holder.today.text = getTodayDate()
        holder.today.isEnabled = !parkingSpaces[position].isBookedToday

        holder.tomorrow.text = getTomorrowDate()
        holder.tomorrow.isEnabled = !parkingSpaces[position].isBookedTomorrow

        holder.today.setOnClickListener {
            clickCallback.onClick(holder.adapterPosition, R.id.today)
        }

        holder.tomorrow.setOnClickListener {
            clickCallback.onClick(holder.adapterPosition, R.id.tomorrow)
        }
    }

    override fun getItemCount(): Int {
        return parkingSpaces.size
    }

    fun setData(data: List<ParkingSpace>) {
        parkingSpaces.clear()
        parkingSpaces.addAll(data)
        notifyDataSetChanged()
    }

    fun getElementByPosition(position: Int): ParkingSpace {
        return parkingSpaces[position]
    }

    class ParkingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parkingSpaceNumber: TextView = itemView.findViewById(R.id.parkingSpaceNumber)
        val today: Chip = itemView.findViewById(R.id.today)
        val tomorrow: Chip = itemView.findViewById(R.id.tomorrow)
    }

}