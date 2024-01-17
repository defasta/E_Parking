package apps.eduraya.e_parking.data.repository

import apps.eduraya.e_parking.data.db.AppDao
import apps.eduraya.e_parking.data.db.UserPreferences
import apps.eduraya.e_parking.data.network.Api
import apps.eduraya.e_parking.data.responses.DataDeposit
import apps.eduraya.e_parking.data.responses.DataPayDeposit
import apps.eduraya.e_parking.data.responses.UserInfo
import apps.eduraya.e_parking.data.responses.user.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AppsRepository @Inject constructor(
    private val api: Api,
    private val preferences: UserPreferences,
    private val appDao: AppDao
    ): BaseRepository(api){

    suspend fun login(email: String, password:String) = safeApiCall {
        api.login(email, password)
    }

    suspend fun signUp(name: String, email: String, password:String, passwordC:String) = safeApiCall {
        api.signUp(name, email, password, passwordC)
    }

    suspend fun getUserData(token: String) = safeApiCall{
        api.getUserData(token)
    }

    suspend fun changeProfile(tokenAccess: String, name:RequestBody, email:RequestBody, avatar:MultipartBody.Part, phone: RequestBody) = safeApiCall {
        api.changeProfile(tokenAccess, name, email, avatar, phone)
    }

    suspend fun saveAccessTokens(accessToken: String){
        preferences.saveAccessTokens(accessToken)
    }

    suspend fun saveInsurancePriceInfo(insurancePrice: String){
        preferences.saveInsurancePriceInfo(insurancePrice)
    }

    suspend fun saveInsuranceDetailInfo(insuranceDetail: String){
        preferences.saveInsuranceDetailInfo(insuranceDetail)
    }

    suspend fun saveIdLastParking(id:String){
        preferences.saveIdLastParking(id)
    }

    suspend fun isInsurance(isInsurance:String){
        preferences.isInsurance(isInsurance)
    }

    suspend fun isInsuranceRequest(isInsurance:String){
        preferences.isInsuranceRequest(isInsurance)
    }

    suspend fun isCheckin(isCheckin:String){
        preferences.isCheckin(isCheckin)
    }

    suspend fun logoutAccount(){
        preferences.clear()
    }

    suspend fun getPlaces(token: String) = safeApiCall {
        api.getPlaces(token)
    }

    suspend fun getQuotasByPlace(token: String, id:String) = safeApiCall {
        api.getQuotasByPlace(token,id)
    }

    fun getUserInfoDB():List<UserInfo>{
        return appDao.getUserInfo()
    }

    fun insertUserInfoDB(userInfo: UserInfo){
        appDao.insertUserInfo(userInfo)
    }

    suspend fun createDeposit(token: String, nominal: String) = safeApiCall {
        api.createDeposit(token, nominal)
    }

    suspend fun getTokenDeposit(tokenAccess: String, id: String) = safeApiCall{
        api.getTokenDeposit(tokenAccess, id)
    }

    suspend fun saveDepositToken(token: String) {
        preferences.saveAccessTokens(token)
    }

    suspend fun getAllDeposit(tokenAccess: String) = safeApiCall{
        api.getAllDeposit(tokenAccess)
    }

    suspend fun getALlVehicle(tokenAccess:String) = safeApiCall {
        api.getAllDVehicle(tokenAccess)
    }

    suspend fun getValetAreasByPlace(tokenAccess: String, id:String) = safeApiCall {
        api.getValetAreasByPlace(tokenAccess, id)
    }

    suspend fun createReservation(tokenAccess: String, valetAreaId: String, checkIn: String) = safeApiCall {
        api.createReservation(tokenAccess, valetAreaId, checkIn)
    }

    suspend fun getAllReservation(tokenAccess: String) = safeApiCall {
        api.getAllReservation(tokenAccess)
    }

    suspend fun getInsurance(tokenAccess: String) = safeApiCall{
        api.getInsurance(tokenAccess)
    }

    suspend fun getLastParking(tokenAccess: String) = safeApiCall {
        api.getLastParking(tokenAccess)
    }

    suspend fun requestEnterParking(tokenAccess: String, gateId: String, isInsurance: String) = safeApiCall {
        api.requestEnterParking(tokenAccess, gateId, isInsurance)
    }

    suspend fun requestExitParking(tokenAccess: String, officerId: String) = safeApiCall {
        api.requestExitParking(tokenAccess, officerId)
    }

    suspend fun requestResetPassword(email:String) = safeApiCall {
        api.requestResetPassword(email)
    }

    suspend fun resetPassword(email: String, token: String, password: String, passwordC: String) = safeApiCall {
        api.resetPassword(email, token, password, passwordC)
    }
}