package apps.eduraya.e_parking.data.repository

import apps.eduraya.e_parking.data.network.BaseApi
import apps.eduraya.e_parking.data.network.SafeApiCall

abstract class BaseRepository (private val api: BaseApi): SafeApiCall{
    suspend fun logout() = safeApiCall {
        api.logout()
    }
}