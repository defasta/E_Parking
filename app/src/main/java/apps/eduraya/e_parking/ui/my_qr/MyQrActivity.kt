package apps.eduraya.e_parking.ui.my_qr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import apps.eduraya.e_parking.data.responses.UserInfo
import apps.eduraya.e_parking.databinding.ActivityMyQrBinding
import apps.eduraya.e_parking.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Bitmap
import android.graphics.Point
import android.view.Display

import androidmads.library.qrgenearator.QRGContents
import android.view.WindowManager
import androidx.lifecycle.asLiveData
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.ui.home.HomeActivity
import apps.eduraya.e_parking.ui.scan_qr.ScanQrActivity

@AndroidEntryPoint
class MyQrActivity : AppCompatActivity() {
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: ActivityMyQrBinding
    private lateinit var qrgEncoder :QRGEncoder
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyQrBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.navBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivCamera.setOnClickListener {
            startActivity(Intent(this@MyQrActivity, ScanQrActivity::class.java ))
        }

        val userPreferences = UserPreferences(this)
        userPreferences.isCheckin.asLiveData().observe(this, Observer {
            if(it == "1"){
                showCheckinQR()
            }else if(it == "0"){
                showExitQR()
            }
        })

        binding.buttonCheckin.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showCheckinQR(){
        val manager = getSystemService(WINDOW_SERVICE) as WindowManager

        // initializing a variable for default display.
        val display: Display = manager.defaultDisplay

        // creating a variable for point which
        // is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)

        // getting width and
        // height of a point
        val width: Int = point.x
        val height: Int = point.y

        // generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        val userPreferences = UserPreferences(this)
        userPreferences.isInsurance.asLiveData().observe(this, Observer { isInsurance->
            viewModel.getUserInfoDBObserver().observe(this,object : Observer<List<UserInfo>> {
                override fun onChanged(t: List<UserInfo>?) {
                    t?.forEach {
                        qrgEncoder = QRGEncoder(it.id.toString()+isInsurance, null, QRGContents.Type.TEXT, dimen)
                        bitmap = qrgEncoder.encodeAsBitmap()
                        binding.ivMyqr.setImageBitmap(bitmap)
                    }
                }

            })
        })
    }

    private fun showExitQR(){
        binding.textView5.text = "Keluar Parkir"
        binding.textView14.text ="Scan QR Code ini untuk keluar parkir"
        val manager = getSystemService(WINDOW_SERVICE) as WindowManager

        // initializing a variable for default display.
        val display: Display = manager.defaultDisplay

        // creating a variable for point which
        // is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)

        // getting width and
        // height of a point
        val width: Int = point.x
        val height: Int = point.y

        // generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        val userPreferences = UserPreferences(this)
        userPreferences.idLastParking.asLiveData().observe(this, Observer {
            qrgEncoder = QRGEncoder(it.toString(), null, QRGContents.Type.TEXT, dimen)
            bitmap = qrgEncoder.encodeAsBitmap()
            binding.ivMyqr.setImageBitmap(bitmap)
        })
//        userPreferences.isInsurance.asLiveData().observe(this, Observer { isInsurance->
//            viewModel.getUserInfoDBObserver().observe(this,object : Observer<List<UserInfo>> {
//                override fun onChanged(t: List<UserInfo>?) {
//                    t?.forEach {
//                        qrgEncoder = QRGEncoder(it.id.toString()+isInsurance, null, QRGContents.Type.TEXT, dimen)
//                        bitmap = qrgEncoder.encodeAsBitmap()
////                        binding.ivMyqr.setImageBitmap(bitmap)
//                    }
//                }
//
//            })
//        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@MyQrActivity, HomeActivity::class.java ))
        finishAffinity()
    }

}