package apps.eduraya.e_parking.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.databinding.HomeFragmentBinding
import apps.eduraya.e_parking.rupiah
import apps.eduraya.e_parking.startAnActivity
import apps.eduraya.e_parking.ui.base.BaseFragment
import apps.eduraya.e_parking.ui.deposit.CreateDepositActivity
import apps.eduraya.e_parking.ui.maps.MapsActivity
import apps.eduraya.e_parking.ui.my_qr.MyQrActivity
import apps.eduraya.e_parking.visible
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>(HomeFragmentBinding::inflate) {
    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvListActivity.visible(false)

        binding.cvSearch.setOnClickListener {
            requireActivity().startAnActivity(MapsActivity::class.java)
        }

        binding.btnDeposit.setOnClickListener {
            requireActivity().startAnActivity(CreateDepositActivity::class.java)
        }

//        viewModel.getUserInfoDBObserver().observe(this,object : Observer<List<UserInfo>>{
//            override fun onChanged(t: List<UserInfo>?) {
//                t?.forEach {
//                    binding.username.text = it.name
//                    binding.tvSaldo.text = "Rp. ${it.balance.toString()}"
//                }
//            }
//
//        })

        val userPreferences = UserPreferences(requireContext())
        userPreferences.accessToken.asLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { token ->
            viewModel.setUserInfoResponse("Bearer $token")
            viewModel.setLastParkingResponse("Bearer $token")
        })
        viewModel.getUserInfoResponse.observe(viewLifecycleOwner, Observer {
            binding.progressbar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    lifecycleScope.launch {
                        binding.username.text = it.value.data?.user?.name
                        binding.tvSaldo.text = rupiah(it.value.data?.user?.balance!!.toDouble())
                        Glide.with(requireContext()).load(it.value.data?.user?.avatar).into(binding.imgProfile)
                    }
                }
                is Resource.Failure -> Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.getLastParkingResponse.observe(viewLifecycleOwner, Observer {
            binding.progressbar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.isCheckin("0")
                        viewModel.saveIdLastParking(it.value.data.id.toString())

                        binding.tvNoActivity.visible(false)
                        binding.cvListActivity.visible(true)
                        binding.tvPlace.text = it.value.data.placeName
                        binding.tvVehicle.text = it.value.data.vehicleName
                        binding.tvDate.text = it.value.data.checkIn
                        binding.tvTotalBill.text = rupiah(it.value.data.totalBill.toDouble())
                    }
                }
                is Resource.Failure -> {
                    lifecycleScope.launch {
                        viewModel.isCheckin("1")
                        binding.tvNoActivity.visible(true)
                    }
                }
            }
        })
    }

}