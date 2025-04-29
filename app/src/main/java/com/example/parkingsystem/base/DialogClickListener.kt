package com.example.parkingsystem.base

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment

interface DialogClickListener {
    fun onClick(@IdRes viewId: Int, carNumber: String = "", dialog: DialogFragment)
}