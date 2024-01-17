package apps.eduraya.e_parking.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.eduraya.e_parking.data.network.Resource
import apps.eduraya.e_parking.data.repository.AppsRepository
import apps.eduraya.e_parking.data.responses.UserInfo
import apps.eduraya.e_parking.data.responses.insurance.GetInsuranceResponse
import apps.eduraya.e_parking.data.responses.lastparking.GetLastParkingResponse
import apps.eduraya.e_parking.data.responses.user.GetDataUserResponse
import apps.eduraya.e_parking.data.responses.user.UserData
import apps.eduraya.e_parking.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppsRepository
):BaseViewModel(repository){

    var userInfoDB: MutableLiveData<List<UserInfo>> = MutableLiveData()

    init {
        loadUserInfoDB()
    }

    fun loadUserInfoDB(){
        val list = repository.getUserInfoDB()
        userInfoDB.postValue(list)
    }

    fun getUserInfoDBObserver(): MutableLiveData<List<UserInfo>>{
        return userInfoDB
    }

    private val _getUserInfoResponse: MutableLiveData<Resource<GetDataUserResponse>> = MutableLiveData()
    val getUserInfoResponse: LiveData<Resource<GetDataUserResponse>>
        get() =  _getUserInfoResponse

    fun setUserInfoResponse(token: String) = viewModelScope.launch{
        _getUserInfoResponse.value = Resource.Loading
        _getUserInfoResponse.value = repository.getUserData(token)
    }

    private val _getInsuranceInfoResponse: MutableLiveData<Resource<GetInsuranceResponse>> = MutableLiveData()
    val getInsuranceResponse: LiveData<Resource<GetInsuranceResponse>>
        get() = _getInsuranceInfoResponse

    fun setInsuranceInfoResponse(token: String) = viewModelScope.launch {
        _getInsuranceInfoResponse.value = Resource.Loading
        _getInsuranceInfoResponse.value = repository.getInsurance(token)
    }

    private val _getLastParkingResponse: MutableLiveData<Resource<GetLastParkingResponse>> = MutableLiveData()
    val getLastParkingResponse:LiveData<Resource<GetLastParkingResponse>>
        get() = _getLastParkingResponse

    fun setLastParkingResponse(token: String) = viewModelScope.launch {
        _getLastParkingResponse.value = Resource.Loading
        _getLastParkingResponse.value = repository.getLastParking(token)
    }

    suspend fun saveInsurancePriceInfo(insuranceInfo: String){
        repository.saveInsurancePriceInfo(insuranceInfo)
    }

    suspend fun saveInsuranceDetailInfo(insuranceDetail: String){
        repository.saveInsuranceDetailInfo(insuranceDetail)
    }

    suspend fun saveIdLastParking(id:String){
        repository.saveIdLastParking(id)
    }

    suspend fun isInsurance(isInsurance:String){
        repository.isInsurance(isInsurance)
    }

    suspend fun isInsuranceRequest(isInsurance:String){
        repository.isInsuranceRequest(isInsurance)
    }

    suspend fun isCheckin(isCheckin:String){
        repository.isCheckin(isCheckin)
    }

    suspend fun logoutAccount(){
        repository.logoutAccount()
    }
}