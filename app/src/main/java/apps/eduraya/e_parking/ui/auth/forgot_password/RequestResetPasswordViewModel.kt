package apps.eduraya.e_parking.ui.auth.forgot_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.data.repository.AppsRepository
import apps.eduraya.e_parking.data.responses.RequestResetPasswordResponse
import apps.eduraya.e_parking.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestResetPasswordViewModel @Inject constructor(
    private val repository: AppsRepository
): BaseViewModel(repository){
    private val _getRequestResetPasswordResponse: MutableLiveData<Resource<RequestResetPasswordResponse>> = MutableLiveData()
    val getRequestResetPasswordResponse: LiveData<Resource<RequestResetPasswordResponse>>
        get() = _getRequestResetPasswordResponse

    fun setRequestResetPassword(email:String) = viewModelScope.launch {
        _getRequestResetPasswordResponse.value = Resource.Loading
        _getRequestResetPasswordResponse.value = repository.requestResetPassword(email)
    }
}