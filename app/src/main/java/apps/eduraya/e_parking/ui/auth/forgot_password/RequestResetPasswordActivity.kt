package apps.eduraya.e_parking.ui.auth.forgot_password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import apps.eduraya.e_parking.R
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.databinding.ActivityRequestResetPasswordBinding
import apps.eduraya.e_parking.enable
import apps.eduraya.e_parking.handleApiErrorActivity
import apps.eduraya.e_parking.ui.deposit.PayDepositActivity
import apps.eduraya.e_parking.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RequestResetPasswordActivity : AppCompatActivity() {
    private val viewModel by viewModels<RequestResetPasswordViewModel>()
    private lateinit var binding: ActivityRequestResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.progressbar.visible(false)
        binding.buttonRequestReset.enable(false)

        binding.etEmailAddress.addTextChangedListener {
            binding.buttonRequestReset.enable(it.toString().isNotEmpty())
        }

        binding.buttonRequestReset.setOnClickListener {
            val email = binding.etEmailAddress.text.toString().trim()
            viewModel.setRequestResetPassword(email)
        }

        binding.navBack.setOnClickListener {
            onBackPressed()
        }

        viewModel.getRequestResetPasswordResponse.observe(this, Observer {
            binding.progressbar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success ->{
                    lifecycleScope.launch {
                        Toast.makeText(this@RequestResetPasswordActivity, it.value.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RequestResetPasswordActivity, ResetPasswordActivity::class.java).apply {
                            putExtra("KEY_EMAIL", binding.etEmailAddress.text.toString().trim())
                        })
                        finishAffinity()
                    }
                }
                is Resource.Failure -> handleApiErrorActivity(it)
            }
        })
    }


}