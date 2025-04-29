package com.example.parkingsystem.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.parkingsystem.R
import com.example.parkingsystem.base.BaseFragment
import com.example.parkingsystem.base.DialogClickListener
import com.example.parkingsystem.base.AdapterClickListener
import com.example.parkingsystem.databinding.FragmentHomeBinding
import com.example.parkingsystem.utils.ConfirmReservationDialogFragment
import com.example.parkingsystem.utils.DatesHelper.getTodayDate
import com.example.parkingsystem.utils.DatesHelper.getTomorrowDate
import com.example.parkingsystem.utils.getSupportActionBar
import com.example.parkingsystem.utils.viewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ParkingSpacesAdapter
    private lateinit var userCarNumber: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSupportActionBar().show()
        getSupportActionBar().setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        adapter = ParkingSpacesAdapter(object: AdapterClickListener {
            override fun onClick(position: Int, viewId: Int) {
                when(viewId) {
                    R.id.today -> {
                        val parkingSpace = adapter.getElementByPosition(position)
                        val dialog = ConfirmReservationDialogFragment(getTodayDate(), parkingSpace, userCarNumber, object : DialogClickListener {
                            override fun onClick(viewId: Int, carNumber: String, dialog: DialogFragment) {
                                when(viewId) {
                                    R.id.cancel -> {
                                        dialog.dismiss()
                                    }
                                    R.id.confirm -> {
                                        viewModel.makeReservation(parkingSpace.id, parkingSpace.floor, getTodayDate(), carNumber)
                                        dialog.dismiss()
                                    }
                                }
                            }
                        })
                        dialog.show(parentFragmentManager, TAG)
                    }
                    R.id.tomorrow -> {
                        val parkingSpace = adapter.getElementByPosition(position)
                        val dialog = ConfirmReservationDialogFragment(getTomorrowDate(), parkingSpace, userCarNumber, object : DialogClickListener {
                            override fun onClick(viewId: Int, carNumber: String, dialog: DialogFragment) {
                                when(viewId) {
                                    R.id.cancel -> {
                                        dialog.dismiss()
                                    }
                                    R.id.confirm -> {
                                        viewModel.makeReservation(parkingSpace.id, parkingSpace.floor, getTomorrowDate(), carNumber)
                                        dialog.dismiss()
                                    }
                                }
                            }
                        })
                        dialog.show(parentFragmentManager, TAG)
                    }
                }
            }
        })

        viewModel.parkingSpaces.observe(viewLifecycleOwner) {
            binding.refresher.isRefreshing = it.isLoading
            adapter.setData(it.data)
            showError(it.error)
        }

        viewModel.reservation.observe(viewLifecycleOwner) {
            loaderVisible(it.isLoading)

            if(it.successMakeReservation) {
                viewModel.loadParkingSpaces()
            }

            if(!it.successMakeReservation && !it.isLoading) {
                showError(it.error)
            }
        }

        viewModel.carNumber.observe(viewLifecycleOwner) {
            loaderVisible(it.isLoading)
            userCarNumber = it.carNumber
            showError(it.error)
        }

        with(binding) {
            // if RV blinks disable animation
            parkingSpaces.adapter = adapter
            parkingSpaces.setHasFixedSize(true)
            refresher.setOnRefreshListener {
                viewModel.loadParkingSpaces()
            }

            callSecurityGuard.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(requireContext().getString(R.string.securityPhoneNumber))
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.userInfo -> {
                findNavController().navigate(R.id.action_homeFragment_to_userInfoFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
