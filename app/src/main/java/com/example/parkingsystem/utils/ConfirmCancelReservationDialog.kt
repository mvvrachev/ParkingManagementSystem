package com.example.parkingsystem.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.parkingsystem.R
import com.example.parkingsystem.base.DialogClickListener

class ConfirmCancelReservationDialog(private val clickCallback: DialogClickListener) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.cancel_reservation, container, false)

        val cancelButton: Button = view.findViewById(R.id.dismissDialog)
        val confirmButton: Button = view.findViewById(R.id.confirmDialog)

        cancelButton.setOnClickListener {
            clickCallback.onClick(R.id.dismissDialog, dialog = this)
        }
        confirmButton.setOnClickListener {
            clickCallback.onClick(R.id.confirmDialog, dialog = this)
            dismiss()
        }

        return view
    }
}