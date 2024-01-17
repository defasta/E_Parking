package apps.eduraya.e_parking.ui.auth.forgot_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.data.repository.AppsRepository
import apps.eduraya.e_parking.data.responses.RequestResetPasswordResponse
import apps.eduraya.e_parking.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: AppsRepository,
    savedStateHandle: SavedStateHandle
):BaseViewModel(repository){

    companion object{
        const val KEY_EMAIL = "KEY_EMAIL"
    }

    private val _email = savedStateHandle.getLiveData<String>(KEY_EMAIL)
    val email : LiveData<String>
        get() = _email

    private val _getResetPasswordResponse: MutableLiveData<Resource<RequestResetPasswordResponse>> = MutableLiveData()
    val getResetPasswordResponse: LiveData<Resource<RequestResetPasswordResponse>>
        get() = _getResetPasswordResponse

    fun setResetPassword(email:String, token: String, password: String, passwordC:String) = viewModelScope.launch {
        _getResetPasswordResponse.value = Resource.Loading
        _getResetPasswordResponse.value = repository.resetPassword(email, token, password, passwordC)
    }
}