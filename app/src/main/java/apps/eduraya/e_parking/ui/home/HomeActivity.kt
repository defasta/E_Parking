package apps.eduraya.e_parking.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import apps.eduraya.e_parking.R
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.databinding.ActivityHomeBinding
import apps.eduraya.e_parking.ui.insurance.InsuranceActivity
import apps.eduraya.e_parking.ui.maps.MapsActivity
import apps.eduraya.e_parking.ui.my_qr.MyQrActivity
import apps.eduraya.e_parking.ui.scan_qr.ScanQrActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel by viewModels<HomeViewModel>()
    var permissionArrays = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding
    private lateinit var alertBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment )
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.menu.getItem(2).isEnabled = false
        binding.bottomNav.background = null
        getInsuranceInfo()
        val userPreferences = UserPreferences(this)
        binding.fab.setOnClickListener {
            val userPreferences = UserPreferences(this)
            userPreferences.isCheckin.asLiveData().observe(this, Observer {
                if(it == "1"){
                    doCheckin()
                }else if(it == "0"){
                    startActivity(Intent(this@HomeActivity, MyQrActivity::class.java ))
                }
            })
//            userPreferences.accessToken.asLiveData().observe(this, Observer {token->
//                viewModel.setLastParkingResponse("Bearer $token")
//                viewModel.getLastParkingResponse.observe(this, Observer {
//                    when(it){
//                        is Resource.Loading -> {}
//                        is Resource.Success -> {
//                            lifecycleScope.launch {
//                                viewModel.isCheckin("0")
//                                viewModel.saveIdLastParking(it.value.data.id.toString())
//                            }
//                            startActivity(Intent(this@HomeActivity, MyQrActivity::class.java ))
//                        }
//                        is Resource.Failure -> {
//                            doCheckin()
//                        }
//                    }
//                })
//            })
//            doCheckin()
//            userPreferences.isInsurance.asLiveData().observe(this, Observer { isInsurance ->
//                if (isInsurance == "0"){
//
//                }else{
//                    startActivity(Intent(this, MyQrActivity::class.java))
//                }
//            })
        }

        val setPermission = Build.VERSION.SDK_INT
        if (setPermission > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission() && checkIfAlreadyhavePermission2()) {
            } else {
                requestPermissions(permissionArrays, 101)
            }
        }

        alertBuilder = AlertDialog.Builder(this)
    }

    private fun getInsuranceInfo(){
        val userPreferences = UserPreferences(this)
        userPreferences.accessToken.asLiveData().observe(this, Observer { token->
            viewModel.setInsuranceInfoResponse("Bearer $token")
        })
        viewModel.getInsuranceResponse.observe(this, Observer {
            when(it){
                is Resource.Loading ->{}
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.saveInsurancePriceInfo(it.value.data.price.toString())
                        viewModel.saveInsuranceDetailInfo(it.value.data.description)
                    }
                }
                is Resource.Failure -> Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doCheckin(){
        val userPreferences = UserPreferences(this)
        userPreferences.insurancePrice.asLiveData().observe(this, Observer { price ->
            alertBuilder.setTitle("Asuransi Parkir")
                .setMessage("Apakah Anda ingin mengasuransikan parkir Anda? Asuransi sebesar Rp. ${price}")
                .setCancelable(true)
                .setPositiveButton("Ya"){dialogInterface, it ->
                    lifecycleScope.launch {
                        viewModel.isInsurance("-11")
                        viewModel.isInsuranceRequest("1")
//                        viewModel.isCheckin("1")
                    }
                    startActivity(Intent(this@HomeActivity, MyQrActivity::class.java ))
                }
                .setNegativeButton("Tidak"){dialogInterface, it ->
                    lifecycleScope.launch {
                        viewModel.isInsurance("-00")
                        viewModel.isInsuranceRequest("0")
//                        viewModel.isCheckin("1")
                    }
                    startActivity(Intent(this@HomeActivity, MyQrActivity::class.java ))
                }
                .setNeutralButton("Tentang Asuransi"){dialogInterface, it ->
                    startActivity(Intent(this@HomeActivity, InsuranceActivity::class.java ))
                }
                .show()
        })
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfAlreadyhavePermission2(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

}