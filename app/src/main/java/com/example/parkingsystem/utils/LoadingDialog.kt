package com.example.parkingsystem.utils

import android.app.AlertDialog
import android.content.Context
import com.example.parkingsystem.R

class LoadingDialog(context: Context) {

    private val dialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.loader)
            .setCancelable(false)
        dialog = builder.create()
    }

    fun show() {
        dialog.show()
    }

    fun hide() {
        dialog.dismiss()
    }
}