package com.example.parkingsystem.base

import androidx.annotation.IdRes

interface AdapterClickListener {
    fun onClick(position: Int, @IdRes viewId: Int)
}