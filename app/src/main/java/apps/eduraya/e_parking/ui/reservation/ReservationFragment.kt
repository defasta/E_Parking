package apps.eduraya.e_parking.ui.reservation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import apps.eduraya.e_parking.R
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.data.responses.reservations.DataReservations
import apps.eduraya.e_parking.databinding.FragmentReservationBinding
import apps.eduraya.e_parking.startAnActivity
import apps.eduraya.e_parking.ui.base.BaseFragment
import apps.eduraya.e_parking.ui.my_qr.MyQrActivity
import apps.eduraya.e_parking.ui.reservation.adapter.ReservationAdapter
import apps.eduraya.e_parking.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReservationFragment : BaseFragment<FragmentReservationBinding>(FragmentReservationBinding::inflate) {
    private val viewModel by viewModels<ReservationViewModel>()
    lateinit var reservationAdapter: ReservationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressbar.visible(false)
        binding.tvNo.visible(false)
        getReservationList()
    }

    private fun getReservationList(){
        val userPreferences = UserPreferences(requireContext())
        userPreferences.accessToken.asLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { token ->
            viewModel.setAllReservationResponse("Bearer $token")
            viewModel.getAllReservationResponse.observe(viewLifecycleOwner, Observer {
                binding.progressbar.visible(it is Resource.Loading)
                when(it){
                    is Resource.Success ->
                        lifecycleScope.launch {
                            it.value.data?.data?.sortByDescending { item -> item.createdAt }
                            reservationAdapter = ReservationAdapter(it.value.data?.data)
                            binding.rvListReservation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            binding.rvListReservation.adapter = reservationAdapter
                            binding.rvListReservation.setHasFixedSize(true)
                            reservationAdapter.setOnItemClickCallback(object :ReservationAdapter.OnItemClickCallback{
                                override fun onItemClicked(dataReservation: DataReservations) {
//                                   requireActivity().startAnActivity(MyQrActivity::class.java)
                                }
                            })

                        }
                    is Resource.Failure -> Toast.makeText(context, "Gagal memuat data.",Toast.LENGTH_SHORT).show()
                }
            })
        })
    }


}