package apps.eduraya.e_parking.ui.scan_qr

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.databinding.ActivityScanQrBinding
import apps.eduraya.e_parking.handleApiError
import apps.eduraya.e_parking.handleApiErrorActivity
import apps.eduraya.e_parking.ui.deposit.PaymentWebViewActivity
import apps.eduraya.e_parking.ui.home.HomeActivity
import apps.eduraya.e_parking.visible
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScanQrActivity : AppCompatActivity() {
    private val viewModel by viewModels<ScanQrViewModel>()
    private lateinit var binding: ActivityScanQrBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var alertBuilder: AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.progressbar.visible(false)

        setupPermissions()
        val userPreferences = UserPreferences(this)

        userPreferences.isCheckin.asLiveData().observe(this, Observer {
            if (it == "1"){
                codeScannerEnterParking()
            }else if(it == "0"){
                codeScannerExitParking()
            }
        })

        alertBuilder = AlertDialog.Builder(this)
    }

    private fun codeScannerEnterParking() {

        codeScanner = CodeScanner(this, binding.scn)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {result ->
                runOnUiThread {
                    doEnterParking(result.text.toString())
//                    binding.tvText.text = result.text.toString()
//                    binding.buttonEnter.visible(true)
                }

                codeScanner.releaseResources()
//                binding.buttonEnter.setOnClickListener {
//                    doEnterParking(result.text.toString())
//                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "codeScanner: ${it.message}")
                }
            }

            binding.scn.setOnClickListener {
                codeScanner.startPreview()
            }
        }

    }

    private fun doEnterParking(gateId:String){
        val userPreferences = UserPreferences(this)
        userPreferences.accessToken.asLiveData().observe(this, Observer { token->
            userPreferences.isInsuranceRequest.asLiveData().observe(this, Observer { isInsurance ->
                viewModel.setRequestEnterParking("Bearer $token", gateId, isInsurance!!)
            })
        })
        viewModel.getRequestEnterParking.observe(this, Observer {
            binding.progressbar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    lifecycleScope.launch {
                        alertBuilder.setTitle("Scan Selesai")
                            .setMessage("Mohon tunggu hingga gate terbuka")
                            .setCancelable(true)
                            .setPositiveButton("OK"){dialogInterface, it ->
                                startActivity(Intent(this@ScanQrActivity, HomeActivity::class.java))
                                finishAffinity()
                            }
                            .setNegativeButton("Ulangi"){dialogInterface, it ->
                                dialogInterface.dismiss()
                            }
                            .show()
//                        Toast.makeText(applicationContext, "Check-in sukses, mohon menunggu hingga gate terbuka", Toast.LENGTH_SHORT).show()

                    }
                }
                is Resource.Failure -> handleApiErrorActivity(it)
            }
        })

    }


    private fun codeScannerExitParking() {

        codeScanner = CodeScanner(this, binding.scn)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {result ->
                runOnUiThread {
                    doExitParking(result.text.toString())
//                    binding.tvText.text = result.text.toString()
//                    binding.buttonEnter.visible(true)
                }

                codeScanner.releaseResources()
//                binding.buttonEnter.setOnClickListener {
//                    doEnterParking(result.text.toString())
//                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "codeScanner: ${it.message}")
                }
            }

            binding.scn.setOnClickListener {
                codeScanner.startPreview()
            }
        }

    }

    private fun doExitParking(officerId:String){
        val userPreferences = UserPreferences(this)
        userPreferences.accessToken.asLiveData().observe(this, Observer { token->
            viewModel.setRequestExitParking("Bearer $token", officerId)
//            userPreferences.isInsuranceRequest.asLiveData().observe(this, Observer { isInsurance ->
//                viewModel.setRequestEnterParking("Bearer $token", gateId, isInsurance!!)
//            })
        })
        viewModel.getRequestExitParking.observe(this, Observer {
            binding.progressbar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    lifecycleScope.launch {
                        alertBuilder.setTitle("Scan Selesai")
                            .setMessage("Mohon tunggu hingga gate terbuka")
                            .setCancelable(true)
                            .setPositiveButton("OK"){dialogInterface, it ->
                                startActivity(Intent(this@ScanQrActivity, HomeActivity::class.java))
                                finishAffinity()
                            }
                            .setNegativeButton("Ulangi"){dialogInterface, it ->
                                dialogInterface.dismiss()
                            }
                            .show()
//                        Toast.makeText(applicationContext, "Keluar parkir sukses, mohon menunggu hingga gate terbuka", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@ScanQrActivity, HomeActivity::class.java))
//                        finishAffinity()
                    }
                }
                is Resource.Failure -> handleApiErrorActivity(it)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQ
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQ -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val CAMERA_REQ = 101
    }
}