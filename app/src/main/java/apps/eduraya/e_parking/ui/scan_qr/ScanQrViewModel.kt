package apps.eduraya.e_parking.ui.scan_qr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.data.repository.AppsRepository
import apps.eduraya.e_parking.data.responses.EnterParkingResponse
import apps.eduraya.e_parking.data.responses.exitparking.ExitParkingResponse
import apps.eduraya.e_parking.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ScanQrViewModel @Inject constructor(
    private val repository: AppsRepository
): BaseViewModel(repository){
    private val _getRequestEnterParking: MutableLiveData<Resource<EnterParkingResponse>> = MutableLiveData()
    val getRequestEnterParking: LiveData<Resource<EnterParkingResponse>>
        get() = _getRequestEnterParking

    fun setRequestEnterParking(token: String, gateId: String, isInsurance: String) = viewModelScope.launch{
        _getRequestEnterParking.value = Resource.Loading
        _getRequestEnterParking.value = repository.requestEnterParking(token, gateId, isInsurance)
    }

    private val _getRequestExitParking: MutableLiveData<Resource<ExitParkingResponse>> = MutableLiveData()
    val getRequestExitParking: LiveData<Resource<ExitParkingResponse>>
        get() = _getRequestExitParking

    fun setRequestExitParking(token: String, officerId: String) = viewModelScope.launch{
        _getRequestExitParking.value = Resource.Loading
        _getRequestExitParking.value = repository.requestExitParking(token, officerId)
    }

}