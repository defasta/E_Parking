package apps.eduraya.e_parking.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import apps.eduraya.e_parking.R
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.data.responses.ListDataPlace
import apps.eduraya.e_parking.databinding.ActivityMapsBinding
import apps.eduraya.e_parking.ui.home.HomeActivity
import apps.eduraya.e_parking.ui.maps.adapter.MapsAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import im.delight.android.location.SimpleLocation
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    var permissionArrays = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    lateinit var mapsView: GoogleMap
    lateinit var simpleLocation: SimpleLocation
    lateinit var progressDialog: ProgressDialog
    lateinit var mapsAdapter: MapsAdapter
    lateinit var strCurrentLocation: String
    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0
    lateinit var currentLatLng: LatLng
    private lateinit var binding: ActivityMapsBinding
    private val placeViewModel by viewModels<PlaceViewModel>()
    private val quotasViewModel by viewModels<QuotasByPlaceViewModel>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 2) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        val setPermission = Build.VERSION.SDK_INT
        if (setPermission > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission() && checkIfAlreadyhavePermission2()) {
            } else {
                requestPermissions(permissionArrays, 101)
            }
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("sedang menampilkan lokasi tempat parkir")

        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        //get location
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        //set location lat long
        strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"

        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        mapsAdapter = MapsAdapter(this)
        binding.rvListLocation.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        binding.rvListLocation.setAdapter(mapsAdapter)
        binding.rvListLocation.setHasFixedSize(true)

    }

    override fun onBackPressed() {
        startActivity(Intent(this, HomeActivity::class.java))
        finishAndRemoveTask()
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

    override fun onMapReady(googleMap: GoogleMap) {
        mapsView = googleMap
        //set text location
        val geocoder = Geocoder(this, Locale.getDefault())
//        try {
//            val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
//            if (addressList != null && addressList.size > 0) {
//                val strCity = addressList[0].locality
//                binding.tvCity.text = strCity
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

        val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
        if (addressList != null && addressList.size > 0) {
            val strCity = addressList[0].locality
            binding.tvCity.text = strCity
        }

        //viewmodel
        getLocationViewModel()
    }

    //get multiple marker
    private fun getLocationViewModel() {
        val userPreferences = UserPreferences(this)
        userPreferences.accessToken.asLiveData().observe(this, androidx.lifecycle.Observer { token ->
            placeViewModel.setPlaceResult("Bearer $token")
            Log.d("TOKENNYA USER", token!!)
            placeViewModel.getPlacesResult.observe(this, androidx.lifecycle.Observer {getPlaceResponse ->
                when(getPlaceResponse){
                    is Resource.Loading -> progressDialog.show()
                    is Resource.Success ->
//                        getPlaceResponse.value.data?.data?.forEach {
//                            quotasViewModel.setQuotasByResult("Bearer $token", it?.id.toString())
//                            quotasViewModel.getQuotasByPlaceResult.observe(this, androidx.lifecycle.Observer {
//                                when(it){
//                                    is Resource.Loading -> progressDialog.show()
//                                    is Resource.Success -> lifecycleScope.launch {
//                                        mapsAdapter.setLocationAdapter(getPlaceResponse.value.data?.data, it.value.data!!)
//                                        getMarker(getPlaceResponse.value.data?.data, it.value.data)
//                                        progressDialog.dismiss()
//                                    }
//                                    is Resource.Failure -> Toast.makeText(this, "Gagal memuat kuota kendaraan", Toast.LENGTH_SHORT).show()
//                                }
//                            })
//                        }
                    lifecycleScope.launch {
                        mapsAdapter.setLocationAdapter(getPlaceResponse.value.data?.data)
                        getMarker(getPlaceResponse.value.data?.data)
                        progressDialog.dismiss()
                    }
                    is Resource.Failure -> Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            })

        })

    }

    @SuppressLint("MissingPermission")
    private fun getMarker(placeResultsArrayList: ArrayList<ListDataPlace?>?) {
        for (i in placeResultsArrayList?.indices!!) {
//            mapsView.isMyLocationEnabled = true

            //set LatLong from API
            val latLngMarker = LatLng(
                placeResultsArrayList!![i]?.lat!!,
                placeResultsArrayList[i]?.lng!!)

            //get LatLong to Marker
            mapsView.addMarker(
                MarkerOptions()
                    .position(latLngMarker)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    .title(quotasResultsByArrayList!![i]?.quotaValet.toString() +" kuota parkir di "+placeResultsArrayList!![i]?.name))
                    .title(placeResultsArrayList!![i]?.name))


            //show Marker
            val latLngResult = LatLng(placeResultsArrayList[0]?.lat!!,
                placeResultsArrayList[0]?.lng!!)

            //set position marker
            mapsView.moveCamera(CameraUpdateFactory.newLatLng(latLngResult))
            mapsView.animateCamera(CameraUpdateFactory
                .newLatLngZoom(LatLng(
                    latLngResult.latitude,
                    latLngResult.longitude), 14f))
            mapsView.uiSettings.setAllGesturesEnabled(true)
            mapsView.uiSettings.isZoomGesturesEnabled = true
        }

        //click marker for change position recyclerview
        mapsView.setOnMarkerClickListener { marker ->
            val markerPosition = marker.position
            mapsView.addMarker(MarkerOptions()
                .position(markerPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

            var markerSelected = -1
            for (i in placeResultsArrayList?.indices!!) {
                if (markerPosition.latitude == placeResultsArrayList[i]?.lat && markerPosition.longitude == placeResultsArrayList[i]?.lng) {
                    markerSelected = i
                }
            }
            val cameraPosition = CameraPosition.Builder().target(markerPosition).zoom(14f).build()
            mapsView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            mapsAdapter.notifyDataSetChanged()
            binding.rvListLocation.smoothScrollToPosition(markerSelected)
            false
        }
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
//            if (requestCode == REQ_PERMISSION && resultCode == RESULT_OK) {
            getLocationViewModel()
        }
    }


}


