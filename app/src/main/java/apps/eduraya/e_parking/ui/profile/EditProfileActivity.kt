package apps.eduraya.e_parking.ui.profile

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import apps.eduraya.e_parking.R
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.databinding.ActivityEditProfileBinding
import apps.eduraya.e_parking.startAnActivity
import apps.eduraya.e_parking.ui.home.HomeActivity
import apps.eduraya.e_parking.visible
import com.bumptech.glide.Glide
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    private val viewModel by viewModels<EditProfileViewModel>()
    private lateinit var binding: ActivityEditProfileBinding
    private var mUri: Uri? = null
    private val OPERATION_CHOOSE_PHOTO = 2
    var imagePath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.progressbar.visible(false)

        binding.navBack.setOnClickListener {
            onBackPressed()
        }

        viewModel.userName.observe(this, Observer {
            binding.editTextTextPersonName.setText(it.toString())
        })

        viewModel.userEmail.observe(this, Observer {
            binding.editTextTextEmailAddress.setText(it.toString())
        })

        viewModel.userPhone.observe(this, Observer {
            binding.editTextTextPhone.setText(it.toString())
        })

        viewModel.userAvatar.observe(this, Observer {
            Glide.with(this).load(it).into(binding.imgProfile)
        })

        binding.btnChangePict.setOnClickListener {
            val checkSelfPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
            else{
                openGallery()
            }
        }

        binding.btnSave.setOnClickListener {
            if(imagePath != null){
                val userPreferences = UserPreferences(this)
                userPreferences.accessToken.asLiveData().observe(this, Observer {
                    val name = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), binding.editTextTextPersonName.text.toString().trim())

                    val email = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(),binding.editTextTextEmailAddress.text.toString().trim())

                    val photoBody = okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), File(imagePath))

                    val avatar = MultipartBody.Part.createFormData("avatar", File(imagePath).name, photoBody)

                    val phone = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(),binding.editTextTextPhone.text.toString().trim())

                    viewModel.setEditProfileResponse("Bearer $it",name, email, avatar, phone)

                })

                viewModel.getEditProfileResponse.observe(this, Observer {
                    binding.progressbar.visible(it is Resource.Loading)
                    when(it){
                        is Resource.Success ->
                            lifecycleScope.launch {
                                startAnActivity(HomeActivity::class.java)
                                finishAffinity()
                            }

                        is Resource.Failure -> Toast.makeText(this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                Toast.makeText(this, "Silakan upload foto terlebih dahulu", Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun openGallery(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>
                                            , grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when(requestCode){
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                    PackageManager.PERMISSION_GRANTED){
                    openGallery()
                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
//            OPERATION_CAPTURE_PHOTO ->
//                if (resultCode == Activity.RESULT_OK) {
//                    val bitmap = BitmapFactory.decodeStream(
//                        getContentResolver().openInputStream(mUri))
//                    mImageView!!.setImageBitmap(bitmap)
//                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitkat(data)
                    }
                }
        }
    }

    private fun show(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(this, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri?.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri?.authority){
                val fileName = getDataColumn(applicationContext, uri, null, null)
                var uriToReturn: String? = null
                if(fileName != null){
                    uriToReturn = Uri.withAppendedPath(Uri.parse(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).absolutePath), fileName).toString()
                }
                //return uriToReturn
                imagePath = uriToReturn
            }
        }
        else if ("content".equals(uri?.scheme, ignoreCase = true)){
            imagePath = getImagePath(uri, null)
        }
        else if ("file".equals(uri?.scheme, ignoreCase = true)){
            imagePath = uri?.path
        }
        renderImage(imagePath)
    }

    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            binding.imgProfile?.setImageBitmap(bitmap)
        }
        else {
            show("ImagePath is null")
        }
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, null, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME) //_display_name
                return cursor.getString(columnIndex) //returns file name
            }
        }catch (ex: Exception){
            Log.e( "PathUtils", "Error getting uri for cursor to read file: ${ex.message}")
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }

    @SuppressLint("Range")
    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

}