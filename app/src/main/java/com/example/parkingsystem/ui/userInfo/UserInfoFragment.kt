package com.example.parkingsystem.ui.userInfo

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.parkingsystem.AuthenticationActivity
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.base.AdapterClickListener
import com.example.parkingsystem.base.BaseFragment
import com.example.parkingsystem.base.DialogClickListener
import com.example.parkingsystem.databinding.FragmentUserInfoBinding
import com.example.parkingsystem.models.User
import com.example.parkingsystem.utils.ConfirmCancelReservationDialog
import com.example.parkingsystem.utils.DatesHelper.getTodayDate
import com.example.parkingsystem.utils.viewBinding

class UserInfoFragment : BaseFragment(R.layout.fragment_user_info) {

    private val binding: FragmentUserInfoBinding by viewBinding(FragmentUserInfoBinding::bind)
    private lateinit var viewModel: UserInfoViewModel
    private lateinit var adapter: UserReservationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        adapter = UserReservationsAdapter(object : AdapterClickListener {
            override fun onClick(position: Int, viewId: Int) {
                val reservation = adapter.getElementByPosition(position)
                val dialog = ConfirmCancelReservationDialog(object : DialogClickListener {
                    override fun onClick(viewId: Int, carNumber: String, dialog: DialogFragment) {
                        when(viewId) {
                            R.id.dismissDialog -> {
                                dialog.dismiss()
                            }
                            R.id.confirmDialog -> {
                                viewModel.cancelReservation(reservation)
                                dialog.dismiss()
                            }
                        }
                    }
                })
                dialog.show(parentFragmentManager, TAG)
            }
        })

        viewModel.userInfoViewState.observe(viewLifecycleOwner) {
            loaderVisible(it.isLoading)

            adapter.setData(it.userReservationData)

            with(binding) {
                name.text = it.userData.username
                email.text = it.userData.email
                carNumberInfo.text = getString(R.string.your_car_number, it.userData.carNumber)
                date.text = getString(R.string.today_is, getTodayDate())
            }

            showError(it.error)
        }

        viewModel.cancelReservationViewState.observe(viewLifecycleOwner) {
            loaderVisible(it.isLoading)

            if(it.successCancelReservation) {
                viewModel.loadUserReservations()
            }

             showError(it.error)
        }

        viewModel.logoutViewState.observe(viewLifecycleOwner) {
            loaderVisible(it.isLoading)

            if(it.successLogout) {
                val intent = Intent(requireContext(), AuthenticationActivity::class.java)
                startActivity(intent)
                requireNotNull(activity).finish();
            }

            if(!it.successLogout && !it.isLoading) {
                showError(it.error)
            }
        }

        with(binding) {
            userReservations.adapter = adapter
            userReservations.setHasFixedSize(true)

            logout.setOnClickListener {
                viewModel.doLogout()
            }

            callParking.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(requireContext().getString(R.string.securityPhoneNumber))
                startActivity(intent)
            }
        }
    }
}